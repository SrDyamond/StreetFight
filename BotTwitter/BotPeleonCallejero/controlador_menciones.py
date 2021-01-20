import tweepy
import logging
from config import create_api
import json
import time
import random

def existe_posicion(array, posicion):
	try:
		a = array[posicion]
		return True
	except IndexError:
		return False


def parse_text(texto,user,api):
	texto_separado=texto.split()
	is_int=False

	if existe_posicion(texto_separado,3):
		try :
			cantidad = int(texto_separado[3])
			is_int=True
		except ValueError  :
			print("Este string no contiene un int")

	try:
		if (texto_separado[1] == "Ayuda!"):
			api.update_status("Ayuda\n- Top clanes <número>\n- Top usuarios <número>\n- Info del clan <nombre>\n- Info del usuario <nombre>\n @"+str(user))
			print("Mensaje enviado a"+str())
			#AÑADIR TODAS LAS COMPROBACIONES PARA COMANDOS
		if (texto_separado[1] == "Top" and texto_separado[2] == "clanes" and is_int==True):
			print(cantidad)
		else:
			#Aqui va tweet diciendo que ponga bien el comando
			print("Introduce comando valido")
	except tweepy.TweepError as error:
		if error.api_code == 187:
			print('duplicate message')
