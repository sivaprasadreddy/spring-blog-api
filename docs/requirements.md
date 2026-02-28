# Requirements

- REQ_001 - User Registration:
  Create a new user account with name, email, and password. New users are assigned `ROLE_USER`.

- REQ_002 - User Login:
  Authenticate a user with email/password and return a JWT token plus user details.

- REQ_003 - List/Search Posts:
  Fetch blog posts with pagination and optional keyword search using `query`.

- REQ_004 - Get Post Details:
  Retrieve a single post using its slug.

- REQ_005 - Get Post Comments:
  Retrieve comments associated with a post slug.

- REQ_006 - Create Comment:
  Add a comment to an existing post using commenter name, email, and content.

- REQ_007 - Create Post (Authenticated):
  Allow authenticated users to create a new post with unique slug.

- REQ_008 - Update Post (Authenticated):
  Allow authenticated users to update an existing post and optionally change slug (must remain unique).
