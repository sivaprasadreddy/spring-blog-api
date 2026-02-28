# REQ_008 - Update Post (Authenticated)

## Requirement Description
Update an existing post by slug, including optional slug change.

## Preconditions
- Valid Bearer JWT is provided.
- Source post (`{slug}`) exists.
- Payload contains non-empty `title`, `slug`, `content`.
- If slug is changed, new slug must not already belong to a different post.

## Postconditions
- Existing post data is updated.
- Response `Location` points to final slug resource.

## API
- HTTP Method: `PUT`
- URL: `/api/posts/{slug}`
- Headers:
  - `Authorization: Bearer <jwt>`
  - `Content-Type: application/json`

## Request Body
```json
{
  "title": "Installing LinuxMint OS",
  "slug": "installing-linuxmint-os",
  "content": "Installing LinuxMint 22"
}
```

## Response Body
- Status: `200 OK`
- Headers:
  - `Location: /api/posts/{updatedSlug}`
- No response body

## Error Responses
- `401 Unauthorized` (missing/invalid JWT)
- `404 Not Found`
  - Title: `Resource Not Found`
  - Detail example: `Post with slug 'missing-post-slug' not found`
- `400 Bad Request`
  - Title: `Validation Error` for bean validation issues
  - Title: `Bad Request` when updated slug already exists on another post
- `500 Internal Server Error`
