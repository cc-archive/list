DROP TABLE IF EXISTS UserPhotosBlocks;
DROP TABLE IF EXISTS UserUserBlocks;
DROP TABLE IF EXISTS AdminPhotoBlocks;
DROP TABLE IF EXISTS AdminUserBlocks;

-- Photos an individual user doesn't want to see.
-- If they have three with the same userid, don't show anything by that user.

CREATE TABLE UserPhotoBlocks (
id int(11) NOT NULL auto_increment,
userid int(11) NOT NULL,
photoid int(11) NOT NULL,
primary KEY (id)
);

-- Users the user doesn't want anything to do with - don't show any photos by
-- the blocked user to them or allow any interaction.
-- The "isblocked" value can be used by the user to override any algorithmic
-- blocks (e.g. if users are hidden after 10 blocked photos, this can
-- override that).

CREATE TABLE UserUserBlocks (
id int(11) NOT NULL auto_increment,
userid int(11) NOT NULL,
blockeduserid int(11) NOT NULL,
isblocked boolean NOT NULL DEFAULT true,
primary KEY (id)
);

-- A global block of a photo by an admin.
-- Note that reason can be null if it's obvious why.

CREATE TABLE AdminPhotoBlocks (
id int(11) NOT NULL auto_increment,
adminuserid int(11) NOT NULL,
photoid int(11) NOT NULL,
reason varchar(1024),
primary KEY (id)
);

-- A global block of a user by an admin.
-- The user cannot log in and their photos will not be displayed on the site.
-- Their blocks will also not count for evaluating global blocks on
-- photos/users.

CREATE TABLE AdminUserBlocks (
id int(11) NOT NULL auto_increment,
userid int(11) NOT NULL,
blockeduserid int(11) NOT NULL,
reason varchar(1024) NOT NULL,
primary KEY (id)
);

