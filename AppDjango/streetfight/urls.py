from django.urls import path

from . import rest_facade
from . import views

urlpatterns = [
    path('', views.index, name='index'),
    path('user/top', rest_facade.user_top, name='user_top'),
    path('user/<username>/session', rest_facade.session, name='session'),
    path('user', rest_facade.user, name='user'),
    path('user/<username>', rest_facade.user_by_username, name='user_search'),
    path('flag', rest_facade.flag, name='flag'),
    path('flag/<id_flag>', rest_facade.flag_by_id, name='flag_by_id'),
    path('clan', rest_facade.clan, name='clan'),
    path('clan/top', rest_facade.clan_top, name='clan_top'),
    path('clan/<id_clan>', rest_facade.clan_by_id, name='clan_by_id'),
    path('user/<username>/clan/<id_clan>', rest_facade.change_clan, name='change_clan'),
    path('user/<username>/catch/<id_flag>', rest_facade.try_capture, name='try_capture')
]
