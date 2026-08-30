create index idx_comments_post_id on comments (post_id);

create index idx_posts_category_id on posts (category_id);

create index idx_posts_created_by on posts (created_by);

create index idx_posts_updated_by on posts (updated_by);
