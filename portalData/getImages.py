#!/usr/bin/env python

import json
import sys


filename = sys.argv[1]

print "Loading data from " + filename
file_content = open(filename, 'r').read()

portal_data = json.loads(file_content)

for portal in portal_data:
	print portal["title"]

print "Done"