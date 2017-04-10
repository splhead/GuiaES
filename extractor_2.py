#!/usr/bin/python
#encoding: utf-8
import glob, os, re, json, sys
from bs4 import BeautifulSoup

numbers = re.compile(r'(\d+)')
def numericalSort(value):
    parts = numbers.split(value)
    parts[1::2] = map(int, parts[1::2])
    return parts

def dateFormat(date):
	meses = ('janeiro', 'fevereiro', u'março', 'abril', 'maio', 'junho',
		      'julho', 'agosto', 'setembro','outubro', 'novembro', 'dezembro');

	dia = numbers.findall(date)

	for mes in meses:
		sMes = ""
		sDia = ""
		if date.__contains__(mes.encode('utf-8')) :
			if meses.index(mes) + 1 < 10 :
				sMes = '0' + str(meses.index(mes) + 1)
			else:
				sMes = str(meses.index(mes) + 1)

			if int(dia[0]) < 10 :
				sDia = '0' + str(dia[0])
			else:
				sDia = str(dia[0])
			return "2017-" + sMes + "-" + sDia

os.chdir(".")

json_obj = '['

for file in sorted(glob.glob("*.xhtml"),key=numericalSort):
	# print(file)

	content = open(file,'r').read()

	doc =  BeautifulSoup(content, "lxml", from_encoding="utf-8")

	# doc =  BeautifulSoup(content, "lxml", exclude_encodings="utf-8")

	dia = doc.select_one('p[class="data]')
	if dia is not None:
		dia = dia.text.encode('utf-8')

		titulo = doc.select_one('p[class="titulo"]').text.encode('utf-8').upper()
		
		verso = doc.select_one('p[class="verso"]')

		json_obj += "{'day':'" + str(dateFormat(dia)) + "',"
		json_obj += "'title':'" + titulo + "',"
		json_obj += "'verse':'" + verso.text.encode('utf-8') + "',"

		texto = "'text':'"

		paragrafos = verso.findAllNext("p")
		for paragrafo in paragrafos:
			texto += paragrafo.text.encode('utf-8') + "\n\n"

		texto += "',"
		json_obj += texto
		json_obj += "'type':" + str(2) + "},"

json_obj  = json_obj[:-1]
json_obj += ']'
out = json.dumps(json_obj)

print out
