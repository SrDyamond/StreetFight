from django.urls import path

from . import rest_facade
from . import views

urlpatterns = [
    path('', views.index, name='index'),
    path('user/<username>/session', rest_facade.login, name='login'),
]
