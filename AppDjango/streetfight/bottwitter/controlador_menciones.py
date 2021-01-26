import tweepy
import logging
import time

from AppDjango.asgi import application # no entendemos porqué pero hace falta para importar los models
from streetfight.models import Clan, Usuario, Sesion, Bandera, IntentoCaptura


def existe_posicion(array, posicion):
	try:
		a = array[posicion]
		return True
	except IndexError:
		return False


def parse_text(texto,user,api):
	texto_separado=texto.split()
	is_int=False
	# print(Usuario.objects.all())

	# lista = Usuario.objects.all().order_by('-banderas_capturadas')[:2]
	# print (lista)

	if existe_posicion(texto_separado,3):
		try :
			cantidad = int(texto_separado[3])
			is_int=True
		except ValueError  :
			print("Este string no contiene un int")

	try:
		#ComandoBotAyuda
		if (texto_separado[1] == "Ayuda!"):
			api.update_status("Ayuda\n- Top clanes <número>\n- Top usuarios <número>\n- Info del clan <nombre>\n- Info del usuario <nombre>\n @"+str(user))
			print("Mensaje de ayuda enviado a"+str(user))
		#ComandoTopClanes
		if (texto_separado[1] == "Top" and texto_separado[2] == "clanes" and is_int==True):
			lista = Usuario.objects.all()
			lista_clan=[]
			for usuario in lista:
				print("ID:"+str(usuario.id_clan.nombre))
				print("LISTA"+str(lista_clan))
				if (usuario.id_clan.nombre not in lista_clan):
					lista_clan.append(usuario.id_clan.nombre)

			print("LISTAFINAL"+str(lista_clan))
		#ComandoTopUsuarios
		elif (texto_separado[1] == "Top" and texto_separado[2] == "usuarios" and is_int==True):
			lista = Usuario.objects.all().order_by('-banderas_capturadas')[:cantidad]
			lista_usuarios=[]
			for usuario in lista:
				# print("Lista:-"+str(lista_usuarios))
				lista_usuarios.append(usuario.nombre)
			api.update_status("Hola @"+str(user)+" el top "+str(cantidad)+" usuarios  es: \n"+str(lista_usuarios))
		else:
		#ComandoNoValido
			api.update_status("Comando no valido, escribe <Ayuda!> @"+str(user))
			print("Enviadondo ayuda")

	except tweepy.TweepError as error:
		if error.api_code == 187:
			 print('duplicate message')
