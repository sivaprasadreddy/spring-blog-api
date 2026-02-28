# REQ_007 - Create Post (Authenticated)

## Requirement Description
Allow authenticated users to create a new blog post.

## Preconditions
- Valid Bearer JWT is provided.
- Authenticated principal includes user id.
- Payload contains non-empty `title`, `slug`, `content`.
- `slug` must be unique.

## Postconditions
- New post is persisted with `createdBy` set to current user id.
- Post published event is emitted.
- Support email notification is sent by event listener.

## API
- HTTP Method: `POST`
- URL: `/api/posts`
- Headers:
  - `Authorization: Bearer <jwt>`
  - `Content-Type: application/json`

## Request Body
```json
{
  "title": "Post Title",
  "slug": "post-slug",
  "content": "Post content"
}
```

## Response Body
- Status: `201 Created`
- Headers:
  - `Location: /api/posts/{slug}`
- No response body

## Error Responses
- `401 Unauthorized` (missing/invalid JWT)
- `400 Bad Request` (validation or duplicate slug)
  - Title: `Validation Error` for bean validation issues
  - Title: `Bad Request` for duplicate slug
- `500 Internal Server Error`
