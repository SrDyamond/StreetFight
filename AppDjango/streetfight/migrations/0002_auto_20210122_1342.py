# Generated by Django 3.1.5 on 2021-01-22 12:42

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('streetfight', '0001_initial'),
    ]

    operations = [
        migrations.AddField(
            model_name='bandera',
            name='capturando',
            field=models.BooleanField(default=False),
        ),
        migrations.AlterField(
            model_name='sesion',
            name='valor_cookie',
            field=models.CharField(default='651-r68dbVJA6jZFJJiNBpp5J2AiUlTuZM7yW19G55KIXrQoi2Ho13dJ-Msp0izCbxkRJCVAGe2IlTWBwgzqTflF7A', max_length=75, unique=True),
        ),
    ]
