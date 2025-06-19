# Ingeniería de Software: TP de API

# Integrantes:

- Camila Mantilla
- Ignacio Scopel
- Jonathan Dominguez
- Juan Cardoso
- Luciano Serra
- Mateo Serrano
- Milton Formiga

# Instrucciones para levantar el proyecto

Para levantar la aplicación con Docker Compose

```
docker-compose up --build
```

Es necesario tener un archivo de variables de entorno `.env`, donde se defina lo
siguiente:

* `APP_FRONTEND_BASE_URL=http://localhost/`
* `APP_FRONTEND_BASE_URL=http://localhost/`
* `DB_NAME=futbol_5_api_db`
* `DB_USERNAME=...`
* `DB_PASSWORD=...`
* `DB_EXTERNAL_PORT=5500`
* `VOLUME_DIR=/home/<user>/docker-volumes`
* `ADMINER_EXTERNAL_PORT=9000`
* `BACKEND_EXTERNAL_PORT=8080`
* `FRONTEND_EXTERNAL_PORT=80`
* `BACKEND_EXTERNAL_URL=http://localhost:8080/`
* `APP_FRONTEND_BASE_URL=http://localhost:80/`
* `EMAIL_USERNAME=...`
* `EMAIL_PASSWORD=...`

Para el sistema de correos usamos [APP Passwords Google](https://myaccount.google.com/apppasswords), que pones el correo, el nombre de la app y te genera una password para usar el sistema de correos.

Los puertos son configurables.

Para correr los tests:

```
cd backend

mvn test
```