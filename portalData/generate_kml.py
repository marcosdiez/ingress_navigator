#!/usr/bin/env python
from __future__ import unicode_literals
import json
import sys
import os.path
import sqlite3
import codecs

def check_parameters():
	if len(sys.argv) < 2:
		print "Error: usage: {} TARGET_DB".format(sys.argv[0])
		sys.exit(0)


def print_placemark(row):
	output = """
	<Placemark>
		<name>{}</name>
		<description><![CDATA[
		<a href="http://maps.google.com/maps?daddr={},{}">Open Google Maps
		<img src="{}">
		</a>
		]]></description>
		<Point>
			<coordinates>{},{},0</coordinates>
		</Point>
	</Placemark>
	"""

	title = row[0].replace("&","")
	return output.format(title, row[2], row[3], row[1], row[3], row[2])


def load_data():
	db = sqlite3.connect(sys.argv[1])
	cur = db.cursor()

	query = "SELECT title, imageUrl, lat, lng from PortalData order by title;";
	cur.execute(query)
	rows = cur.fetchall()
	print "Begin..."



	file = codecs.open("output.kml", "w", "utf-8")
	file.write(u'\ufeff')


	file.write("""<?xml version="1.0" encoding="UTF-8"?>
<kml xmlns="http://www.opengis.net/kml/2.2">
	<Document>
		<Folder>
		<name>Placemarks</name>
		<open>0</open>
	""")
	for row in rows:
		file.write(print_placemark(row))

	db.close()
	file.write("""
			</Folder>
	</Document>
</kml>
""")
	file.close()

check_parameters()
load_data()
print "Done"

