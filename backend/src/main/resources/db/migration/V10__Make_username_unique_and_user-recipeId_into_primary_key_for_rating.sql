ALTER TABLE User MODIFY COLUMN username varchar(30) UNIQUE;
ALTER TABLE Rating DROP COLUMN id, ADD PRIMARY KEY(userId, recipeId);