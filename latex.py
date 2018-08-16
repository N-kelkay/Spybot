import sys
import requests
import re

SITE = 'http://latex2png.com/'
payload = {
	'latex': ''.join(sys.argv[1:]),
	'res': '600',
	'color': '000000',
	'x': '28',
	'y': '14'
}
session = requests.session()
result = session.post(SITE, data=payload)

matches = re.search('hist.imageurl = "(.*)";', result.text)

print(SITE + matches.groups()[0])