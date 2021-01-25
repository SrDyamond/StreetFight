from django.shortcuts import render
from django.http import HttpResponse, HttpResponseNotAllowed, JsonResponse
import secrets
import hashlib
import datetime
from django.utils import timezone
import json
import math

from .models import Clan, Usuario, Sesion, Bandera, IntentoCaptura
from django.views.decorators.csrf import csrf_exempt

from . import custom_error_response

@csrf_exempt
def session(request, username):
    # Si recibimos una peticion que no es PUT, devolvemos un 405
    if request.method == 'POST':
        return login(request, username)
    elif request.method == 'DELETE':
        return logout(request, username)
    else:
        return HttpResponseNotAllowed(['POST', 'DELETE'])


def login(request, username):
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
        expiartion_date = timezone.now() + datetime.timedelta(days=7)
        response = {
            "user_id": user.id,
            "session_cookie": session_cookie,
            "expiration": expiartion_date.timestamp() # considerar si es la mejor forma
        }
        new_session = Sesion(id_usuario=user, fecha_caducidad=expiartion_date, valor_cookie=session_cookie)
        new_session.save()
        return JsonResponse(response, status=200)

    return JsonResponse(custom_error_response.BAD_PASSWORD, status=401)


def logout(request, username):
    if not 'sessioncookie' in request.headers:
        return JsonResponse(custom_error_response.BAD_REQUEST, status=400)
    
    session_cookie = request.headers.get('sessioncookie')

    try: # Recupero el user de base de datos, y si no existe devuelvo 404
        user = Usuario.objects.get(nombre__exact=username)
    except Usuario.DoesNotExist:
        return JsonResponse(custom_error_response.NOT_FOUND, status=404)

    session_list = Sesion.objects.filter(id_usuario__exact=user)

    valid_session = False
    for session in session_list:
        if session.valor_cookie == session_cookie:
            valid_session = True
            session.delete()
            break
    
    if valid_session:
        return JsonResponse(custom_error_response.LOGOUT_OK, status=200)
    else:
        return JsonResponse(custom_error_response.BAD_COOKIE, status=401)


@csrf_exempt
def flag(request):
    # Si recibimos una peticion que no es GET, devolvemos un 405
    if request.method != 'GET':
        return HttpResponseNotAllowed(['GET'])

    # if not 'latitude' in request.GET or not 'longitude' in request.GET or not 'radius' in request.GET:
    #     return JsonResponse(custom_error_response.BAD_REQUEST, status=400)

    user_latitude = request.GET.get('latitude', None)
    user_longitude = request.GET.get('longitude', None)
    search_radius = request.GET.get('radius', None)

    try:
        if user_latitude is None or user_longitude is None or search_radius is None:
            raise ValueError
        user_latitude = float(user_latitude)
        user_longitude = float(user_longitude)
        search_radius = float(search_radius)
        # https://www.movable-type.co.uk/scripts/latlong.html
        if search_radius > 0.200000: # 0.2 = 16.26 km (ese es el radio máximo permitido)
            raise ValueError
    except ValueError:
        return JsonResponse(custom_error_response.BAD_REQUEST, status=400)

    all_flags_list = Bandera.objects.all()
    flags_in_area_list = []
    
    for flag in all_flags_list:
        distance_to_location = math.sqrt((flag.latitud - user_latitude) ** 2 + (flag.longitud - user_longitude) ** 2)
        # print(distance_to_location, "<=", search_radius, distance_to_location <= search_radius)
        # if distance_to_location < search_radius:
        if distance_to_location <= search_radius:
            flags_in_area_list.append(flag)

    response = []
    for flag in flags_in_area_list:
        flag_response = {
            "id": flag.id,
            "latitude": flag.latitud,
            "longitude": flag.longitud,
            "capturing": flag.capturando
        }
        
        if not flag.id_clan is None:
            flag_response["clan"] = {
                "url_icon": flag.id_clan.id,
                "acronym": flag.id_clan.abreviatura,
                "color": flag.id_clan.color
            }

        response.append(flag_response)

    return JsonResponse(response, safe=False, status=200)


@csrf_exempt
def flag_by_id(request, id_flag):
    # Si recibimos una peticion que no es GET, devolvemos un 405
    if request.method != 'GET':
        return HttpResponseNotAllowed(['GET'])
    
    try:
        flag = Bandera.objects.get(pk=id_flag)
    except Bandera.DoesNotExist:
        return JsonResponse(custom_error_response.NOT_FOUND, status=404)

    response = {
        "id": flag.id,
        "name": flag.nombre,
        "description": flag.descripcion,
        "user_latitude": flag.latitud,
        "user_longitude": flag.longitud,
        "capturing": flag.capturando,
        "clan": {
            "id": flag.id_clan.id,
            "name": flag.id_clan.nombre,
            "color": flag.id_clan.color
        }
    }

    if not flag.id_clan.url_icon is None:
        response["clan"]["url_icon"] = flag.id_clan.url_icon

    if not flag.id_clan.abreviatura is None:
        response["clan"]["acronym"] = flag.id_clan.abreviatura

    return JsonResponse(response, status=200)

@csrf_exempt
def user(request):
    try:
        request_body = json.loads(request.body)
        if not 'password_sha' in request_body or not 'user_id' in request_body:
            raise ValueError
    except ValueError:
        return JsonResponse(custom_error_response.BAD_REQUEST, status=400)

    try: # MIra si existe el usuario, y si existe devuelvo 409
        user = Usuario.objects.get(nombre__exact=request_body.get('username'))
    except Usuario.ALREADY_EXISTS:
        return JsonResponse(custom_error_response.ALREADY_EXISTS, status=409)

