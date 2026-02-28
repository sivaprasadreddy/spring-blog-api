# Project Overview

## Application
The `spring-blog-api` is a modular Spring Boot REST API for a blog platform.

## Features
- User registration (`POST /api/users`)
- JWT-based login (`POST /api/login`)
- List posts with pagination (`GET /api/posts?page=`)
- Search posts by query (`GET /api/posts?query=`)
- Get details of a post by slug (`GET /api/posts/{slug}`)
- Get comments for a post (`GET /api/posts/{slug}/comments`)
- Add a comment to a post (`POST /api/posts/{slug}/comments`)
- Create a new post (authenticated) (`POST /api/posts`)
- Update an existing post (authenticated) (`PUT /api/posts/{slug}`)
- Event-driven email notification when a post is published
- Scheduled weekly newsletter email job

## Actors
- Guest/Anonymous user:
  - Can browse posts and comments
  - Can add comments
  - Can register and login
- Authenticated user (ROLE_USER):
  - Can create and update posts
- Admin user (ROLE_ADMIN):
  - Inherits user capabilities through role hierarchy (`ROLE_ADMIN > ROLE_USER`)
- System scheduler:
  - Runs a weekly newsletter job and sends digest emails
