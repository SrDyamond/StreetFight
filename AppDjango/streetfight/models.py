from django.db import models
from colorfield.fields import ColorField # https://github.com/fabiocaccamo/django-colorfield
import secrets


class Clan(models.Model):
    nombre = models.CharField(max_length=25, unique=True)
    abreviatura = models.CharField(max_length=4, blank=True, null=True)
    color = ColorField(format='hexa', default="#FF0000")
    url_icon = models.URLField(max_length=300, blank=True, null=True, default=None)
    fecha_fundacion = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.nombre


class Usuario(models.Model):
    nombre = models.CharField(max_length=25, unique=True)
    banderas_capturadas = models.IntegerField(default=0, editable=True)
    salt = models.CharField(max_length=16)
    clave_sha_concatenada = models.CharField(max_length=40)
    id_clan = models.ForeignKey(Clan, on_delete=models.CASCADE)
    fundador = models.BooleanField(default=False) # sugerencia de arreglo, cambiar en la documentación

    def __str__(self):
        return self.nombre + " (" + self.id_clan.abreviatura + ")"


class Sesion(models.Model):
    id_usuario = models.ForeignKey(Usuario, on_delete=models.CASCADE)
    fecha_caducidad = models.DateTimeField()
    valor_cookie = models.CharField(max_length=10+1+64, unique=True, default="651-" + secrets.token_urlsafe(nbytes=64))

    def __str__(self):
        return "Sesión de " + str(self.id_usuario) + " hasta " + str(self.fecha_caducidad)


class Bandera(models.Model):
    nombre = models.CharField(max_length=50)
    descripcion = models.CharField(max_length=500, blank=True, null=True)
    latitud = models.FloatField(default=43.370682)
    longitud = models.FloatField(default=-8.395913)
    capturando = models.BooleanField(default=False)
    id_clan = models.ForeignKey(Clan, on_delete=models.CASCADE, blank=True, null=True, default=None)

    def __str__(self):
        if self.id_clan:
            return self.nombre + " (" + self.id_clan.abreviatura + ")"
        else:
            return self.nombre


class IntentoCaptura(models.Model):
    id_usuario = models.ForeignKey(Usuario, on_delete=models.CASCADE)
    id_bandera = models.ForeignKey(Bandera, on_delete=models.CASCADE)
    fecha = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.id_usuario.nombre + " captura " + self.id_bandera.nombre + " (" + str(self.fecha) + ")"