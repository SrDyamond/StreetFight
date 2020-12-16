# Street Fight

Street Fight es un MMORPG no lineal en el que escribes tu propia historia sin limitarte a seguir un camino prefijado, explora un amplio mundo abierto, todo cuanto hagas tendra su repercusíon en el mundo.

## Diagrama ER

![Diagrama ER](ER/ER.png)

## Tipos de datos

### Tabla SESION
- id INT PRIMARY KEY
- id_usuario INT NOT NULL FOREIGN KEY
- timestamp_caducidad BIGINT NOT NULL
- valor_cookie VARCHAR(40) NOT NULL

### Tabla USARIO
- id INT PRIMARY KEY
- nombre VARCHAR(25) NOT NULL
- banderas_capturadas INT NOT NULL
- salt VARCHAR(40) NOT NULL
- clave_sha_concatenada VARCHAR(40) NOT NULL
- id_clan INT NOT NULL FOREIGN KEY

### Tabla CLAN
- id INT PRIMARY KEY
- nombre VARCHAR(25) NOT NULL
- abreviatura CHAR(4)
- color CHAR(7) NOT NULL
- url_icono VARCHAR(300)
- timestamp_fundacion BIGINT NOT NULL
- id_fundador INT NOT NULL FOREIGN KEY

### Tabla BANDERA
- id INT PRIMARY KEY
- nombre VARCHAR(50) NOT NULL
- descripcion VARCHAR(500) NOT NULL
- latitud DOUBLE NOT NULL
- longitud DOUBLE NOT NULL
- id_clan INT NOT NULL FOREIGN KEY

### Tabla INTENTO_CAPTURA
- id_usuario INT NOT NULL FOREIGN KEY
- id_bandera INT NOT NULL FOREIGN KEY
- timestamp BIGINT NOT NULL
- PRIMARY KEY (id_usuario, id_bandera, timestamp)

## Mockups UI

### Android APP

### Webpage

### Twitter Bot

#### DIFUSION AUTOMATICA

Mensaje de difusion cuando un clan captura una bandera (si esa bandera esta en blanco).

```
El clan Los Pepes ha capturado la bandera de Afundación.
```

Mensaje de difusion cuando un clan captura una bandera y otro clan la pierde.

```
El clan Los Pepes ha robado la bandera de Afundación al clan Los Juanjos.
```

Mensaje de difusion para la puntuación del los top clanes (10).

```
El nuevo top 10 de clanes es:
- clan 1 (score)
- clan 2 (score)
- clan 3 (score)
- etc
```

Mensaje de difusion para la puntuación del los top usuarios (10).

```
El nuevo top 10 de usuarios es:
- user 1 (score)
- user 2 (score)
- user 3 (score)
- etc
```

Mensaje de difusion clan sin banderas.

```
El clan Los Juanjos se han quedado sin territorio, a ver si se ponen las pilas.
```

#### SOLICITADAS

Top de clanes (XX).

solicitud:

```
Top clanes 30
```

respuesta:

```
El top 30 de clanes es:
- clan 1 (score)
- clan 2 (score)
- clan 3 (score)
- etc
```

Top de usuarios (XX).

solicitud:

```
Top usuarios 15
```

respuesta:

```
El top 15 de usuarios es:
- user 1 (score)
- user 2 (score)
- user 3 (score)
- etc
```

Consulta clan

solicitud:

```
Info del clan Los Pepes
```

respuesta:

```
El clan Los Pepes (LP) tiene una puntuación de 675, ha capturado 40 banderas y tiene 30 miembros.
```

Consulta usuario.

solicitud:

```
Info del usuario Pikachu78
```

respuesta:
```
El usuario Pikachu78 tiene una puntuación de 50, ha capturado 10 banderas y pertenece al clan Pokimons En Acción (PEA).
```

Comando no reconocido.

```
Oh, algo a salido mal. Puto, escribe ayuda para saber como usar esto.
```

Comando ayuda.

solicitud: 

```
Ayuda
```

```
Help
```

```
Ayúdame
```

respuesta:

```
todos los comandos
```

## Definición OpenAPI

[Archivo de definición del OpenAPI](openapi.yaml)