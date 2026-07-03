# Spring Boot Best Practices

A collection of conventions and patterns for building well-structured Spring Boot applications.

---

## Package Structure

Organize packages by **feature module**, not by layer. Each module contains its own `api` and `domain` sub-packages:

```
com.example.myapp
├── orders/
│   ├── api/                  ← Controllers, request/response records
│   ├── domain/               ← Entities, repositories, services, mappers
│   │   └── models/           ← DTOs and command objects
│   └── OrdersAPI.java        ← Public facade for cross-module access
├── customers/
│   ├── api/
│   ├── domain/
│   │   └── models/
│   └── CustomersAPI.java
├── notifications/
├── jobs/
├── shared/
│   ├── entities/             ← Shared base entities (e.g., BaseEntity)
│   ├── exceptions/           ← Shared exception types
│   └── models/               ← Shared models (e.g., PagedResult)
└── config/                   ← Application-wide configuration
```

**Rules:**
- Group by feature, not by layer — no top-level `controllers/`, `services/`, or `repositories/` packages.
- Cross-module access goes through a public `*API` facade class, not directly into another module's `domain` package.
- Shared utilities and base types live in the `shared` package.

---

## Visibility Modifiers

Default to **minimum necessary visibility**. Only expose what other modules or layers genuinely need.

| Component                | Class                       | Constructor     | Methods         |
|--------------------------|-----------------------------|-----------------|-----------------|
| Controller               | package-private             | package-private | package-private |
| Service                  | `public`                    | package-private | `public`        |
| Repository               | package-private (interface) | —               | —               |
| Entity                   | package-private             | protected       | `public`        |
| DTO / record             | `public` or package-private | —               | —               |
| Module API facade        | `public`                    | package-private | `public`        |
| Request/Response records | package-private             | —               | —               |
| Config/Exception handler | package-private             | —               | —               |

**Examples:**

```java
// Controller — package-private; not used outside its own api package
class OrderController {
    OrderController(OrderService orderService) { ... }
    PagedResult<OrderDto> findOrders(...) { ... }
}

// Service — public class with package-private constructor (Spring injects via DI)
@Service
public class OrderService {
    OrderService(OrderRepository repo, OrderMapper mapper) { ... }
    public PagedResult<OrderDto> findOrders(int pageNo) { ... }
}

// Module facade — public; this is the only entry point for other modules
@Component
public class OrdersAPI {
    OrdersAPI(OrderService orderService) { ... }
    public List<OrderDto> findOrdersCreatedBetween(LocalDate from, LocalDate to) { ... }
}

// Utility class — public with private constructor to prevent instantiation
public final class SecurityUtils {
    private SecurityUtils() {}
    public static Long getCurrentUserIdOrThrow() { ... }
}
```

**Why:** Restricting visibility keeps implementation details internal and makes the module's public contract explicit. Package-private controllers, entities, and repositories cannot be accidentally used from outside the module.

---

## JPA Entities

Entities live in `{module}/domain/` and are **package-private**. They are never exposed outside their own module.

```java
@Entity
@Table(name = "orders")
class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_id_generator")
    @SequenceGenerator(name = "order_id_generator", sequenceName = "order_id_seq")
    private Long id;

    @Column(name = "reference", nullable = false, unique = true, length = 50)
    private String reference;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;  // FK stored as a plain Long — avoids cross-module @ManyToOne
}
```

**BaseEntity** — a shared mapped superclass for audit fields, used by all entities:

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @CreatedDate
    protected LocalDateTime createdAt;

    @LastModifiedDate
    protected LocalDateTime updatedAt;

    @Version
    protected Long version;
}
```

**Rules:**
- Entities are only used in the `domain` layer (repository + service). Controllers never receive or return entities.
- Store cross-module foreign keys as plain `Long` fields rather than `@ManyToOne` associations to avoid unintended lazy-loading, N+1 queries, and module coupling.
- Enable optimistic locking via `@Version` on `BaseEntity`.

---

## DTOs (Data Transfer Objects)

DTOs are **immutable records** and live in `{module}/domain/models/`. They represent the data contract between the service layer and its callers.

```java
// Domain DTO — returned by services and repositories
public record OrderDto(
        Long id,
        String reference,
        String status,
        Long customerId,
        String customerName,   // denormalized — avoids an extra query at the call site
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}
```

**Rules:**
- Use records — immutable by default, no boilerplate.
- DTOs may carry denormalized fields (e.g., `customerName` alongside `customerId`) to avoid N+1 queries at the call site.
- DTOs must not reference JPA entities or any internal class from another module.
- Omit sensitive fields (e.g., passwords, secrets) from general-purpose DTOs. Provide a separate DTO variant if an internal use case requires them.

---

## Entity-to-DTO Conversion

Use one of two strategies depending on complexity. Conversion always happens in the **repository query** or **service layer** — never in a controller.

### 1. JPQL Constructor Expression (preferred for joins)

When the DTO requires data from more than one table, construct it directly in the JPQL query:

```java
// OrderRepository
@Query("""
    select new com.example.myapp.orders.domain.models.OrderDto(
        o.id, o.reference, o.status, o.customerId, c.name, o.createdAt, o.updatedAt)
    from Order o join Customer c on o.customerId = c.id
    where o.reference = :reference
""")
Optional<OrderDto> findByReference(@Param("reference") String reference);
```

Single query, no N+1 risk, joins resolved at the database level.

### 2. MapStruct Mapper (preferred for simple mappings)

For straightforward entity → DTO mappings without joins, use a MapStruct mapper declared as a package-private interface in the `domain` layer:

```java
@Mapper(componentModel = "spring")
interface OrderMapper {
    OrderDto toDto(Order order);
}

// Example with field exclusion
@Mapper(componentModel = "spring")
interface CustomerMapper {
    @Mapping(target = "password", ignore = true)
    CustomerDto toDto(Customer entity);          // general use — password excluded

    CustomerDto toDtoWithPassword(Customer entity); // internal auth use only
}
```

Usage in a service:

```java
public List<OrderDto> findOrdersByCustomer(Long customerId) {
    return orderRepository.findByCustomerId(customerId)
            .stream()
            .map(orderMapper::toDto)
            .toList();
}
```

---

## HTTP Request / Response Modeling

### Request Payloads

Model incoming JSON request bodies as **package-private records** with Jakarta Validation annotations, placed in the `api` package:

```java
// orders/api/CreateOrderRequest.java
record CreateOrderRequest(
        @NotBlank(message = "Reference is required") String reference,
        @NotNull(message = "Customer ID is required") Long customerId,
        @NotEmpty(message = "At least one item is required") List<OrderItemRequest> items) {}
```

### Command Objects

Controllers translate request records into **command objects** before calling the service. Commands live in `domain/models/` and contain only validated, clean domain data:

```java
// Controller
ResponseEntity<Void> createOrder(@Valid @RequestBody CreateOrderRequest request) {
    Long userId = SecurityUtils.getCurrentUserIdOrThrow();
    var cmd = new CreateOrderCmd(request.reference(), request.customerId(), request.items(), userId);
    orderService.createOrder(cmd);
    URI location = URI.create("/api/orders/" + cmd.reference());
    return ResponseEntity.created(location).build();
}

// Command record — lives in domain/models/
public record CreateOrderCmd(String reference, Long customerId, List<OrderItemCmd> items, Long createdBy) {}
```

**Why the two-step conversion?** Request records belong to the `api` package and carry HTTP-specific concerns (validation annotations). Command objects belong to `domain` and carry only what the service needs — keeping the service layer free of HTTP dependencies.

### Response Bodies

Services return domain DTOs. Controllers return them directly or wrapped in `ResponseEntity`:

```java
// List with pagination
@GetMapping("")
PagedResult<OrderDto> findOrders(@RequestParam(defaultValue = "1") int page) {
    return orderService.findOrders(page);
}

// Single resource
@GetMapping("/{reference}")
ResponseEntity<OrderDto> getOrder(@PathVariable String reference) {
    return orderService.findByReference(reference)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + reference));
}

// Creation — 201 Created with Location header, no body
@PostMapping("")
ResponseEntity<Void> createOrder(@Valid @RequestBody CreateOrderRequest request) {
    ...
    return ResponseEntity.created(location).build();
}
```

### Error Responses

Handle all exceptions centrally via a `GlobalExceptionHandler` using Spring's RFC 7807 `ProblemDetail`:

```java
@RestControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail handle(ResourceNotFoundException e) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(NOT_FOUND, e.getMessage());
        pd.setTitle("Resource Not Found");
        pd.setProperty("errors", List.of(e.getMessage()));
        return pd;
    }

    @ExceptionHandler(BadRequestException.class)
    ProblemDetail handle(BadRequestException e) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
        pd.setTitle("Bad Request");
        pd.setProperty("errors", List.of(e.getMessage()));
        return pd;
    }
}
```

**Example error response:**
```json
{
  "type": "about:blank",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Order not found: ORD-999",
  "errors": ["Order not found: ORD-999"]
}
```

---

## Tests

### Test Stack

| Tool             | Purpose                                        |
|------------------|------------------------------------------------|
| JUnit 5          | Test runner                                    |
| AssertJ          | Fluent assertions                              |
| Spring Boot Test | `@SpringBootTest`, `MockMvc`, `RestTestClient` |
| Testcontainers   | Real database and service containers           |
| ArchUnit         | Architecture rule enforcement                  |
| Spring Modulith  | Module boundary validation                     |

### Base Integration Test Class

All integration tests extend a shared `AbstractIT` base class. This starts the full application context once and reuses it across all tests:

```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureRestTestClient
@Import(TestcontainersConfig.class)
@ActiveProfiles("test")
@Sql("/test-data.sql")    // reload fixture data before each test class
public abstract class AbstractIT {
    @Autowired protected RestTestClient restTestClient;
    @Autowired protected MockMvcTester mvc;
}
```

### Test Data Setup

Load test data from a SQL file via `@Sql`. The SQL file should:
1. Insert a known set of entities with **fixed IDs** so tests can reference predictable data.
2. Reset sequences to a high value so records created during tests do not collide with seed IDs.

```sql
-- Seed data with fixed IDs
insert into customers(id, name, email, created_at) values
(1, 'Alice', 'alice@example.com', now()),
(2, 'Bob',   'bob@example.com',   now());

insert into orders(id, reference, status, customer_id, created_at) values
(1, 'ORD-001', 'PLACED', 1, now()),
(2, 'ORD-002', 'SHIPPED', 2, now());

-- Avoid ID collisions with records inserted during tests
alter sequence customer_id_seq restart with 101;
alter sequence order_id_seq    restart with 101;
```

### Testcontainers Configuration

Declare containers once in a shared `@TestConfiguration` class. Use `@ServiceConnection` to wire them automatically — no manual property overrides needed:

```java
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {
    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgres() {
        return new PostgreSQLContainer<>("postgres:17-alpine");
    }

    // Add other containers (Redis, Kafka, etc.) as needed
    @Bean
    @ServiceConnection
    GenericContainer<?> redis() {
        return new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);
    }
}
```

### Two Complementary Testing Styles

Write tests using both `RestTestClient` and `MockMvcTester`. Both share the same `AbstractIT` base.

**RestTestClient** — preferred for asserting response body content:

```java
class OrderControllerTests extends AbstractIT {

    @Test
    void shouldReturnOrdersWithPagination() {
        var result = restTestClient.get()
                .uri("/api/orders")
                .exchange()
                .expectStatus().isOk()
                .returnResult(new ParameterizedTypeReference<PagedResult<OrderDto>>() {})
                .getResponseBody();

        assertThat(result.data()).hasSize(2);
        assertThat(result.totalPages()).isEqualTo(1);
    }

    @Test
    void shouldCreateOrder() {
        restTestClient.post()
                .uri("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        { "reference": "ORD-NEW", "customerId": 1, "items": [] }
                        """)
                .exchange()
                .expectStatus().isCreated();
    }
}
```

**MockMvcTester** — preferred for asserting headers, redirects, and status codes:

```java
class OrderControllerMockMvcTests extends AbstractIT {

    @Test
    void shouldReturnLocationHeaderOnCreation() {
        mvc.post()
                .uri("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "reference": "ORD-NEW", "customerId": 1, "items": [] }
                        """)
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.CREATED)
                .redirectedUrl()
                .endsWith("/api/orders/ORD-NEW");
    }

    @Test
    void shouldReturn400WhenReferenceIsMissing() {
        mvc.post()
                .uri("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "customerId": 1 }
                        """)
                .exchange()
                .assertThat()
                .hasStatus(HttpStatus.BAD_REQUEST);
    }
}
```

### Architecture Tests

Use ArchUnit to codify package conventions and Spring Modulith to enforce module boundaries:

```java
// Enforce that controllers do not directly use repositories
class ArchitectureTests {
    @Test
    void controllersShouldNotDependOnRepositories() {
        noClasses()
            .that().haveNameMatching(".*Controller")
            .should().dependOnClassesThat().haveNameMatching(".*Repository")
            .check(importedClasses);
    }
}

// Validate module boundaries using Spring Modulith
class ModularityTests {
    @Test
    void modulesShouldBeCompliant() {
        ApplicationModules.of(MyApplication.class).verify();
    }
}
```

---

## Configuration Classes

Configuration classes live in the `config` package at the root of the application. They are **package-private** — they configure Spring beans but are never referenced directly from other code.

**Rules:**
- Annotate with `@Configuration`. Add feature-enabling annotations (`@EnableAsync`, `@EnableJpaAuditing`, etc.) on their own dedicated, single-purpose config class rather than piling them onto one class.
- Keep config classes package-private. Bean methods can also be package-private — Spring discovers them through reflection.
- Use constructor injection (or method parameters on `@Bean` methods) rather than field injection.
- Avoid putting unrelated beans in the same config class. Prefer one config class per concern.

**Example:**

```java
// config/AsyncConfig.java — single responsibility
@Configuration
@EnableAsync
class AsyncConfig {}

// config/PersistenceConfig.java — single responsibility
@Configuration
@EnableJpaAuditing
class PersistenceConfig {}

// config/WebSecurityConfig.java — security chain only
@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**");
        http.csrf(CsrfConfigurer::disable);
        http.sessionManagement(s -> s.sessionCreationPolicy(STATELESS));
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .anyRequest().authenticated());
        http.oauth2ResourceServer(c -> c.jwt(Customizer.withDefaults()));
        http.exceptionHandling(c ->
                c.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
        return http.build();
    }
}

// config/OpenApiConfig.java — reads ApplicationProperties via @Bean method parameter
@Configuration
class OpenApiConfig {

    @Bean
    OpenAPI openApi(ApplicationProperties props) {
        var api = props.openApi();
        return new OpenAPI()
                .info(new Info().title(api.title()).version(api.version()));
    }
}
```

---

## Configuration Properties

Bind all custom application properties to a **`@ConfigurationProperties` record** rather than scattering `@Value` annotations across classes. Place it at the root application package so it is visible to all modules.

```java
// ApplicationProperties.java
@ConfigurationProperties(prefix = "app")
@Validated
public record ApplicationProperties(
        @DefaultValue("10") int pageSize,
        @Valid JwtProperties jwt,
        @Valid CorsProperties cors) {

    public record JwtProperties(
            @NotEmpty String issuer,
            @NotNull Long expiresInSeconds,
            @NotNull RSAPublicKey publicKey,
            @NotNull RSAPrivateKey privateKey) {}

    public record CorsProperties(
            @DefaultValue("/api/**") String pathPattern,
            @DefaultValue("*") String allowedOrigins,
            @DefaultValue("*") String allowedMethods) {}
}
```

Enable it in the main application class:

```java
@SpringBootApplication
@ConfigurationPropertiesScan
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

Inject `ApplicationProperties` as a constructor parameter wherever the properties are needed:

```java
@Service
public class OrderService {
    private final ApplicationProperties properties;

    OrderService(OrderRepository repo, ApplicationProperties properties) {
        this.repo = repo;
        this.properties = properties;
    }

    public PagedResult<OrderDto> findOrders(int pageNo) {
        var pageable = PageRequest.of(pageNo - 1, properties.pageSize());
        ...
    }
}
```

**Rules:**
- Use `@Validated` + Jakarta Validation constraints (`@NotEmpty`, `@NotNull`) on the record to fail fast at startup when required properties are missing.
- Use `@DefaultValue` for optional properties with sensible defaults.
- Use nested records to group related properties (jwt, cors, mail, etc.).
- Never use `@Value` for application-specific settings — use `@ConfigurationProperties` records. Reserve `@Value` only for simple Spring/infrastructure property references where a full record would be overkill.
- Corresponding properties file:

```properties
app.page-size=10
app.jwt.issuer=MyApp
app.jwt.expires-in-seconds=604800
app.jwt.public-key=classpath:certs/public.pem
app.jwt.private-key=classpath:certs/private.pem
app.cors.path-pattern=/api/**
app.cors.allowed-origins=https://myapp.example.com
```

---

## Accessing the Authenticated User

In JWT-secured APIs, extract the current user from the `SecurityContext` in a dedicated utility class rather than repeating the extraction logic in every controller.

```java
// auth/SecurityUtils.java
public final class SecurityUtils {
    private SecurityUtils() {}

    public static Long getCurrentUserIdOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new AccessDeniedException("Access denied");
        }
        if (auth.getPrincipal() instanceof Jwt jwt) {
            Long userId = jwt.getClaim("user_id");
            if (userId != null) return userId;
        }
        throw new AccessDeniedException("Access denied");
    }
}
```

Usage in a controller:

```java
@PostMapping("")
ResponseEntity<Void> createOrder(@Valid @RequestBody CreateOrderRequest request) {
    Long userId = SecurityUtils.getCurrentUserIdOrThrow();
    var cmd = new CreateOrderCmd(request.reference(), userId);
    orderService.createOrder(cmd);
    ...
}
```

**For Spring MVC with session-based auth**, prefer injecting the principal via a method parameter instead:

```java
@GetMapping("/me/orders")
List<OrderDto> myOrders(@AuthenticationPrincipal UserDetails user) {
    return orderService.findOrdersByUser(user.getUsername());
}
```

**Rules:**
- Extract auth-related boilerplate into a utility class or resolved argument — never duplicate it across controllers.
- Pass only the resolved user ID (or username) to the service layer. Services must not access `SecurityContextHolder` directly; keep security concerns in the `api` and `auth` packages.
- Services receive the user identity as part of a command object, not via a thread-local.

---

## Profile-Specific Properties Files

Use Spring's profile mechanism to override properties per environment without modifying the base configuration.

**File naming convention:**

```
src/main/resources/
├── application.properties          ← base config, applies to all environments
├── application-local.properties    ← local dev overrides (docker compose, verbose logging)
└── application-prod.properties     ← production overrides (if not using env vars)

src/test/resources/
└── application-test.properties     ← test-specific overrides
```

**`application.properties`** — defaults and required structure:

```properties
spring.application.name=my-app
server.port=8080

# Placeholders resolved from env vars at runtime; local defaults for convenience
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/mydb}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}

# Expose only safe actuator endpoints in the base config
management.endpoints.web.exposure.include=info,health
management.endpoint.health.probes.enabled=true

# Disable open-session-in-view to avoid lazy-loading surprises
spring.jpa.open-in-view=false
spring.jpa.hibernate.ddl-auto=validate
```

**`application-local.properties`** — local developer overrides:

```properties
# Expose all actuator endpoints locally for debugging
management.endpoints.web.exposure.include=*
logging.level.org.springframework.security=DEBUG
```

**`application-test.properties`** — test environment overrides:

```properties
# Use immediate shutdown to speed up test teardown
server.shutdown=immediate
logging.level.org.springframework.security=DEBUG
```

**Activating profiles:**

```bash
# IDE / local run
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Production
java -jar app.jar --spring.profiles.active=prod

# Tests — set on the base test class
@ActiveProfiles("test")
public abstract class AbstractIT { ... }
```

**Rules:**
- The base `application.properties` must work without any profile active (using reasonable defaults or environment variable placeholders).
- Never commit secrets (passwords, API keys) to any properties file. Use environment variables or a secrets manager in production.
- `local` profile is for developer convenience only — it is never active in CI or production.
- `test` profile is activated via `@ActiveProfiles("test")` on the `AbstractIT` base class; it should only contain overrides that make the test suite faster or more deterministic.
- Prefer environment variable placeholders (`${DB_URL:default}`) over hard-coded values in the base config to make the app 12-factor friendly.

---

## Summary

| Concern                  | Decision                                                                    |
|--------------------------|-----------------------------------------------------------------------------|
| Package structure        | Feature-module packages; no top-level layer packages                        |
| Cross-module access      | Through `public *API` facade classes only                                   |
| Default visibility       | Package-private; only services and module APIs are `public`                 |
| JPA entities             | Package-private; used only inside the `domain` layer                        |
| DTOs                     | Immutable records; live in `{module}/domain/models/`                        |
| Conversion layer         | Repository (JPQL constructor) or service (MapStruct); never controller      |
| Request modeling         | Package-private records with Jakarta Validation in the `api` package        |
| Command objects          | Intermediate records; created in controller, consumed by service            |
| Response modeling        | Domain DTOs returned directly or wrapped in `ResponseEntity`/`PagedResult`  |
| Error handling           | `GlobalExceptionHandler` with RFC 7807 `ProblemDetail`                      |
| Configuration classes    | Package-private; one class per concern; no `@Value` for app properties      |
| Configuration properties | `@ConfigurationProperties` record with `@Validated`; injected as a bean     |
| Authenticated user       | `SecurityUtils` utility class; pass user ID in command objects to service   |
| Profile properties       | `application-{profile}.properties`; `local` for dev, `test` for tests       |
| Test data                | `@Sql` with fixed IDs and sequence resets to avoid collision                |
| Containers               | Testcontainers with `@ServiceConnection` in a shared `TestcontainersConfig` |
| Test style               | `RestTestClient` + `MockMvcTester`; shared `AbstractIT` base class          |
