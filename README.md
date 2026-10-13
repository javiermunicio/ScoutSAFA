# Scouts SAFA

App Android (Kotlin + Jetpack Compose) del **Grupo Scout SAFA – Moratalaz, Madrid**.
Misma organización de archivos y mismas tecnologías que RoomMatch: Firebase (Auth, Firestore, Messaging), Koin para inyección de dependencias y Navigation Compose.

## Qué hace

- **Registro / login** con correo y contraseña, y un onboarding para elegir la rama (Castores, Lobatos, Scouts, Escultas, Rovers) y el teléfono.
- **Inicio**: próximas actividades y anuncios fijados que afectan a tu rama.
- **Actividades**: calendario filtrable por rama (próximas / pasadas). Cada familia confirma "Voy / No voy". Los scouters crean, editan y borran, y ven quién asiste.
- **Anuncios**: avisos al grupo entero o a una rama, con opción de fijarlos.
- **Perfil**: teléfono, rama, tema oscuro y cerrar sesión.
- **Notificaciones push** por topics de FCM: `grupo` y `section_<RAMA>` (p. ej. `section_SCOUTS`).

## Estructura (igual que RoomMatch)

```
app/src/main/java/es/scoutsafa/app/
├── MainActivity.kt, MyApp.kt
├── data/
│   ├── remote/            FirestoreCollections
│   └── repositories/      Auth, User, Activity, Announcement, Attendance
├── di/                    AppModule (Koin)
├── domain/
│   ├── model/             User, Section, ScoutActivity, Announcement, ActivityAttendance
│   └── usecase/           un caso de uso por acción
├── notifications/         helper, token FCM, servicio de mensajes, permiso
├── presentation/
│   ├── navigation/        Screen, NavGraph
│   ├── ui/components/     tarjetas, formulario, filtros...
│   ├── ui/screen/         una pantalla por archivo
│   └── viewmodel/         un ViewModel por pantalla
├── ui/theme/              Color, Theme, Type
└── util/                  DateValidator, DateUtils
```

## Puesta en marcha

1. Crea un proyecto en [Firebase](https://console.firebase.google.com) y añade una app Android con el paquete **`es.scoutsafa.app`**.
2. Activa **Authentication → Correo/contraseña** y **Firestore Database**.
3. Descarga `google-services.json` y colócalo en `app/` (está en `.gitignore`; no se sube al repo).
4. Publica las reglas de `firestore.rules` (Firestore → Reglas).
5. Abre la carpeta en Android Studio, sincroniza Gradle y ejecuta.

> Si prefieres otro identificador de paquete, cámbialo en `app/build.gradle.kts` (`namespace` y `applicationId`), renombra la carpeta `es/scoutsafa/app` y regenera `google-services.json`.

## Roles

Todo el mundo se registra como `FAMILIA`. Para dar permisos de scouter, abre el documento del usuario en Firestore (`users/{uid}`) y cambia `role` a `SCOUTER`. Solo los scouters pueden crear/editar/borrar actividades y anuncios (lo hacen cumplir las reglas de `firestore.rules`, no solo la interfaz).

## Notificaciones

La app se suscribe a los topics `grupo` y `section_<RAMA>` al iniciar sesión y al cambiar de rama. Para avisar, envía un mensaje a ese topic desde **Firebase Console → Messaging**. Si añades `type = activity` en los datos del mensaje, se muestra en el canal "Actividades"; si no, en "Anuncios".

## Pendiente / a personalizar

- Sustituir el icono provisional (`res/drawable/ic_launcher_foreground.xml`) por el logo del grupo.
- Ajustar ramas, edades y colores a los del grupo (`Section.kt`, `ui/theme/Color.kt`).
- Los nombres de quien confirma asistencia solo se muestran a scouters en la app, pero cualquier usuario con sesión puede leer la colección `attendances` (hace falta para mostrar el recuento). Si eso es un problema de privacidad, mueve el recuento a un contador agregado.
