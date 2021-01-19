from django.db import models


class Clan(models.Model):
    nombre = models.CharField(max_length=25, unique=True)
    abreviatura = models.CharField(max_length=4, blank=True, null=True)
    color = models.CharField(max_length=7)
    url_icon = models.CharField(max_length=300, blank=True, null=True)
    fecha_fundacion = models.DateTimeField()

    def __str__(self):
        return self.nombre
    


class Usuario(models.Model):
    nombre = models.CharField(max_length=25, unique=True)
    banderas_capturadas = models.IntegerField()
    salt = models.CharField(max_length=40)
    clave_sha_concatenada = models.CharField(max_length=40)
    id_clan = models.ForeignKey(Clan, on_delete=models.CASCADE)
    fundador = models.BooleanField() # sugerencia de arreglo, cambiar en la documentación

    def __str__(self):
        return self.nombre


class Sesion(models.Model):
    id_usuario = models.ForeignKey(Usuario, on_delete=models.CASCADE)
    fecha_caducidad = models.DateTimeField()
    valor_cookie = models.CharField(max_length=10+1+64, unique=True)

    def __str__(self):
        return self.valor_cookie


class Bandera(models.Model):
    nombre = models.CharField(max_length=50)
    descripcion = models.CharField(max_length=500)
    latitud = models.FloatField()
    longitud = models.FloatField()
    id_clan = models.ForeignKey(Clan, on_delete=models.CASCADE, blank=True, null=True)

    def __str__(self):
        return self.nombre


class IntentoCaptura(models.Model):
    id_usuario = models.ForeignKey(Usuario, on_delete=models.CASCADE)
    id_clan = models.ForeignKey(Clan, on_delete=models.CASCADE)
    fecha = models.DateTimeField()

    def __str__(self):
        return str(self.id_usuario) + '-' + str(self.id_clan) + ' ' + str(self.fecha)