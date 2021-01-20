import tweepy
import logging
from config import create_api
import json
import time
import random

def parse_text(texto,user,api):
	texto_separado=texto.split()

	try:
		if (texto_separado[1] == "Ayuda!"):
			api.update_status("Ayuda\n- Top clanes <número>\n- Top usuarios <número>\n- Info del clan <nombre>\n- Info del usuario <nombre>\n @"+str(user))
			print("Mensaje enviado a"+str())
			#AÑADIR TODAS LAS COMPROBACIONES PARA COMANDOS
	except tweepy.TweepError as error:
		if error.api_code == 187:
			print('duplicate message')
