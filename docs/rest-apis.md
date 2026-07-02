# REST API Endpoints

## Base URL

```
http://localhost:8080
```

## Authentication

Protected endpoints require a Bearer token in the `Authorization` header:

```
Authorization: Bearer <token>
```

Obtain a token via the [Login](#post-apilogin) endpoint.

---

## Auth API

### POST /api/login

Authenticate a user and receive a JWT access token.

**Request Body**

```json
{
  "email": "user@example.com",
  "password": "secret"
}
```

| Field      | Type   | Required | Constraints          |
|------------|--------|----------|----------------------|
| `email`    | string | yes      | Valid email address  |
| `password` | string | yes      | Non-empty            |

**Responses**

`200 OK`

```json
{
  "token": "eyJhbGci...",
  "expiresAt": "2026-07-03T10:00:00Z",
  "userId": 1,
  "name": "John Doe",
  "email": "user@example.com",
  "role": "ROLE_USER"
}
```

`401 Unauthorized` — Invalid credentials.

---

## Users API

### POST /api/users

Register a new user account.

**Request Body**

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "secret"
}
```

| Field      | Type   | Required | Constraints         |
|------------|--------|----------|---------------------|
| `name`     | string | yes      | Non-blank           |
| `email`    | string | yes      | Valid email address |
| `password` | string | yes      | Non-blank           |

**Responses**

`201 Created`

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "role": "ROLE_USER"
}
```

---

## Posts API

### GET /api/posts

Retrieve a paginated list of posts. Optionally filter by a search query.

**Query Parameters**

| Parameter | Type   | Default | Description                    |
|-----------|--------|---------|--------------------------------|
| `query`   | string | `""`    | Full-text search term          |
| `page`    | int    | `1`     | Page number (1-based)          |

**Responses**

`200 OK`

```json
{
  "data": [
    {
      "id": 1,
      "title": "My First Post",
      "slug": "my-first-post",
      "content": "Hello world...",
      "authorId": 1,
      "authorName": "John Doe",
      "createdAt": "2026-01-01T10:00:00",
      "updatedAt": "2026-01-02T12:00:00"
    }
  ],
  "currentPageNo": 1,
  "totalPages": 5,
  "totalElements": 42,
  "hasNextPage": true,
  "hasPreviousPage": false
}
```

---

### GET /api/posts/{slug}

Retrieve a single post by its slug.

**Path Parameters**

| Parameter | Type   | Description   |
|-----------|--------|---------------|
| `slug`    | string | The post slug |

**Responses**

`200 OK` — Returns a [PostDto](#postdto).

`404 Not Found` — Post with the given slug does not exist.

---

### POST /api/posts

Create a new post.

**Authentication:** Required (Bearer token)

**Request Body**

```json
{
  "title": "My New Post",
  "slug": "my-new-post",
  "content": "Post content goes here..."
}
```

| Field     | Type   | Required | Constraints |
|-----------|--------|----------|-------------|
| `title`   | string | yes      | Non-empty   |
| `slug`    | string | yes      | Non-empty   |
| `content` | string | yes      | Non-empty   |

**Responses**

`201 Created` — `Location` header contains the URI of the new post (`/api/posts/{slug}`).

`401 Unauthorized` — Missing or invalid Bearer token.

---

### PUT /api/posts/{slug}

Update an existing post.

**Authentication:** Required (Bearer token)

**Path Parameters**

| Parameter | Type   | Description                |
|-----------|--------|----------------------------|
| `slug`    | string | Slug of the post to update |

**Request Body**

```json
{
  "title": "Updated Title",
  "slug": "updated-slug",
  "content": "Updated content..."
}
```

| Field     | Type   | Required | Constraints                                    |
|-----------|--------|----------|------------------------------------------------|
| `title`   | string | yes      | Non-empty                                      |
| `slug`    | string | yes      | Non-empty; must not conflict with another post |
| `content` | string | yes      | Non-empty                                      |

**Responses**

`200 OK` — `Location` header contains the URI of the updated post (`/api/posts/{slug}`).

`400 Bad Request` — The new slug is already used by a different post.

`401 Unauthorized` — Missing or invalid Bearer token.

`404 Not Found` — Post with the given slug does not exist.

---

### GET /api/posts/{slug}/comments

Retrieve all comments for a post.

**Path Parameters**

| Parameter | Type   | Description   |
|-----------|--------|---------------|
| `slug`    | string | The post slug |

**Responses**

`200 OK`

```json
[
  {
    "id": 1,
    "name": "Jane Doe",
    "email": "jane@example.com",
    "content": "Great post!",
    "createdAt": "2026-01-05T08:30:00",
    "updatedAt": "2026-01-05T08:30:00"
  }
]
```

`404 Not Found` — Post with the given slug does not exist.

---

### POST /api/posts/{slug}/comments

Add a comment to a post.

**Path Parameters**

| Parameter | Type   | Description   |
|-----------|--------|---------------|
| `slug`    | string | The post slug |

**Request Body**

```json
{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "content": "Great post!"
}
```

| Field     | Type   | Required | Constraints         |
|-----------|--------|----------|---------------------|
| `name`    | string | yes      | Non-empty           |
| `email`   | string | yes      | Valid email address |
| `content` | string | yes      | Non-empty           |

**Responses**

`201 Created`

`404 Not Found` — Post with the given slug does not exist.

---

## Schemas

### PostDto

| Field        | Type            | Description                  |
|--------------|-----------------|------------------------------|
| `id`         | long            | Post identifier              |
| `title`      | string          | Post title                   |
| `slug`       | string          | URL-friendly identifier      |
| `content`    | string          | Post body                    |
| `authorId`   | long            | ID of the author             |
| `authorName` | string          | Display name of the author   |
| `createdAt`  | datetime        | Creation timestamp           |
| `updatedAt`  | datetime        | Last update timestamp        |

### CommentDto

| Field       | Type     | Description           |
|-------------|----------|-----------------------|
| `id`        | long     | Comment identifier    |
| `name`      | string   | Commenter's name      |
| `email`     | string   | Commenter's email     |
| `content`   | string   | Comment body          |
| `createdAt` | datetime | Creation timestamp    |
| `updatedAt` | datetime | Last update timestamp |

### PagedResult

| Field             | Type    | Description                        |
|-------------------|---------|------------------------------------|
| `data`            | array   | List of items for the current page |
| `currentPageNo`   | int     | Current page number (1-based)      |
| `totalPages`      | int     | Total number of pages              |
| `totalElements`   | long    | Total number of items              |
| `hasNextPage`     | boolean | Whether a next page exists         |
| `hasPreviousPage` | boolean | Whether a previous page exists     |
