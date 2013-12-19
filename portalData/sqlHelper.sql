-- CREATE TABLE 'PortalData' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'guid' TEXT NOT NULL, 'title' TEXT NOT NULL, 'imageUrl' TEXT, 'imageDownloaded' INTEGER NOT NULL default 0 , 'lat' REAL NOT NULL default '0', 'lng' REAL NOT NULL default '0');


-- CREATE TABLE 'PortalData'
-- ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
-- 'guid' TEXT NOT NULL UNIQUE ,
-- 'title' TEXT NOT NULL,
-- 'imageUrl' TEXT,
-- 'imageDownloaded' INTEGER NOT NULL default 0 ,
-- 'lat' REAL NOT NULL default '0', 'lng' REAL NOT NULL default '0');


-- CREATE UNIQUE INDEX portalData_idx ON portalData(guid);


CREATE TABLE 'PortalData'
(
'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
'guid' TEXT NOT NULL UNIQUE ,
'title' TEXT NOT NULL,
'imageUrl' TEXT,
'imageDownloaded' INTEGER NOT NULL default 0 ,
'lat' REAL NOT NULL default '0',
'lng' REAL NOT NULL default '0',
'like' INTEGER NOT NULL default 0 ,
'target' INTEGER NOT NULL default 0 ,
'hasKey' INTEGER NOT NULL default 0
);




-- https://maps.google.com.br/maps?saddr=46,23&daddr=45,23+to:45,24



-- https://maps.google.com.br/maps?saddr=46,23&daddr=45,23+to:45,24



-- https://maps.google.com.br/maps?saddr=46,23&daddr=46,24+to:46,25+to:46,26&hl=pt&sll=45.46591,23.439331&sspn=1.687415,2.469177&geocode=FYDnvQIdwPNeAQ%3BFYDnvQIdADZuAQ%3BFYDnvQIdQHh9AQ%3BFYDnvQIdgLqMAQ&mra=ls&t=m&z=8


-- https://maps.google.com.br/maps?saddr=46,23&daddr=46,24+to:46,25+to:46,26