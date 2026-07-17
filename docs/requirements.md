# Requirements

- REQ_001 - User Registration:
  Create a new user account with name, email, and password. New users are assigned `ROLE_USER`.

- REQ_002 - User Login:
  Authenticate a user with email/password and return a JWT token plus user details.

- REQ_003 - List Categories:
  Fetch the list of all categories.

- REQ_004 - Get Category Details:
  Retrieve a single category using its slug. Returns 404 if not found.

- REQ_005 - Create Category (Authenticated):
  Allow authenticated users to create a new category with name and unique slug.

- REQ_006 - Update Category (Authenticated):
  Allow authenticated users to update an existing category and optionally change slug (must remain unique).

- REQ_007 - Delete Category (Authenticated):
  Allow authenticated users to delete a category. Deletion is rejected if the category is still associated with one or more posts.

- REQ_008 - List/Search Posts:
  Fetch blog posts with pagination and optional keyword search using `query`.

- REQ_009 - Get Post Details:
  Retrieve a single post using its slug.

- REQ_010 - Get Post Comments:
  Retrieve comments associated with a post slug.

- REQ_011 - Create Comment:
  Add a comment to an existing post using commenter name, email, and content.

- REQ_012 - Create Post (Authenticated):
  Allow authenticated users to create a new post with unique slug.

- REQ_013 - Update Post (Authenticated):
  Allow authenticated users to update an existing post and optionally change slug (must remain unique).
  A post may optionally be associated with a category via `categorySlug`.
