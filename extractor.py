#!/usr/bin/python
import glob, os, re, json
from bs4 import BeautifulSoup

numbers = re.compile(r'(\d+)')
def numericalSort(value):
    parts = numbers.split(value)
    parts[1::2] = map(int, parts[1::2])
    return parts

def dateFormat(date):
	meses = ('janeiro', 'fevereiro', 'mar\u00e7o', 'abril', 'maio', 'junho',
		      'julho', 'agosto', 'setembro','outubro', 'novembro', 'dezembro');

	dia = numbers.findall(date)

	for mes in meses:
		if date.__contains__(mes) :
			out = str(dia[0]) + "/" + str(meses.index(mes) + 1)
			return out

os.chdir(".")

json_obj = "["

for file in sorted(glob.glob("*.xhtml"),key=numericalSort):
	#print(file)
	content = open(file,'r')
	# print(content.readlines())
	doc =  BeautifulSoup(content.read(), "lxml")
	dia = doc.select_one('p[class="x01-data"]').text
	titulo = doc.select_one('p[class="x03-t-tulo"]').text
	verso = doc.select_one('p[class="x04-medit"]')

	json_obj += "{'day':'" + str(dateFormat(dia)) + "',"
	json_obj += "'title':'" + titulo + "',"
	json_obj += "'verse':'" + verso.text + "',"

	texto = "'text':'"

	paragrafos = verso.findAllNext("p")
	for paragrafo in paragrafos:
		texto += paragrafo.text + "\n\n"

	texto += "'},"
	json_obj += texto

json_obj += "]"
out = json.dumps(json_obj)

print out