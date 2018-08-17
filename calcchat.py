import sys
from requests_html import HTMLSession

SITE = 'http://www.calcchat.com/book/Calculus-ETF-4e/' + sys.argv[1] + '/' + sys.argv[2] + '/' + sys.argv[3]
session = HTMLSession()

r = session.get(SITE)
r.html.render()
img_element = r.html.find('#solutionimg', first=True)
print(img_element.attrs['src'])