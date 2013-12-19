#!/usr/bin/env python

import json
import sys
import os.path
import sqlite3

def check_parameters():
	if len(sys.argv) == 1:
		print "Error: usage: {} JSONFILE".format(sys.argv[0])
		sys.exit(0)


def load_data():
	filename = sys.argv[1]
	print "Loading data from " + filename
	file_content = open(filename, 'r').read()
	portal_data = json.loads(file_content)
	print "Loaded {} portals".format(len(portal_data))
	return portal_data



def download_images():
	for portal in portal_data:
		if "guid" in portal:
			if not os.path.isfile(portal["guid"] + ".jpg"):
				print "wget {} -O {}.jpg".format(portal["imageUrl"], portal["guid"])


def populate_db(portal_data):
	print "Populationg DB"
	db = sqlite3.connect('portalData.sqlite3')
	c = db.cursor()


	# Larger example that inserts many records at a time
	purchases = [('2006-03-28', 'BUY', 'IBM', 1000, 45.00),
	             ('2006-04-05', 'BUY', 'MSFT', 1000, 72.00),
	             ('2006-04-06', 'SELL', 'IBM', 500, 53.00),
	            ]

	for portal in portal_data:
		if "guid" in portal:
			data = (portal["guid"], portal["title"], portal["imageUrl"], 1, portal["lat"], portal["lng"], 0, 0 , 0)
			c.execute('INSERT INTO PortalData ("guid","title","imageUrl","imageDownloaded","lat","lng","like","target","hasKey") VALUES (?,?,?,?,?,?,?,?,?)', data)
			db.commit()


	db.close()



check_parameters()
portal_data = load_data()
populate_db(portal_data)






print "Done"