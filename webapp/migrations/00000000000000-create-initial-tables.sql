drop table if exists Users;
drop table if exists UserList;
drop table if exists Photos;
drop table if exists UserSessions;
drop table if exists UserSuggestions;
drop table if exists HowToPopularity;

CREATE TABLE UserSessions (
id int(11) NOT NULL auto_increment,
skey VARCHAR(32) NOT NULL,
session_start TIMESTAMP,
userid int(11) NOT NULL,
primary KEY (id)
);

CREATE TABLE HowToPopularity (
id int(11) NOT NULL auto_increment,
listitem int(11) NOT NULL,
primary KEY (id)
);

CREATE TABLE Users (
email VARCHAR(255) NOT NULL,
makerid int(11),
id int(11) NOT NULL auto_increment,
primary KEY (id)
);

CREATE TABLE UserList (
id int(11) NOT NULL auto_increment,
userid int(11) NOT NULL,
listid int(11) NOT NULL,
complete int(11),
primary KEY(id)
);

CREATE TABLE Photos (
id int(11) NOT NULL auto_increment,
userid int(11) NOT NULL,
filename varchar(255) NOT NULL,
url varchar(255),
listitem int(11) NOT NULL,
primary KEY (id)
);

CREATE TABLE UserSuggestions (
id int(11) NOT NULL auto_increment,
userid int(11) NOT NULL,
filename varchar(255),
category int(11),
description varchar(255),
title varchar(255),
primary KEY (id)
);

drop table if exists List;
drop table if exists Makers;
drop table if exists Categories;
drop table if exists UserCategories;

CREATE TABLE Makers (
id int(11) NOT NULL auto_increment,
name VARCHAR(255) NOT NULL,
uri VARCHAR(255),
primary KEY (id)
);

CREATE TABLE Categories (
id int(11) NOT NULL auto_increment,
title VARCHAR(255) NOT NULL,
makerid int(11) NOT NULL,
approved int(11),
color VARCHAR(10) NOT NULL,
primary KEY (id)
);


CREATE TABLE UserCategories (
id int(11) NOT NULL auto_increment,
userid int(11) NOT NULL,
categoryid int(11) NOT NULL,
primary KEY (id)
);

CREATE TABLE List (
id int(11) NOT NULL auto_increment,
makerid int(11) NOT NULL,
title VARCHAR(255) NOT NULL,
description TEXT,
uri VARCHAR(255),
approved int(11),
category int(11),
primary KEY (id)
);

alter table Users add unique (email);
alter table Makers add unique (name);
alter table Makers add unique (uri);
alter table List add unique (uri);
