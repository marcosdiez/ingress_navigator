#!/usr/bin/env python

import json
import sys
import os.path


filename = sys.argv[1]

print "Loading data from " + filename
file_content = open(filename, 'r').read()

portal_data = json.loads(file_content)



for portal in portal_data:
	if "guid" in portal:
		if not os.path.isfile(portal["guid"] + ".jpg"):
			print "wget {} -O {}.jpg".format(portal["imageUrl"], portal["guid"])

print "Done"