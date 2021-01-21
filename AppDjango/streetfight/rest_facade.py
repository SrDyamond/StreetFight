from django.shortcuts import render
from django.http import HttpResponse, HttpResponseNotAllowed, JsonResponse
import secrets
import hashlib
import datetime
import json

from .models import Usuario, Sesion
from django.views.decorators.csrf import csrf_exempt

from . import custom_error_response

@csrf_exempt
def login(request, username):
    # Si recibimos una peticion que no es PUT, devolvemos un 405
    if request.method != 'POST':
        return HttpResponseNotAllowed(['POST'])

    try:
        request_body = json.loads(request.body)
        if not 'password_sha' in request_body:
            raise ValueError
    except ValueError:
        return JsonResponse(custom_error_response.BAD_REQUEST, status=400)

    try: # Recupero el user de base de datos, y si no existe devuelvo 404
        user = Usuario.objects.get(nombre__exact=username)
    except Usuario.DoesNotExist:
        return JsonResponse(custom_error_response.NOT_FOUND, status=404)

    clave_sha_concatenada_request_no_hash = request_body.get('password_sha') + user.salt
    clave_sha_concatenada_request = hashlib.sha1(str.encode(clave_sha_concatenada_request_no_hash)).hexdigest()
    clave_sha_concatenada_db = user.clave_sha_concatenada
    
    if clave_sha_concatenada_db == clave_sha_concatenada_request:
        # Genero un token de sesion
        session_cookie = str(user.id) + "-" + secrets.token_urlsafe(64)
        expiartion_date = datetime.datetime.now() + datetime.timedelta(days=7)
        response = {
            "user_id": user.id,
            "session_cookie": session_cookie,
            "expiration": expiartion_date.timestamp() # considerar si es la mejor forma
        }
        new_session = Sesion(id_usuario=user, fecha_caducidad=expiartion_date, valor_cookie=session_cookie)
        new_session.save()
        return JsonResponse(response, status=200)

    return JsonResponse(custom_error_response.BAD_PASSWORD, status=401)
