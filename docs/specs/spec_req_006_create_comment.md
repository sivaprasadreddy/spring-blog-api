# REQ_006 - Create Comment

## Requirement Description
Create a new comment for a given post slug.

## Preconditions
- Target post exists.
- Payload contains non-empty `name`, valid `email`, non-empty `content`.

## Postconditions
- Comment record is created and linked to target post.

## API
- HTTP Method: `POST`
- URL: `/api/posts/{slug}/comments`
- Headers:
  - `Content-Type: application/json`

## Request Body
```json
{
  "name": "Siva",
  "email": "siva@gmail.com",
  "content": "Test comment"
}
```

## Response Body
- Status: `201 Created`
- No response body

## Error Responses
- `400 Bad Request` (validation failure)
  - Title: `Validation Error`
  - `errors` includes messages like:
    - `Name is required`
    - `Invalid email address`
    - `Content is required`
- `404 Not Found`
  - Title: `Resource Not Found`
  - Detail example: `Post with slug 'missing-post-slug' not found`
- `500 Internal Server Error`
