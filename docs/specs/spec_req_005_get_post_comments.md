# REQ_005 - Get Post Comments

## Requirement Description
Get all comments for a post identified by slug.

## Preconditions
- Post with provided slug exists.

## Postconditions
- Returns list of comments for the target post.

## API
- HTTP Method: `GET`
- URL: `/api/posts/{slug}/comments`
- Headers:
  - `Accept: application/json`

## Request Body
- None

## Response Body
- Status: `200 OK`
```json
[
  {
    "id": 2,
    "name": "Test",
    "email": "test@gmail.com",
    "content": "sample comment 2",
    "createdAt": "2026-02-28T10:00:00",
    "updatedAt": null
  }
]
```

## Error Responses
- `404 Not Found`
  - Title: `Resource Not Found`
  - Detail example: `Post with slug 'missing-post-slug' not found`
- `500 Internal Server Error`
