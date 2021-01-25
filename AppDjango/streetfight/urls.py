from django.urls import path

from . import rest_facade
from . import views

urlpatterns = [
    path('', views.index, name='index'),
    path('user/<username>/session', rest_facade.session, name='session'),
    path('flag', rest_facade.flag, name='flag'),
    path('flag/<id_flag>', rest_facade.flag_by_id, name='flag_by_id'),
    path('user',rest_facade.user, name ='user')
]
