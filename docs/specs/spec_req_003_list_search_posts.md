# REQ_003 - List/Search Posts

## Requirement Description
Retrieve paginated posts and optionally filter by search query.

## Preconditions
- None (public endpoint).
- `page` should be positive integer; values less than 1 are normalized to page 1.

## Postconditions
- Returns a page object with post list and pagination metadata.

## API
- HTTP Method: `GET`
- URL: `/api/posts`
- Query Parameters:
  - `query` (optional, default empty)
  - `page` (optional, default `1`)
- Headers:
  - `Accept: application/json`

## Request Body
- None

## Response Body
- Status: `200 OK`
```json
{
  "data": [
    {
      "id": 2,
      "title": "SpringBoot: Introducing SpringBoot",
      "slug": "introducing-springboot",
      "content": "...",
      "createdBy": 2,
      "createdAt": "2014-06-20T00:00:00",
      "updatedAt": null
    }
  ],
  "currentPageNo": 1,
  "totalPages": 2,
  "totalElements": 9,
  "hasNextPage": true,
  "hasPreviousPage": false
}
```

## Error Responses
- `500 Internal Server Error` (unexpected server errors)
