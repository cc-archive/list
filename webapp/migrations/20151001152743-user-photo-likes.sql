DROP TABLE IF EXISTS UserPhotoLikes;

-- Photos an individual user has liked.

CREATE TABLE UserPhotoLikes (
id int(11) NOT NULL auto_increment,
userid int(11) NOT NULL,
photoid int(11) NOT NULL,
primary KEY (id)
);
