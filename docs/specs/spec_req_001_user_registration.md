# REQ_001 - User Registration

## Requirement Description
Create a new user account with basic profile and credentials.

## Preconditions
- Request payload contains non-blank `name`, valid `email`, and non-blank `password`.
- Email must be unique in persistence layer.

## Postconditions
- A new user record is persisted.
- Password is stored in encoded form.
- User role is set to `ROLE_USER`.

## API
- HTTP Method: `POST`
- URL: `/api/users`
- Headers:
  - `Content-Type: application/json`

## Request Body
```json
{
  "name": "User123",
  "email": "user123@gmail.com",
  "password": "secret"
}
```

## Response Body
- Status: `201 Created`
```json
{
  "name": "User123",
  "email": "user123@gmail.com",
  "role": "ROLE_USER"
}
```

## Error Responses
- `400 Bad Request` (validation failure)
  - Title: `Validation Error`
  - `errors` includes messages like:
    - `Name is required`
    - `Invalid email address`
    - `Password is required`
- `500 Internal Server Error` (e.g., persistence constraint/unhandled errors)
