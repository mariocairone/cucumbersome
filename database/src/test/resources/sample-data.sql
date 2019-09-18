CREATE TABLE IF NOT EXISTS models (
	"id" int4 NOT NULL,
 	"created" timestamp,	
	"email"  varchar(255),
	"fullname"  varchar(255),
	"modified"  timestamp,
	"password"  varchar(255),	
	PRIMARY KEY ("id")
);

TRUNCATE TABLE models;

INSERT INTO models
	("id", created, email, fullname, modified, "password")
VALUES 
	(101,'2014-07-16 00:00:00','cchacin@superbiz.org','Carlos','2014-07-16 00:00:00','passWorD'),
	(102,'2014-07-16 00:00:00','cchacin2@superbiz.org','Carlos2','2014-07-16 00:00:00','passWorD2');