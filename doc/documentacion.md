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

## Definición OpenAPI

[Archivo de definición del OpenAPI](openapi.yaml)