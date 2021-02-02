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


def _to_milliseconds(datetime):
    # https://stackoverflow.com/questions/6999726/how-can-i-convert-a-datetime-object-to-milliseconds-since-epoch-unix-time-in-p#comment43779166_23004143
    # return int(datetime.replace(tzinfo=timezone.utc).timestamp() * 1000)
    return int(datetime.timestamp() * 1000)


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

    try:  # Recupero el user de base de datos, y si no existe devuelvo 404
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
            "expiration": _to_milliseconds(expiartion_date)  # revisar funcionamiento
        }
        new_session = Sesion(
            id_usuario=user, fecha_caducidad=expiartion_date, valor_cookie=session_cookie)
        new_session.save()
        return JsonResponse(response, status=200)

    return JsonResponse(custom_error_response.BAD_PASSWORD, status=401)


def logout(request, username):
    if not 'sessioncookie' in request.headers:
        return JsonResponse(custom_error_response.BAD_REQUEST, status=400)

    session_cookie = request.headers.get('sessioncookie')

    try:  # Recupero el user de base de datos, y si no existe devuelvo 404
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
        # 0.2 = 16.26 km (ese es el radio máximo permitido)
        if search_radius > 0.200000:
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
    # Si recibimos una peticion que no es PUT, devolvemos un 405
    if request.method == 'GET':
        return search_user(request)
    elif request.method == 'POST':
        return create_user(request)
    else:
        return HttpResponseNotAllowed(['POST', 'GET'])


def search_user(request):
    q = request.GET.get('q', None)
    if q is None:
        return JsonResponse(custom_error_response.BAD_REQUEST, status=400)

    user_list = Usuario.objects.filter(nombre__icontains=q)

    response = []

    for user in user_list:
        user_info_dict = {
            "id": user.id,  # autogenerado por django
            "name": user.nombre,
            "clan": {
                "id": user.id_clan.id,  # autogenerado por django
                "name": user.id_clan.nombre,
                "color": user.id_clan.color
            }
        }

        if not user.id_clan.url_icon is None:
            user_info_dict["clan"]["url_icon"] = user.id_clan.url_icon

        if not user.id_clan.abreviatura is None:
            user_info_dict["clan"]["acronym"] = user.id_clan.abreviatura

        response.append(user_info_dict)

    return JsonResponse(response, safe=False, status=200)


def create_clan(new_clan_json):
    try:  # Compruevo la existencia del name, color
        if not 'name' in new_clan_json or not 'color' in new_clan_json:
            raise ValueError
    except ValueError:
        return None, JsonResponse(custom_error_response.BAD_REQUEST, status=400)

    if Clan.objects.filter(nombre__exact=new_clan_json.get('name')).exists():
        return None, JsonResponse(custom_error_response.ALREADY_EXISTS, status=409)

    new_clan = Clan(nombre=new_clan_json.get('name'),
                    color=new_clan_json.get('color'))

    if 'acronym' in new_clan_json and 'url_icon' in new_clan_json:
        new_clan.abreviatura = new_clan_json.get('acronym')
        new_clan.url_icon = new_clan_json.get('url_icon')
    elif 'acronym' in new_clan_json:
        new_clan.abreviatura = new_clan_json.get('acronym')
    elif 'url_icon' in new_clan_json:
        new_clan.url_icon = new_clan_json.get('url_icon')

    new_clan.save()

    return new_clan, None


def create_user(request):
    try:  # Compruevo la existencia del username y del password_sha
        request_body = json.loads(request.body)
        if not 'password_sha' in request_body or not 'username' in request_body:
            raise ValueError
        if not 'create_clan' in request_body and not 'join_clan_id' in request_body:
            raise ValueError
    except ValueError:
        return JsonResponse(custom_error_response.BAD_REQUEST, status=400)

    # Mira si existe el usuario, y si existe devuelvo 409
    if Usuario.objects.filter(nombre__exact=request_body.get('username')).exists():
        return JsonResponse(custom_error_response.ALREADY_EXISTS, status=409)

    clan = None

    if 'create_clan' in request_body:
        clan, error = create_clan(request_body.get('create_clan'))
        if error is not None:
            return error
    else:
        try:  # Obtenemos el objeto clan al que unirnos, sino 404
            clan = Clan.objects.get(pk=request_body.get('join_clan_id'))
        except Clan.DoesNotExist:
            return JsonResponse(custom_error_response.NOT_FOUND, status=404)

    salt = secrets.token_urlsafe(64)[:16]
    clave_sha_concatenada_request_no_hash = request_body.get(
        'password_sha') + salt
    clave_sha_concatenada_request = hashlib.sha1(
        str.encode(clave_sha_concatenada_request_no_hash)).hexdigest()

    new_user = Usuario(
        nombre=request_body.get('username'),
        salt=salt,
        clave_sha_concatenada=clave_sha_concatenada_request,
        id_clan=clan
    )

    if 'create_clan' in request_body:
        new_user.fundador = True

    new_user.save()  # guardamos el usuario en la DB

    session_cookie = str(new_user.id) + "-" + secrets.token_urlsafe(64)
    expiartion_date = timezone.now() + datetime.timedelta(days=7)

    response = {
        "user_id": new_user.id,
        "session_cookie": session_cookie,
        "expiration": _to_milliseconds(expiartion_date)  # revisar funcionamiento
    }

    new_session = Sesion(id_usuario=new_user, fecha_caducidad=expiartion_date, valor_cookie=session_cookie)
    new_session.save()  # guardamos la nueva sesion en la DB

    return JsonResponse(response, status=200)


@csrf_exempt
def user_by_username(request, username):
    if request.method != 'GET':
        return HttpResponseNotAllowed(['GET'])

    try:  # Recupero el user de base de datos, y si no existe devuelvo 404
        user = Usuario.objects.get(nombre__exact=username)
    except Usuario.DoesNotExist:
        return JsonResponse(custom_error_response.NOT_FOUND, status=404)

    response = user_info(user)

    return JsonResponse(response, status=200)


@csrf_exempt
def user_top(request):

    if request.method != 'GET':
        return HttpResponseNotAllowed(['GET'])

    length = request.GET.get('length', 10)

    try:
        length = int(length)
        if length > 200 or length == 0:
            raise ValueError
    except ValueError:
        return JsonResponse(custom_error_response.BAD_REQUEST, status=400)

    # all_users_list = sorted(Usuario.objects.all(), key=lambda Usuario: Usuario.banderas_capturadas)
    # Comprobar funcionalidad

    all_users_list = Usuario.objects.all().order_by('-banderas_capturadas')

    response = []
    for i in range(length):
        try:
            response.append(user_info(all_users_list[i]))
        except IndexError:
            break

    """
    i = 0
    for user in all_users_list:
        if i >= length:
            break
        response.append(user_info(user))
        i += 1
    """
    return JsonResponse(response, safe=False, status=200)


def user_info(user):
    response = {
        "id": user.id,  # autogenerado por django
        "name": user.nombre,
        "captured_flags": user.banderas_capturadas,
        "founder": user.fundador,
        "clan": {
            "id": user.id_clan.id,  # autogenerado por django
            "name": user.id_clan.nombre,
            "color": user.id_clan.color
        }
    }

    if not user.id_clan.url_icon is None:
        response["clan"]["url_icon"] = user.id_clan.url_icon

    if not user.id_clan.abreviatura is None:
        response["clan"]["acronym"] = user.id_clan.abreviatura

    return response


@csrf_exempt
def clan(request):
    if request.method == 'GET':
        return search_clan(request)
    elif request.method == 'POST':
        return create_clan_only(request)
    else:
        return HttpResponseNotAllowed(['POST', 'GET'])


def create_clan_only(request):
    try:
        request_body = json.loads(request.body)
    except ValueError:
        return JsonResponse(custom_error_response.BAD_REQUEST, status=400)

    if Clan.objects.filter(nombre__exact=request_body.get('name')).exists():
        return JsonResponse(custom_error_response.ALREADY_EXISTS, status=409)

    session_cookie = request.headers.get('sessioncookie')

    if not 'founder_id' in request_body:
        return JsonResponse(custom_error_response.BAD_REQUEST, status=400)

    try:
        session_list = Sesion.objects.filter(valor_cookie=session_cookie)
    except:
        return JsonResponse(custom_error_response.BAD_COOKIE, status=401)

    try:  # Si el usuario no existe
        user = Usuario.objects.get(pk=request_body.get('founder_id'))
    except:
        return JsonResponse(custom_error_response.NOT_FOUND, status=404)

    clan, error = create_clan(request_body)
    if error is not None:
        return error

    user.id_clan = clan
    user.fundador = True
    user.save()

    response = {
        "id": clan.id,
        "name": clan.nombre,
        "url_icon": clan.url_icon,
        "acronym": clan.abreviatura,
        "color": clan.color
    }

    return JsonResponse(response, status=201)


def search_clan(request):
    q = request.GET.get('q', None)
    if q is None:
        return JsonResponse(custom_error_response.BAD_REQUEST, status=400)

    clan_list = Clan.objects.filter(nombre__icontains=q)

    response = []

    for clan in clan_list:
        clan_info_dict = {
            "id": clan.id,  # autogenerado por django
            "name": clan.nombre,
            "color": clan.color

        }

        if not clan.url_icon is None:
            clan_info_dict["url_icon"] = clan.url_icon

        if not clan.abreviatura is None:
            clan_info_dict["acronym"] = clan.abreviatura

        response.append(clan_info_dict)

    return JsonResponse(response, safe=False, status=200)


def clan_top(request):
    if request.method != 'GET':
        return HttpResponseNotAllowed(['GET'])

    length = request.GET.get('length', 10)
    try:
        length = int(length)
        if length > 200 or length == 0:
            raise ValueError
    except ValueError:
        return JsonResponse(custom_error_response.BAD_REQUEST, status=400)

    try:  # Recupero el clan de base de datos, y si no existe devuelvo 404
        all_clan_list = Clan.objects.all()
    except Clan.DoesNotExist:
        return JsonResponse(custom_error_response.NOT_FOUND, status=404)

    top_clan = []

    for clan in all_clan_list:
        try:  # Recupero los user de base de datos, y si no existe devuelvo 404
            lista = Usuario.objects.filter(id_clan=clan)
        except Usuario.DoesNotExist:
            return JsonResponse(custom_error_response.NOT_FOUND, status=404)

        points = 0
        for user in lista:  # Sumo los puntos
            points += user.banderas_capturadas
        top_clan.append(points)

    # Ordeno los clanes segun la puntiacion
    top_clan, all_clan_list = (list(x) for x in zip(
        *sorted(zip(top_clan, all_clan_list), key=lambda pair: pair[0], reverse=True)))

    """for i in top_clan:
        print(top_clan[:i])"""
    response = []
    for i in range(length):
        try:  # Revisar que no pidas mas clanes de los que existen
            clan = all_clan_list[i]
        except IndexError:
            return JsonResponse(custom_error_response.BAD_REQUEST, status=400)
        try:
            Clan_info = {
                "id":  clan.id,
                "name": clan.nombre,
                "url_icon": clan.url_icon,
                "acronym": clan.abreviatura,
                "color": clan.color
            }
            response.append(Clan_info)
        except IndexError:
            break
    return JsonResponse(response, safe=False, status=200)


def clan_by_id(request, id_clan):
    if request.method != 'GET':
        return HttpResponseNotAllowed(['GET'])

    try:  # Recupero el clan de base de datos, y si no existe devuelvo 404
        clan = Clan.objects.get(pk=id_clan)
    except Clan.DoesNotExist:
        return JsonResponse(custom_error_response.NOT_FOUND, status=404)

    try:  # Recupero los user de base de datos, y si no existe devuelvo 404
        lista = Usuario.objects.filter(id_clan=clan)
    except Usuario.DoesNotExist:
        return JsonResponse(custom_error_response.NOT_FOUND, status=404)

    fundadores = []
    cont = 0
    points = 0
    for user in lista:  # Sumo los puntos
        points += user.banderas_capturadas
        if user.fundador == True:  # Recojo el/los nombres de los fundadores
            fundadores.append(user.nombre)
            cont += 1

    response = {
        "id":  clan.id,
        "name": clan.nombre,
        "url_icon": clan.url_icon,
        "acronym": clan.abreviatura,
        "color": clan.color,
        "members": cont,
        "flags": points,
        "founder_names": fundadores
    }

    return JsonResponse(response, status=200)


@csrf_exempt
def change_clan(request, username, id_clan):
    if request.method != 'POST':
        return HttpResponseNotAllowed(['POST'])
    #si cookei es valida sino 401
    if not 'sessioncookie' in request.headers:
        return JsonResponse(custom_error_response.BAD_REQUEST, status=400)

    session_cookie = request.headers.get('sessioncookie')

    try:  #Si user existe sino 404
        user = Usuario.objects.get(nombre__exact=username)
    except Usuario.DoesNotExist:
        return JsonResponse(custom_error_response.NOT_FOUND, status=404)

    session_list = Sesion.objects.filter(id_usuario__exact=user)

    valid_session = False
    for session in session_list:
        if session.valor_cookie == session_cookie:
            valid_session = True
            break

    if not valid_session:
        return JsonResponse(custom_error_response.BAD_COOKIE, status=401)
    #si cln exisste sino 404
    try:  # Recupero el clan de base de datos, y si no existe devuelvo 404
        clan = Clan.objects.get(pk=id_clan)
    except Clan.DoesNotExist:
        return JsonResponse(custom_error_response.NOT_FOUND, status=404)
    #si usuarios se cambia al mismo clan eror 409
    if clan == user.id_clan:
        return JsonResponse(custom_error_response.ALREADY_EXISTS, status=409)

    user.id_clan=clan
    user.save()

    response = {} #temporal

    return JsonResponse(custom_error_response.CLAN_CHANGED, status=200)


@csrf_exempt
def try_capture(request, username, id_flag):
    if request.method != 'POST':
        return HttpResponseNotAllowed(['POST']) # error 405

    #sino manda la cookie, error 400
    if not 'sessioncookie' in request.headers:
        return JsonResponse(custom_error_response.BAD_REQUEST, status=400)

    session_cookie = request.headers.get('sessioncookie')

    try:  #Si user existe sino 404
        user = Usuario.objects.get(nombre__exact=username)
    except Usuario.DoesNotExist:
        return JsonResponse(custom_error_response.NOT_FOUND, status=404)

    session_list = Sesion.objects.filter(id_usuario__exact=user)

    valid_session = False
    for session in session_list:
        if session.valor_cookie == session_cookie:
            valid_session = True
            break

    if not valid_session:#si coookie no es valida 401
        return JsonResponse(custom_error_response.BAD_COOKIE, status=401)

    try: # si bandera no existe, error 404
        flag = Bandera.objects.get(pk=id_flag)
    except Bandera.DoesNotExist:
        return JsonResponse(custom_error_response.NOT_FOUND, status=404)

    # obtengo todos los intentos de captura sobre esa bandera
    intento_captura_list = IntentoCaptura.objects.filter(id_bandera=flag)

    # cuento cuantos usuarios de mi clan han realizado un intento de captura en los últimos 10 min
    users_capturing = 0
    for intento_captura in intento_captura_list:
        antiguedad_intento_captura = timezone.now() - intento_captura.fecha
        if intento_captura.id_usuario.id_clan == user.id_clan and antiguedad_intento_captura < datetime.timedelta(minutes=10):
            users_capturing += 1

    if users_capturing >= 1:
        # si hay 1 o más usuarios de tu mismo clan la bandera se captura
        flag.capturando = False
        flag.id_clan = user.id_clan
        # y borramos todos los intentos de captura asociados a esa bandera (creo que no lo voy a hacer)
        # for intento_captura in intento_captura_list:
        #     intento_captura.delete()
    else:
        # sino creamos un nuevo intento de captura
        flag.capturando = True
        new_intento = IntentoCaptura(id_usuario=user, id_bandera=flag)
        new_intento.save()
    flag.save()

    return JsonResponse(custom_error_response.CAPTURE_STARTED, status=200)
