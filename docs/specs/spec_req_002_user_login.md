# REQ_002 - User Login

## Requirement Description
Authenticate user credentials and return JWT token and user identity details.

## Preconditions
- User exists with matching email/password.
- Payload contains valid email and non-empty password.

## Postconditions
- JWT access token is generated.
- Token expiration timestamp is returned.

## API
- HTTP Method: `POST`
- URL: `/api/login`
- Headers:
  - `Content-Type: application/json`

## Request Body
```json
{
  "email": "siva@gmail.com",
  "password": "siva"
}
```

## Response Body
- Status: `200 OK`
```json
{
  "token": "<jwt>",
  "expiresAt": "2026-03-06T12:00:00Z",
  "userId": 2,
  "name": "Siva Prasad",
  "email": "siva@gmail.com",
  "role": "ROLE_USER"
}
```

## Error Responses
- `401 Unauthorized` (invalid credentials)
  - Title: `Unauthorized`
- `400 Bad Request` (validation failure)
  - Title: `Validation Error`
  - `errors` includes:
    - `Invalid email address`
    - `Password is required`
