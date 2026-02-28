# REQ_004 - Get Post Details

## Requirement Description
Fetch a single post by unique slug.

## Preconditions
- Post with provided `slug` exists.

## Postconditions
- Returns the matching post.

## API
- HTTP Method: `GET`
- URL: `/api/posts/{slug}`
- Headers:
  - `Accept: application/json`

## Request Body
- None

## Response Body
- Status: `200 OK`
```json
{
  "id": 2,
  "title": "SpringBoot: Introducing SpringBoot",
  "slug": "introducing-springboot",
  "content": "...",
  "createdBy": 2,
  "createdAt": "2014-06-20T00:00:00",
  "updatedAt": null
}
```

## Error Responses
- `404 Not Found`
  - Title: `Resource Not Found`
  - Detail example: `Post with slug 'missing-post-slug' not found`
- `500 Internal Server Error`
