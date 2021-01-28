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

def obtener_miembros_clan(clan_obj):
	lista_usuarios_clan = Usuario.objects.filter(id_clan=clan_obj)
	return len(lista_usuarios_clan)


def parse_text(texto,user,api,tweet_id):
	print("Leyendo:", texto)
	texto_separado=texto.split()

	is_int=False
	if existe_posicion(texto_separado,3):
		try :
			cantidad = int(texto_separado[3])
			is_int=True
		except ValueError:
			# print("No int")
			pass

	try:
		#ComandoBotAyuda
		if (texto_separado[1] == "Ayuda!"):
			mensaje = "Ayuda\n- Top clanes <número>\n- Top usuarios <número>\n- Info del clan <nombre>\n- Info del usuario <nombre>\n @"+str(user)
			print(mensaje)
			api.update_status(mensaje,tweet_id)
		elif (texto_separado[1] == "Top" and texto_separado[2] == "clanes" and is_int==True):

			lista_clanes = Clan.objects.all()
			clan_puntuacion_dic = {}
			for clan in lista_clanes:
				clan_puntuacion_dic[clan.nombre] = obtener_banderas_clan(clan)

			str_salida = ""
			for nombre_clan in sorted(clan_puntuacion_dic, key=clan_puntuacion_dic.get, reverse=True)[:cantidad]:
				str_salida += "- {} ({})\n".format(nombre_clan, clan_puntuacion_dic[nombre_clan])

			mensaje = "Hola @"+str(user)+" el top "+str(cantidad)+" clanes es: \n"+str_salida
			print(mensaje)
			api.update_status(mensaje,tweet_id)
		#ComandoTopUsuarios
		elif (texto_separado[1] == "Top" and texto_separado[2] == "usuarios" and is_int==True):
			lista = Usuario.objects.all().order_by('-banderas_capturadas')[:cantidad]
			lista_usuarios={}
			str_salida = ""
			for usuario in lista:
				lista_usuarios[usuario.nombre] = usuario.banderas_capturadas

			for nombre_usuario in sorted(lista_usuarios, key=lista_usuarios.get, reverse=True)[:cantidad]:
				str_salida += "- {} ({})\n".format(nombre_usuario, lista_usuarios[nombre_usuario])

			mensaje = "Hola @"+str(user)+" el top "+str(cantidad)+" usuarios  es: \n"+str(str_salida)
			print(mensaje)
			api.update_status(mensaje,tweet_id)
		elif (texto_separado[1] == "Info" and texto_separado[2] == "clan"):
			clan_str=texto.replace("@StreetFightSP Info clan " , "")
			try:
				clan= Clan.objects.get(nombre__exact=clan_str)
				banderas=(obtener_banderas_clan(clan))
				miembros=(obtener_miembros_clan(clan))
				mensaje = "Hola @"+str(user)+" el clan "+str(clan)+" tiene "+str(banderas)+" banderas y "+str(miembros)+" miembros"
				print(mensaje)
				api.update_status(mensaje,tweet_id)
			except Clan.DoesNotExist:
				mensaje = "El clan no se econtro o no existe en la base de datos @"+str(user)
				print(mensaje)
				api.update_status(mensaje,tweet_id)
		elif (texto_separado[1] == "Info" and texto_separado[2] == "usuario"):
			usuario_str=texto.replace("@StreetFightSP Info usuario " , "")
			try:
				usuario_bbdd= Usuario.objects.get(nombre__exact=usuario_str)
				mensaje = "Hola @"+str(user)+" el usuario "+str(usuario_bbdd.nombre)+" tiene "+str(usuario_bbdd.banderas_capturadas)+" banderas y es del clan "+str(usuario_bbdd.id_clan.nombre)
				print(mensaje)
				api.update_status(mensaje,tweet_id)
			except Usuario.DoesNotExist:
				mensaje = "El usuario no se econtro o no existe en la base de datos @"+str(user)
				print(mensaje)
				api.update_status(mensaje,tweet_id)
		else:
			mensaje = "Comando no valido, escribe \"Ayuda!\" @"+str(user)
			print(mensaje)
			api.update_status(mensaje,tweet_id)
	except tweepy.TweepError as error:
		# if error.api_code == 187:
		# 	 print('duplicate message')
		pass
