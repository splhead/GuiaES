#!/usr/bin/python3
#encoding: utf-8
import glob, os, re, json, sys, datetime
from bs4 import BeautifulSoup

currentDate = datetime.datetime.now();

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
		if date.__contains__(mes) :
			if meses.index(mes) + 1 < 10 :
				sMes = '0' + str(meses.index(mes) + 1)
			else:
				sMes = str(meses.index(mes) + 1)

			if int(dia[0]) < 10 :
				sDia = '0' + str(dia[0])
			else:
				sDia = str(dia[0])
			return str(currentDate.year) + "-" + sMes + "-" + sDia

os.chdir(".")

json_obj = '['

for file in sorted(glob.glob("*.xhtml"),key=numericalSort):
	# print(file)

	content = open(file,'r').read()

	doc =  BeautifulSoup(content, "lxml", from_encoding="utf-8")

	# doc =  BeautifulSoup(content, "lxml", exclude_encodings="utf-8")
 
	dia = doc.find(attrs={"class":"data"})
	
	if dia is not None:
		dia = dia.text.lower().strip()
		
		titulo = doc.select_one('h1[class="titulo"]').text.upper()
		# print(dia, titulo)
		
		verso = doc.select_one('p[class="_3_VersoBiblico"]')

		formattedDay = str(dateFormat(dia))

		if formattedDay is not None:

			json_obj += "{\"day\":\"" + formattedDay + '",'
			json_obj += '"title":"' + titulo + '",'
			json_obj += '"verse":"' + verso.text + '",'

			texto = '"text":"'

			paragrafos = verso.findAllNext("p")
			for paragrafo in paragrafos:
				texto += paragrafo.text + "\n\n"

			texto += '",'
			json_obj += texto
			json_obj += '"type":' + str(1) + '},'
		else:
			print('Erro na data')

json_obj  = json_obj[:-1]
json_obj += ']'
out = json.dumps(json_obj)
final_out = re.sub(r'\\"','"', out[1:-1])

print(final_out)