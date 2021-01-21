from django.contrib import admin

from .models import Clan
from .models import Usuario
from .models import Sesion
from .models import Bandera
from .models import IntentoCaptura

admin.site.register(Clan)
admin.site.register(Usuario)
admin.site.register(Sesion)
admin.site.register(Bandera)
admin.site.register(IntentoCaptura)
