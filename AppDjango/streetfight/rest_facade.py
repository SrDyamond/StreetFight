from django.shortcuts import render
from django.http import HttpResponse, HttpResponseNotAllowed, JsonResponse
import secrets
import hashlib
import json

from .models import Usuario
from django.views.decorators.csrf import csrf_exempt

from . import custom_error_response

@csrf_exempt
def login(request, username):
    # Si recibimos una peticion que no es PUT, devolvemos un 405
    if request.method != 'POST':
        return HttpResponseNotAllowed(['POST'])

    try: # Recupero el usuario de base de datos, y si no existe devuelvo 404
        usuario = Usuario.objects.get(nombre__exact=username)
    except Usuario.DoesNotExist:
        return JsonResponse(custom_error_response.NOT_FOUND, status=404)

    request_body = json.loads(request.body)

    clave_sha_concatenada_request_no_hash = request_body.get('password_sha') + usuario.salt
    clave_sha_concatenada_request = hashlib.sha1(str.encode(clave_sha_concatenada_request_no_hash)).hexdigest()
    clave_sha_concatenada_db = usuario.clave_sha_concatenada
    
    if clave_sha_concatenada_db == clave_sha_concatenada_request:
        # Genero un token de sesion

#########

        aleatorio = secrets.token_urlsafe(64)
        mi_respuesta = {"session_cookie": aleatorio}
        usuario.token_sesion = aleatorio
        usuario.save()
        return JsonResponse(mi_respuesta, status=200)

    return JsonResponse({"errorDescription": "Error en la contraseña, acceso a la cuenta denegado"}, status=401)
