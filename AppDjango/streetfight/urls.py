from django.urls import path

# from . import rest_facade
from . import views

urlpatterns = [
    # path('user/<username>/login', rest_facade.login, name='login'),
    path('', views.index, name='index'),
]
