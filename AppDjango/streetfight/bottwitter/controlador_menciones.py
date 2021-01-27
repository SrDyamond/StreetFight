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

def obtener_banderas_clan(clan_obj):
	lista_usuarios_clan = Usuario.objects.filter(id_clan=clan_obj)
	suma = 0
	for usuario in lista_usuarios_clan:
		suma += usuario.banderas_capturadas
	return suma


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
		#ComandoBotAyuda
		if (texto_separado[1] == "Ayuda!"):
			api.update_status("Ayuda\n- Top clanes <número>\n- Top usuarios <número>\n- Info del clan <nombre>\n- Info del usuario <nombre>\n @"+str(user))
			print("Mensaje de ayuda enviado a"+str(user))
		#ComandoTopClanes
		if (texto_separado[1] == "Top" and texto_separado[2] == "clanes" and is_int==True):

			lista_clanes = Clan.objects.all()
			clan_puntuacion_dic = {}
			for clan in lista_clanes:
				clan_puntuacion_dic[clan.nombre] = obtener_banderas_clan(clan)

			str_salida = ""
			for nombre_clan in sorted(clan_puntuacion_dic, key=clan_puntuacion_dic.get, reverse=True)[:cantidad]:
				str_salida += "- {} ({})\n".format(nombre_clan, clan_puntuacion_dic[nombre_clan])

			print("Top "+str(cantidad)+" clanes enviado a @"+str(user))
			api.update_status("Hola @"+str(user)+" el top "+str(cantidad)+" clanes es: \n"+str_salida)
		#ComandoTopUsuarios
		elif (texto_separado[1] == "Top" and texto_separado[2] == "usuarios" and is_int==True):
			lista = Usuario.objects.all().order_by('-banderas_capturadas')[:cantidad]
			lista_usuarios={}
			str_salida = ""
			for usuario in lista:
				lista_usuarios[usuario.nombre] = usuario.banderas_capturadas

			for nombre_usuario in sorted(lista_usuarios, key=lista_usuarios.get, reverse=True)[:cantidad]:
				str_salida += "- {} ({})\n".format(nombre_usuario, lista_usuarios[nombre_usuario])

			print("Top "+str(cantidad)+" usuarios enviado a @"+str(user))
			api.update_status("Hola @"+str(user)+" el top "+str(cantidad)+" usuarios  es: \n"+str(str_salida))

		if (texto_separado[1] == "Info" and texto_separado[2] == "clan"):
			clan_str=texto.replace("@StreetFightSP Info clan " , "")
			lista_clanes = Clan.objects.all()
			print(clan_str)
			print(lista_clanes)
			try:
				clan= Clan.objects.get(nombre__exact=clan_str)
				print("Los clanes coinciden")
				#################
				#MANDAR TWEET
				#################
			except:
				print("Usuario no encontrado")
		else:
		#ComandoNoValido
			api.update_status("Comando no valido, escribe <Ayuda!> @"+str(user))
			print("Enviando ayuda")

	except tweepy.TweepError as error:
		if error.api_code == 187:
			 print('duplicate message')
