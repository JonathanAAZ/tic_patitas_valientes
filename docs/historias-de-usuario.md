# Historias de usuario — Patitas Valientes

Backlog de historias en la plantilla del documento de tesis. Cada historia va dentro de un
bloque de código para que el formato de la plantilla (la disposición de la cabecera, las
viñetas `•`) se conserve tal cual al copiarla al Word.

**Cómo añadir una historia nueva:** pégala en este archivo desde Android Studio o el Notepad,
dentro de un bloque de código, y avísame. La valido con la skill `validar-historia`, que
comprueba los criterios contra el código real y devuelve qué añadir, quitar o modificar.

## Estado

| Historia | Título | RF | Validada | Pendiente en el código |
|---|---|---|---|---|
| HU001 | Iniciar Sesión | RF001 | Sí | Credenciales de prueba precargadas en el layout |
| HU002 | Crear cuenta de usuario | RF002 | Sí | Nada |
| HU003 | Recuperar Contraseña | — | **Pendiente** | La función no existe todavía |
| HU004 | Cambiar contraseña | RF003 | Sí | Nada |
| HU005 | Editar información del perfil | RF004 | Sí | Nada |
| HU006 | Visualizar información del perfil | RF004 | Sí | Nada |
| HU007 | Buscar y filtrar información | RF005 | Sí | Nada |
| HU008 | Agregar mascota al catálogo | RF007 | Sí | Nada |
| HU009 | Editar mascotas del catálogo | RF008 | Sí | Nada |
| HU010 | Activar o desactivar mascotas | RF009 | Sí | Nada |
| HU011 | Activar o desactivar usuarios | RF010 | Sí | Nada |
| HU012 | Asignar seguimientos a voluntarios | RF011 | Sí | Nada |
| HU013 | Quitar seguimientos a voluntarios | RF012 | Sí | Nada |
| HU014 | Revisar reportes de animales abandonados | RF013 | **Pendiente** | Módulo sin empezar |
| HU015 | Aceptar o rechazar solicitudes de adopción | RF014 | Sí | Redacción de cinco avisos de fallo |
| HU016 | Agregar vacuna | RF016 | Sí | Nada |
| HU017 | Editar vacuna | RF016 | Sí | Nada |
| HU018 | Eliminar vacunas | RF017 | Sí | Falta el mensaje de éxito y el de fallo |
| HU019 | Agregar desparasitación | RF018 | Sí | Nada |
| HU020 | Editar desparasitación | RF019 | Sí | Nada |
| HU021 | Eliminar desparasitación | RF020 | Sí | Falta el mensaje de éxito y el de fallo |
| HU022 | Chat de seguimiento de adopción | RF021 | Sí | Nada |
| HU023 | Reportar animales abandonados | RF022 | **Pendiente** | Módulo sin empezar |
| HU024 | Editar reportes de animales abandonados | RF023 | **Pendiente** | Módulo sin empezar |
| HU025 | Visualizar los requisitos para adoptar | RF024 | Sí | Nada |
| HU026 | Enviar solicitud de adopción | RF025 | Sí | Dos cadenas de depuración en pantalla |
| HU027 | Completar proceso de adopción | RF026 | Sí | Redacción de cuatro avisos de fallo |
| HU028 – HU030 | *(bloques vacíos libres)* | — | — | — |

Al final del documento hay **dos anotaciones sin número**: el código QR de la mascota,
desprendido de HU008, y la lista de mascotas adoptadas, desprendida de HU007. Ninguna de las
dos está construida.

---

## HU001 — Iniciar Sesión

```
Título: 
Iniciar Sesión
Nro. o Código: 
HU001    Prioridad:
Alta    Estimación:
    Ref. Requerimiento:
RF001    Fecha de creación: 
29/04/2024
HISTORIA DE USUARIO
COMO usuario registrado QUIERO poder iniciar sesión con mi correo electrónico y contraseña PARA acceder a las funcionalidades del sistema de acuerdo al rol.
CRITERIOS DE ACEPTACIÓN
•    El sistema DEBE presentar una pantalla de inicio de sesión donde se solicite el correo electrónico y la contraseña.
•    El sistema DEBE marcar el campo correspondiente con un mensaje de error y NO DEBE enviar la petición cuando el correo electrónico o la contraseña estén vacíos.
•    El sistema DEBE marcar el campo correspondiente con un mensaje de error y NO DEBE enviar la petición cuando el correo electrónico tenga un formato inválido.
•    El sistema DEBE validar las credenciales ingresadas contra el servicio de autenticación.
•    El sistema DEBE presentar un único mensaje de error, sin indicar cuál de los dos datos es incorrecto, cuando las credenciales no sean válidas. Esto evita revelar qué correos se encuentran registrados.
•    El sistema DEBE verificar el estado y el rol de la cuenta antes de abrir la sesión.
•    El sistema NO DEBE abrir la sesión cuando la cuenta se encuentre en estado INACTIVO, y DEBE presentar un mensaje indicando que la cuenta está desactivada.
•    Si las credenciales son válidas y la cuenta está en estado ACTIVO, el sistema DEBE abrir la sesión y presentar la pantalla de inicio correspondiente al rol: [Administrador], [Voluntario] o [Adoptante].
•    El sistema DEBE asociar el dispositivo a la cuenta que inicia sesión y DEBE dejar de enviar a ese dispositivo las notificaciones de cualquier otra cuenta.
•    La pantalla de inicio de sesión DEBE presentar la opción: [Registrarse] (HU002). El acceso a [Recuperar Contraseña] se incorpora en HU003.
```

**Notas de la validación.** Ocho de los diez criterios son los originales con el fondo
corregido. Los dos nuevos —el bloqueo por cuenta `INACTIVO` y la asociación del dispositivo—
son reglas que el código ya aplicaba y que no estaban escritas. El mensaje único de
credenciales inválidas no es una elección de redacción: el proyecto tiene activada la
protección de enumeración de correo en Firebase, que devuelve el mismo error en los dos casos.

Pendiente en el código: las credenciales de prueba precargadas en
`activity_iniciar_sesion.xml:61` y `:88`.

---

## HU002 — Crear cuenta de usuario

```
Título: 
Crear cuenta de usuario
Nro. o Código: 
HU002    Prioridad:
Alta    Estimación:
    Ref. Requerimiento:
RF002    Fecha de creación: 
29/04/2024
HISTORIA DE USUARIO
COMO usuario QUIERO poder crear una cuenta dentro del sistema PARA acceder a las funcionalidades correspondientes a mi rol.
CRITERIOS DE ACEPTACIÓN
•    El sistema DEBE presentar una pantalla para crear una cuenta de usuario.
•    El sistema DEBE validar la cédula ingresada y DEBE presentar junto al campo, mientras se escribe, el mensaje [* Por favor, ingrese una cédula válida] cuando no sea válida.
•    El sistema DEBE validar el formato del correo electrónico y DEBE presentar junto al campo, mientras se escribe, el mensaje [* Por favor, ingrese un correo válido] cuando no sea válido.
•    El sistema DEBE verificar que el correo electrónico no se encuentre vinculado con otra cuenta y DEBE presentar el mensaje [Ya existe una cuenta vinculada a este correo electrónico] cuando ya esté en uso.
•    El sistema DEBE presentar el mensaje [Complete todos los campos solicitados] cuando existan campos obligatorios vacíos, y NO DEBE continuar al paso siguiente.
•    El sistema DEBE mostrar una alerta de confirmación o cancelación antes de guardar toda la información.
•    El sistema DEBE presentar el mensaje [Cuenta creada] únicamente cuando hayan terminado todas las escrituras de la cuenta, y NO DEBE anunciarlo antes.

En el caso de seleccionar la opción [Registrarse]:
•    El sistema DEBE presentar un formulario que contenga los campos: Nombre, Correo, Número de Cédula, Fecha de Nacimiento, Estado Civil, Ocupación, Número de Teléfono, Usuario de Instagram (Opcional), Nombre en Facebook (Opcional), Contraseña y Confirmación de Contraseña.
•    El sistema DEBE exigir para la contraseña un mínimo de 8 caracteres, una minúscula, una mayúscula, un número y un carácter especial.
•    El sistema DEBE indicar en pantalla, de forma individual y a medida que se escribe, cuáles de los cinco requisitos de la contraseña se cumplen y cuáles no.
•    El sistema DEBE verificar que la contraseña y su confirmación coincidan, y DEBE presentar un mensaje de error cuando no coincidan.
•    El sistema DEBE presentar, una vez validados los campos anteriores, un formulario que permita ingresar la información del domicilio: País, Provincia, Cantón, Parroquia, Barrio y Calles.
•    El sistema DEBE presentar a continuación una pantalla que permita agregar una foto de perfil, y DEBE permitir continuar sin agregarla.
•    El sistema DEBE asignar automáticamente el rol de Adoptante a la cuenta creada.

En caso de que la cuenta sea registrada por un usuario con rol Administrador:
•    El sistema DEBE presentar un formulario que contenga los campos: Nombre, Correo, Número de Cédula, Fecha de Nacimiento, Estado Civil, Ocupación y Número de Teléfono.
•    El sistema NO DEBE solicitar contraseña, usuario de Instagram ni nombre en Facebook.
•    El sistema NO DEBE solicitar información de domicilio ni foto de perfil.
•    El sistema DEBE asignar el rol de Voluntario a la nueva cuenta de usuario.
•    El sistema DEBE asignar el número de cédula del voluntario como su contraseña inicial.
•    El sistema DEBE mantener la pantalla abierta, indicando el motivo, cuando falle cualquiera de las escrituras, para permitir reintentar.
•    El sistema DEBE regresar a la lista de usuarios una vez creada la cuenta.
OBSERVACIONES
•    La foto de perfil no es obligatoria.
•    Al registrarse directamente en el sistema, el rol asignado será de adoptante. Para ser un voluntario, el administrador debe agregarlo.
•    La cuenta de voluntario se crea sin domicilio ni foto de perfil; ambos quedan disponibles para que el voluntario los complete desde su perfil.
•    Pendiente de revisión: la cédula como contraseña inicial no puede cambiarse mientras HU003 no exista, y se almacena en texto plano en la colección Cuentas. Se recomienda sustituirla por un correo de establecimiento de contraseña.
```

**Notas de la validación.** Todos los criterios se cumplen hoy en el código. El alta de
voluntarios se mantiene dentro de esta historia, con las dos secciones condicionadas por rol.
Los tres cambios de fondo respecto al original fueron: los textos reales de los errores en
línea de la cédula y el correo, el mensaje de éxito movido a la sección común con la
condición de no anunciarlo antes de tiempo, y la minúscula añadida a las cinco reglas de la
contraseña.

---

## HU003 — Recuperar Contraseña

Pendiente. La función no existe en el proyecto: no hay pantalla ni llamada a
`sendPasswordResetEmail`. Es la misma pieza que necesita el voluntario para dejar de usar su
cédula como contraseña, así que conviene resolver las dos cosas juntas.

---

## HU004 — Cambiar contraseña

```
Título: 
Cambiar contraseña
Nro. o Código: 
HU004	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF003	Fecha de creación: 
30/04/2024
HISTORIA DE USUARIO
COMO usuario registrado QUIERO poder cambiar mi contraseña desde la aplicación PARA mantener protegida mi cuenta.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE presentar la opción [Cambiar contraseña] en el menú de inicio de los tres roles: Administrador, Voluntario y Adoptante.
•	El sistema DEBE presentar un campo para ingresar la contraseña actual del usuario.
•	El sistema DEBE presentar un mensaje indicando que debe ingresar su contraseña actual cuando ese campo esté vacío, y NO DEBE enviar la petición.
•	El sistema DEBE verificar la contraseña actual contra el servicio de autenticación antes de permitir el cambio.
•	El sistema DEBE presentar el mensaje [Contraseña incorrecta] cuando la contraseña actual no sea válida, y NO DEBE habilitar los campos de la nueva contraseña.
•	El sistema DEBE mantener deshabilitados los campos de la nueva contraseña y su confirmación hasta que la contraseña actual haya sido verificada.
•	El sistema DEBE presentar, una vez verificada la contraseña actual, los campos para la nueva contraseña y su confirmación.
•	El sistema DEBE exigir para la nueva contraseña un mínimo de 8 caracteres, una minúscula, una mayúscula, un número y un carácter especial.
•	El sistema DEBE presentar la lista de los cinco requisitos en rojo, y DEBE pasar a verde cada requisito en el momento en que se cumple, a medida que se escribe.
•	El sistema DEBE presentar el mensaje [* Las contraseñas no coinciden] mientras la nueva contraseña y su confirmación sean distintas.
•	El sistema DEBE mantener deshabilitada la opción de guardar mientras no se cumplan los cinco requisitos o la confirmación no coincida.
•	El sistema DEBE mostrar una alerta de confirmación o cancelación antes de guardar la nueva contraseña, advirtiendo que se cerrará la sesión.
•	El sistema DEBE presentar el mensaje [Contraseña actualizada] cuando el cambio haya terminado, y NO DEBE anunciarlo antes.
•	El sistema DEBE presentar un mensaje indicando que no se pudo cambiar la contraseña cuando la operación falle, y DEBE mantener la pantalla abierta.
•	El sistema DEBE cerrar la sesión del usuario una vez cambiada la contraseña, y DEBE presentar la pantalla de Inicio de Sesión (HU001).
•	El sistema NO DEBE permitir regresar a las pantallas de la sesión anterior después del cambio.
OBSERVACIONES
•	La recuperación de la contraseña olvidada no forma parte de esta historia: este flujo exige la contraseña actual. Ver HU003.
•	La nueva contraseña se exige con las mismas cinco reglas que en el registro (HU002).
•	Al cerrarse la sesión, el usuario debe iniciarla de nuevo con la contraseña nueva.
```

**Notas de la validación.** La mecánica de validación de la nueva contraseña ya estaba
completa y es mejor de lo que la historia describía: las cinco reglas se comprueban en vivo en
`ControladorUtilidades.validarClaveYConfirmacionConjuntamente`, cada requisito pasa de rojo a
verde por separado, y la opción de guardar no se habilita hasta que todas se cumplen.

Dos cambios de fondo respecto al original: el QUIERO pasó de recuperar la contraseña olvidada
a cambiarla estando autenticado —recuperarla es HU003, y este flujo exige la contraseña
actual—, y se añadieron los criterios del cierre de sesión y de la pila de pantallas, que el
original no cubría.

**Implementado.** El botón de cambiar contraseña se añadió al inicio de voluntario y
adoptante (antes solo lo tenía el administrador). Al terminar, la pantalla cierra la sesión,
limpia el rol guardado y vuelve a Inicio de Sesión vaciando la pila. `cambiarClaveUsuario`
pasó a devolver el resultado por callback, de modo que el mensaje `Contraseña actualizada`
aparece cuando ya no quedan escrituras pendientes y el cierre de sesión ocurre después de
la última, no antes.

Queda pendiente, como en HU002: la contraseña nueva se sigue copiando en texto plano en la
colección `Cuentas` (`ControladorUsuario.actualizarClaveDB`). Es la misma decisión que arrastra
HU003 y no se tocó aquí.

---

## HU005 — Editar información del perfil

```
Título: 
Editar información del perfil
Nro. o Código: 
HU005	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF004	Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO usuario registrado QUIERO acceder a una sección de información personal PARA consultar y editar mis datos.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE permitir editar la información del perfil desde las opciones que presenta la pantalla de visualización (HU006).
•	El sistema DEBE ofrecer [Tomar una foto] y [Subir una foto] al editar la foto de perfil.
•	El sistema DEBE solicitar los permisos de cámara y de acceso a imágenes cuando no los tenga concedidos, y DEBE indicarlo cuando el permiso sea denegado.
•	El sistema DEBE presentar el mensaje [No se ha seleccionado foto de perfil] cuando se guarde la edición de la foto sin haber elegido una nueva.
•	El sistema DEBE presentar el mensaje [Foto de perfil actualizada] cuando la foto se haya guardado.
•	El sistema DEBE presentar la opción [Editar perfil], que abre un formulario para editar: Nombre, Número de Cédula, Fecha de Nacimiento, Estado Civil, Ocupación, Número de Teléfono, Usuario de Instagram (Opcional) y Nombre en Facebook (Opcional).
•	El sistema NO DEBE permitir editar el correo electrónico ni el rol de la cuenta.
•	El sistema DEBE recalcular la edad a partir de la fecha de nacimiento al guardar.
•	El sistema DEBE presentar el mensaje [Complete todos los campos solicitados] cuando existan campos obligatorios vacíos, y NO DEBE guardar.
•	El sistema DEBE presentar el mensaje [Información guardada] cuando los datos personales se hayan guardado.
•	El sistema DEBE presentar, a continuación, el formulario de edición del domicilio con los campos: País, Provincia, Cantón, Parroquia, Barrio y Calles.
•	El sistema DEBE presentar el mensaje [Domicilio actualizado] cuando el domicilio se haya guardado.
•	El sistema DEBE presentar un mensaje indicando que no se pudo guardar cuando cualquiera de las dos operaciones falle, y DEBE mantener la pantalla abierta.

En el caso del usuario con rol de Administrador:
•	El sistema DEBE presentar una opción para acceder a la lista de todos los usuarios registrados.
•	Para cada usuario de la lista, el sistema DEBE presentar su foto, Nombre, Cédula, Rol y Estado, junto con las opciones [Editar] y [Activar] o [Desactivar] según el estado de la cuenta (HU012).
•	Al seleccionar [Editar], el sistema DEBE presentar la pantalla de información del perfil del usuario seleccionado.
•	El sistema DEBE permitir al administrador editar los datos personales de cualquier usuario.
•	El sistema NO DEBE permitir al administrador editar la foto de perfil ni el domicilio de otro usuario.
OBSERVACIONES
•	El correo electrónico no es editable porque identifica a la cuenta en el servicio de autenticación.
•	Las cuentas no se eliminan, se desactivan: borrarlas dejaría sin referencia las adopciones, contratos y seguimientos asociados.
```

**Notas de la validación.** Los dos puntos de entrada son distintos: el perfil propio es
`PerfilFragment`, compartido por los tres roles vía `bMPerfil`; el administrador entra por
`VerUsuariosActivity`, y `ListaUsuariosAdaptador:128` abre `VerInformacionPerfilActivity` con
`enAdministracion = true`, que solo ofrece editar la información, sin foto.

`EditarInformacionPerfilActivity.editarPerfil:278` guarda exactamente los ocho campos
editables y recalcula la edad; al guardar navega a `EditarDomicilioActivity`, que escribe en
`Domicilios`. `EditarFotoPerfilActivity` tiene las dos vías con sus permisos y avisa si se
guarda sin elegir foto.

Tres cambios de fondo respecto al original:

1. `[Eliminar usuario]` pasó a `[Activar]` / `[Desactivar]`. La aplicación no borra cuentas, y
   no debe: dejaría sin referencia las adopciones, los contratos y los seguimientos asociados.
   La confusión viene de los ids del layout, que se llaman `lYBtnEliminarUsuario` e
   `ivEliminarUsuarioLista` pero muestran «Activar» o «Desactivar».
2. Se añadieron por escrito los mensajes que el código ya tiene y la historia no recogía:
   `[Foto de perfil actualizada]`, `[Domicilio actualizado]` y el de fallo.
3. El criterio de la lista ahora nombra los cuatro campos que realmente muestra —foto,
   Nombre, Cédula, Rol y Estado— en lugar de «su información» completa.

**Implementado.** El administrador ya no pasa por el formulario de domicilio: al guardar los
datos de un usuario vuelve a la información de su perfil
(`EditarInformacionPerfilActivity.cargarInformacionBD`). Se activó el mensaje
`[Información guardada]`, que estaba escrito y comentado, así que ahora guardar el perfil
confirma algo. Se quitó el `Toast` de depuración «PROVINCIA BDD…» que se veía al abrir la
edición del domicilio. Y se unificaron los mensajes de error: «Algo salió mal», «Error al
obtener el usuario», «Existe campos vacíos» y la errata «No se encontraró el usuario» pasaron
a `No se pudo obtener la información del perfil`, `No se pudo obtener la información del
domicilio`, `No se pudo guardar la foto de perfil` y `Complete todos los campos solicitados`
según la pantalla.

Queda una rama inalcanzable: el `if (enAdministracion)` de `EditarDomicilioActivity` ya no
puede darse, porque el administrador no llega a esa pantalla. Se dejó como guarda.

Al validar HU006 se devolvieron a esa historia los criterios de visualización que esta había
absorbido —los datos mostrados, el domicilio y la imagen por defecto—. HU006 no estaba escrita
cuando se validó HU005, y las dos se reparten la misma pantalla: HU006 es la consulta, HU005
la edición.

---

## HU006 — Visualizar información del perfil

```
Título: 
Visualizar información del perfil
Nro. o Código: 
HU006	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF004	Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO usuario registrado QUIERO acceder a una sección de información personal PARA visualizar la información que ingresé con anterioridad.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE presentar una opción para acceder a la [Información de perfil].
•	El sistema DEBE presentar dos secciones: [Información personal] e [Información del domicilio].
•	En la sección [Información personal], el sistema DEBE presentar los siguientes datos: Nombre, Rol en el sistema, Correo, Número de Cédula, Fecha de Nacimiento, Edad, Estado Civil, Ocupación, Número de Teléfono, Usuario de Instagram (Opcional) y Nombre en Facebook (Opcional).
•	En la sección [Información del domicilio], el sistema DEBE presentar los siguientes datos: País, Provincia, Cantón, Parroquia, Barrio y Calles.
•	El sistema DEBE presentar la foto de perfil de la cuenta, y DEBE presentar una imagen por defecto cuando la cuenta no tenga foto.
•	El sistema DEBE presentar una opción para [Editar información] (HU005).
•	El sistema DEBE presentar una opción para [Editar foto de perfil] (HU005).
•	El sistema DEBE presentar un mensaje indicando que no se pudo obtener la información cuando la consulta falle.

En el caso del usuario con rol de Administrador:
•	Al seleccionar la opción [Editar] de un ítem de la lista de usuarios, el sistema DEBE presentar la pantalla de visualización de la información de perfil de ese usuario.
•	El sistema DEBE presentar en esa pantalla los mismos datos personales y de domicilio que en el perfil propio.
•	El sistema DEBE presentar una opción para [Editar información], igual que en el perfil propio.
•	El sistema NO DEBE presentar la opción de editar la foto de perfil del usuario consultado.
OBSERVACIONES
•	Debido a que la foto de perfil no es obligatoria, se presentará una imagen básica de perfil en ese espacio.
•	Esta historia cubre solo la consulta. Lo que ocurre al seleccionar [Editar información] o [Editar foto de perfil] está en HU005.
```

**Notas de la validación.** Son dos pantallas con el mismo contenido: `PerfilFragment` para el
perfil propio, al que los tres roles llegan por la opción Perfil del menú inferior, y
`VerInformacionPerfilActivity` para el administrador consultando a otro usuario, al que llega
desde la lista (`ListaUsuariosAdaptador:128`). Las dos pintan los once datos personales y los
seis del domicilio, y las dos ofrecen «Editar información»; solo `PerfilFragment` ofrece editar
la foto, que es lo que la observación pedía.

**Implementado: la imagen por defecto.** `Glide.load(fotoPerfilID)` no llevaba
`.placeholder()`, `.fallback()` ni `.error()`, y el `ImageView` no tiene `android:src` —solo un
borde con tinte celeste—, así que una cuenta sin foto dejaba el recuadro vacío en lugar de la
imagen básica. Se añadieron los tres en las dos pantallas, con
`logo_patitas_valientes`, que es el fallback que el resto de la aplicación ya usa:
`fallback` cubre el campo nulo y `error` la carga fallida, que son dos casos distintos.

Quedan dos detalles menores de la misma pantalla, que no afectan a ningún criterio: el
encabezado dice «Información personal y domicilio» en una sola cabecera, de modo que la
sección [Información personal] no tiene título propio como pide el criterio; y
`VerInformacionPerfilActivity` todavía importa Picasso y conserva su llamada comentada, cuando
la casa usa Glide.

**Sobre el solapamiento con HU005.** Las dos historias comparten RF004 y se reparten la misma
pantalla: HU006 es la consulta, HU005 la edición. Al validar HU005 yo había absorbido en ella
los criterios de visualización —los datos mostrados, el domicilio, la imagen por defecto—
porque HU006 todavía no estaba escrita. Ya se devolvieron aquí, y HU005 arranca ahora
remitiendo a esta historia. Conviene además que HU006 vaya antes que HU005 en el documento,
porque se consulta antes de editar, pero la renumeración es decisión tuya.

---

## HU007 — Buscar y filtrar información

```
Título: 
Buscar y filtrar información
Nro. o Código: 
HU007	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF005	Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO usuario registrado QUIERO buscar y filtrar la información de las listas PARA encontrar lo que necesito sin recorrerlas completas.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE presentar una barra de búsqueda en las pantallas de catálogo de mascotas, lista de mascotas registradas y lista de usuarios.
•	El sistema DEBE actualizar los resultados a medida que se escribe, sin necesidad de presionar un botón.
•	El sistema DEBE presentar un aviso de que no se encontraron resultados cuando la búsqueda no devuelva ninguno.
•	El sistema DEBE presentar, junto a la barra de búsqueda, un botón con un ícono de [Filtro] que despliegue u oculte los filtros disponibles, en las pantallas que tengan filtros.
•	El sistema DEBE permitir quitar los filtros seleccionados y volver a la lista completa.

En el caso del catálogo de mascotas, para el Adoptante:
•	El sistema DEBE presentar una pantalla con las mascotas disponibles para adopción.
•	El sistema DEBE presentar las mascotas en forma de cuadrícula de dos columnas.
•	Cada ítem de la cuadrícula DEBE mostrar: Foto, Nombre, Edad (Cachorro/Adulto), Especie (Canino/Felino) y Sexo (Macho/Hembra).
•	El sistema DEBE permitir la búsqueda por nombre de la mascota.
•	El sistema DEBE presentar los filtros: Por edad (Cachorros/Adultos), Por especie (Caninos/Felinos), Por sexo (Machos/Hembras) y Por estado sanitario (Vacunados/Esterilizados).

En el caso de la lista de mascotas registradas, para el Administrador:
•	El sistema DEBE presentar las mascotas del refugio en forma de lista.
•	Cada ítem de la lista DEBE mostrar: Foto, Nombre, Vacunación, Desparasitación, Esterilización y Estado (Activo/Inactivo).
•	El sistema DEBE permitir la búsqueda por nombre de la mascota.
•	El sistema DEBE presentar los filtros: Activos/Inactivos, Vacunados/No vacunados, Esterilizados/No esterilizados y Desparasitados/No desparasitados.

En el caso de la lista de usuarios, para el Administrador:
•	El sistema DEBE presentar una pantalla con todos los usuarios registrados, en forma de lista.
•	Cada ítem de la lista DEBE mostrar: Foto de perfil, Nombre, Número de Cédula, Rol en el sistema y Estado de la cuenta.
•	El sistema DEBE permitir la búsqueda por nombre y por número de cédula.
OBSERVACIONES
•	La lista de mascotas adoptadas, con los datos del adoptante y del voluntario asignado, no forma parte de esta historia porque todavía no está construida. Ver la anotación al final de este documento.
•	Esta historia cubre la búsqueda y el filtrado. Lo que ocurre al seleccionar un ítem de cualquiera de las tres listas corresponde a las historias de cada función.
```

**Notas de la validación.** Tres de las cuatro secciones de tu historia original ya funcionan,
y la cuarta no existe. Por eso se separó.

**El catálogo del adoptante** es `CatalogoMascotasActivity`: `GridLayoutManager(this, 2)` da la
cuadrícula de dos columnas, el `SearchView` filtra por nombre en vivo
(`onQueryTextChange:147`), `ivFiltroCatalogoMascotas` con `ic_filtro` despliega
`gLMostrarFiltrosCatalogoMascotas`, y «Quitar filtro seleccionado» lo limpia. Los filtros son
**ocho, no seis**: a edad, especie y sexo se suman Vacunados y Esterilizados, que la historia
original no recogía.

**La lista de mascotas en adopción** es `ListaMascotasAdopcionFragment`, montado dentro de
`VerMascotasActivity`. Tiene su `SearchView`, su `ivFiltro`, «Quitar filtros seleccionados» y
exactamente los ocho filtros del criterio. El ítem muestra los seis campos que pedías.

**La lista de usuarios** es `VerUsuariosActivity`. El filtrado es automático
(`onQueryTextChange:251` → `ControladorUsuario.filtrarInformación:184`) y hay aviso de sin
resultados. No tiene filtros: el `Spinner` está comentado en el layout, solo hay búsqueda, así
que el criterio del botón de filtro se limitó a «las pantallas que tengan filtros». El ítem
muestra cinco campos, no cuatro: también el Estado de la cuenta.

> **Un matiz de la búsqueda de usuarios.** `filtrarInformación` busca por nombre y, **solo si
> no encuentra ninguna coincidencia por nombre**, vuelve a buscar por cédula. Es un «o
> excluyente», no un «y»: si algún nombre coincide, las cédulas que también coincidirían no
> aparecen. En la práctica apenas se nota, pero el criterio dice «por nombre y por número de
> cédula». Si quieres el comportamiento literal, es unir los dos resultados en lugar de
> encadenarlos.

**Implementado: `VerMascotasActivity` tenía tres defectos de la lista de «errores a NO
repetir».**

1. `setContentView(R.layout.activity_ver_mascotas)` y después
   `ActivityVerMascotasBinding.inflate(...)` **sin** `setContentView(binding.getRoot())`. El
   binding inflaba una segunda copia del layout que nunca se mostraba, de modo que cualquier
   `binding.algo` habría manipulado vistas invisibles. No se notaba porque el único uso del
   binding era la propia asignación. Ahora hay un solo `setContentView`, con el binding.
2. Los insets se aplicaban sobre `findViewById(R.id.main)`; ahora sobre `binding.main`.
3. `iVRegresarVerMascotas` no tenía listener y la flecha de regresar no hacía nada; ya cierra
   la pantalla.

Sigue pendiente, y es la anotación del final: el botón «Lista de mascotas adoptadas» continúa
sin listener, porque la lista que debería abrir no existe.

Y los ids del ítem de mascota repiten el problema de la lista de usuarios:
`ivEliminarMascotaLista` y `lYBtnEliminarMascota` dicen *eliminar* y el texto que muestran es
«Activar» o «Desactivar».

---

## HU008 — Agregar mascota al catálogo

```
Título: 
Agregar mascota al catálogo
Nro. o Código: 
HU008	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF007	Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO administrador QUIERO agregar una mascota al catálogo PARA que su información se presente a las personas que deseen adoptar.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE presentar una opción para acceder a la lista de mascotas registradas (HU007).
•	El sistema DEBE presentar, dentro de esa lista, una opción para [Agregar mascota].
•	El sistema DEBE presentar un formulario que contenga los campos: Nombre, Especie, Raza, Edad (Cachorro/Adulto), Sexo (Macho/Hembra), Color, Carácter, Vacunación (Sí/No), Desparasitación (Sí/No), Esterilización (Sí/No) con su fecha, y Domicilio.
•	El sistema DEBE limitar el campo Carácter a 140 caracteres.
•	El sistema DEBE permitir seleccionar entre dos domicilios: Refugio y Hogar temporal.
•	El sistema DEBE permitir seleccionar el voluntario correspondiente cuando el domicilio sea un hogar temporal, y DEBE señalar el campo cuando no se haya seleccionado ninguno.
•	El sistema DEBE presentar a continuación una pantalla para agregar la foto de la mascota, y DEBE ofrecer [Tomar una foto] y [Subir una foto].
•	El sistema NO DEBE permitir guardar sin foto, y DEBE indicarlo.
•	El sistema DEBE presentar el mensaje [Complete todos los campos solicitados] cuando existan campos vacíos o sin seleccionar, y NO DEBE continuar.
•	El sistema DEBE mostrar una alerta de confirmación o cancelación antes de guardar.
•	El sistema DEBE presentar el mensaje [Mascota registrada] cuando la mascota se haya guardado, y NO DEBE anunciarlo antes.
•	El sistema DEBE presentar un mensaje indicando que no se pudo registrar la mascota cuando la operación falle.
OBSERVACIONES
•	La generación del código QR no forma parte de esta historia: no está implementada. Ver la anotación al final de este documento.
•	La mascota se crea en estado Activo y entra en la lista de mascotas en adopción.
```

**Notas de la validación.** El recorrido es `ListaMascotasAdopcionFragment` → «Agregar mascota»
→ `AgregarMascotaFragment` → `AgregarFotoMascotaFragment` → `ControladorMascota`. La alerta de
confirmación está en `AgregarFotoMascotaFragment:235` («¿Desea guardar la información
proporcionada?»), el guardado en `ControladorMascota.guardarMascotaEnFirebase:111`, y el aviso
de éxito es «Mascota registrada» (`:131`), que ya cumple la convención mejor que el
«Información guardada correctamente» del original.

El formulario tiene **más campos de los que listaba la historia**: además de los tuyos están
Especie, Raza y la fecha de esterilización. Y el domicilio en la interfaz se llama **Hogar
temporal**, no «Domicilio de voluntario»; se eligió el nombre de la interfaz para que el
criterio sea comprobable.

**Un criterio del original no se podía cumplir, y otro estaba desajustado.**

1. **El límite del campo Carácter existía, pero en 150.** `eTCaracterMascota` ya tenía
   `android:maxLength`, con el valor equivocado. Se ajustó a 140 en los dos formularios, el de
   agregar y el de editar, para que coincida con el criterio.
2. **No hay campo de ubicación.** Al elegir hogar temporal solo se selecciona el voluntario de
   un `Spinner`; no se pide ninguna dirección ni coordenada. El criterio original decía
   «seleccionar el respectivo voluntario y agregar la ubicación», y la segunda mitad no está.
   Se reescribió el criterio a lo que sí hace: seleccionar el voluntario y señalar el campo si
   no se elige.

**El código QR no existe.** `Mascota` tiene el campo `codigoQR` con su getter, su setter y su
serialización en el `Parcelable`, pero **nadie lo escribe ni lo lee**, y el proyecto no tiene
ninguna dependencia de generación de códigos (ni ZXing ni equivalente). Es un campo muerto en
el modelo. Por eso el criterio salió de esta historia y quedó como anotación aparte: es trabajo
sin empezar, y mezclarlo impediría cerrar HU008.

**Implementado.** El límite pasó de 150 a 140 caracteres. Se quitó el `Toast` de depuración
«ESTE ES EL DOMICILIO: …» que `AgregarMascotaFragment` mostraba en pantalla, y el mensaje de
campos vacíos pasó de «Existen campos vacíos o sin seleccionar» a `Complete todos los campos
solicitados`.

La referencia «(HU005)» del original apuntaba al perfil de usuario; la lista de mascotas es
HU007.

---

## HU009 — Editar mascotas del catálogo

```
Título: 
Editar mascotas del catálogo
Nro. o Código: 
HU009	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF008	Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO administrador QUIERO acceder a la información de cada mascota PARA editar sus datos y mantenerlos actualizados.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE presentar una opción para acceder a la lista de mascotas registradas (HU007).
•	Para cada ítem de la lista, el sistema DEBE presentar la información de la mascota junto con las opciones [Editar] y [Activar] o [Desactivar] según su estado (HU010).
•	Al seleccionar [Editar], el sistema DEBE presentar un formulario con la información actual de la mascota ya cargada.
•	El sistema DEBE permitir editar: Nombre, Especie, Raza, Edad, Sexo, Color, Carácter, Vacunación, Desparasitación, Esterilización con su fecha, y Domicilio.
•	El sistema DEBE permitir cambiar la foto de la mascota, ofreciendo [Tomar una foto] y [Subir una foto].
•	El sistema DEBE conservar la foto actual cuando no se seleccione una nueva.
•	El sistema DEBE presentar el mensaje [Complete todos los campos solicitados] cuando existan campos vacíos, y NO DEBE guardar.
•	El sistema DEBE presentar el mensaje [Mascota actualizada] cuando los cambios se hayan guardado, y NO DEBE anunciarlo antes.
•	El sistema DEBE presentar un mensaje indicando que no se pudo guardar cuando la operación falle, y DEBE mantener la pantalla abierta.
•	El sistema DEBE regresar a la lista de mascotas una vez guardados los cambios.
OBSERVACIONES
•	El historial médico —vacunas y desparasitaciones— se registra y edita en su propia pantalla, no en este formulario. Corresponde a otra historia.
•	La mascota no se elimina: se desactiva. Ver HU010.
```

**Notas de la validación.** `EditarMascotaActivity` se abre desde el botón «Editar» del ítem de
la lista, llega con la información cargada y guarda por `ControladorMascota`, que avisa
«Mascota actualizada» (`:292`) y vuelve a la lista (`:295`). El mensaje de campos vacíos ya es
el unificado (`EditarMascotaActivity:418`). La foto se cambia con las dos vías y sus permisos, y
si no se elige una nueva se conserva la anterior.

**Tres cosas del original que no encajaban.**

1. **No existe el botón [Cancelar].** Lo busqué en `activity_editar_mascota.xml` y no está: se
   sale con la flecha de regresar. El criterio se quitó; si lo quieres, es añadir el botón.
2. **El registro de vacunas y desparasitaciones no se edita aquí.** Es el historial médico, que
   tiene su propia pantalla y su propio flujo. Las dos referencias del original quedaron en
   «(HU00)» sin número, señal de que todavía no les habías asignado historia. Se movió a
   observación.
3. **«Eliminar animal» no existe.** El segundo botón del ítem activa o desactiva según el
   estado, igual que en la lista de usuarios. Se corrigió el criterio y la referencia, que
   apuntaba a HU011 cuando la desactivación de mascotas es HU010.

También se ampliaron los campos editables: el formulario permite editar Especie, Raza y la
fecha de esterilización, que el original no listaba, y la Desparasitación, que tampoco estaba.

La referencia «la información del animal (HU005)» apuntaba al perfil de usuario; es HU007.

---

## HU010 — Activar o desactivar mascotas del catálogo

```
Título: 
Activar o desactivar mascotas del catálogo
Nro. o Código: 
HU010	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF009	Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO administrador QUIERO retirar del catálogo las mascotas que ya no están disponibles, y volver a publicarlas si lo están PARA que los adoptantes solo vean las que de verdad pueden adoptar.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE presentar una opción para acceder a la lista de mascotas registradas (HU007).
•	Para cada ítem de la lista, el sistema DEBE presentar la información de la mascota junto con las opciones [Editar] (HU009) y [Activar] o [Desactivar] según su estado actual.
•	El sistema DEBE presentar la opción [Desactivar] cuando la mascota esté Activa, y [Activar] cuando esté Inactiva.
•	Al seleccionar esa opción, el sistema DEBE mostrar una alerta de confirmación o cancelación que indique cuál de las dos acciones se va a realizar.
•	El sistema DEBE cambiar el estado de la mascota a Inactivo cuando esté Activa, y a Activo cuando esté Inactiva.
•	El sistema NO DEBE eliminar el registro de la mascota.
•	El sistema DEBE presentar el mensaje [Estado actualizado] cuando el cambio se haya guardado.
•	El sistema DEBE presentar un mensaje indicando que no se pudo actualizar el estado cuando la operación falle.
•	El sistema DEBE refrescar la lista para reflejar el nuevo estado.
•	El sistema NO DEBE presentar en el catálogo del adoptante las mascotas que estén Inactivas.
OBSERVACIONES
•	El borrado es lógico: la mascota conserva su registro, su historial médico y su relación con adopciones y seguimientos. Eliminarla dejaría esos datos sin referencia.
•	La acción es reversible, y por eso la historia se titula activar o desactivar en lugar de eliminar.
```

**Notas de la validación.** Funciona desde el segundo botón del ítem en
`ListaMascotasAdopcionAdaptador:134`. La alerta se titula «Confirmar cambio de estado» y el
texto cambia según el estado actual: «¿Está seguro/a de que desea desactivar esta mascota?» o
«…activar…» (`:138-140`). El cambio lo hace `ControladorMascota`, que lee el estado actual y lo
invierte (`:226`), y al terminar recarga `ListaMascotasAdopcionFragment` para que la lista
refleje el nuevo estado.

**Cambió el título y el fondo de la historia.** El original se llamaba «Eliminar mascota del
catálogo», pero sus propios criterios decían «DEBE cambiar el estado del animal a Inactivo»:
describía una desactivación con nombre de borrado. Y la aplicación hace algo más de lo que el
original recogía — la acción es **reversible**, el mismo botón vuelve a activar. Por eso ahora
el título y los criterios hablan de activar o desactivar, y se añadió el criterio de que el
registro no se elimina.

El mensaje `[Se ha eliminado correctamente]` se cambió por `[Estado actualizado]`: sin adverbio
de relleno, y sobre todo sin decir «eliminado» de algo que sigue existiendo. Es además el
mismo texto que ya usa la desactivación de usuarios, así que las dos acciones equivalentes
dicen lo mismo.

**Implementado.** Ese mensaje no existía: al terminar, el controlador recargaba el fragment en
silencio y el administrador no recibía ninguna confirmación, a diferencia de la desactivación
de usuarios. Ya muestra `Estado actualizado`, el mismo texto que la de usuarios. Y los dos
avisos de fallo, que decían «Error al actualizar estado en la base de datos» y «Error al
obtener el estado actual de la mascota», pasaron a `No se pudo actualizar el estado de la
mascota` y `No se pudo obtener el estado de la mascota`. De paso, «Hubo un error al guardar la
información en la base de datos» pasó a `No se pudo guardar la información de la mascota`.

La referencia «[Editar información] (HU010)» del original se apuntaba a sí misma; editar es
HU009. Y «la información del animal (HU005)» es HU007.

---

## HU011 — Activar o desactivar usuarios

```
Título: 
Activar o desactivar usuarios
Nro. o Código: 
HU011	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF010	Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO administrador QUIERO activar y desactivar las cuentas de los usuarios PARA controlar quién puede acceder al sistema.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE presentar una opción para acceder a la lista de todos los usuarios registrados (HU007).
•	Para cada ítem de la lista, el sistema DEBE presentar su información junto con las opciones [Editar] (HU005) y [Activar] o [Desactivar] según el estado de la cuenta.
•	El sistema DEBE presentar la opción [Desactivar] cuando la cuenta esté Activa, y [Activar] cuando esté Inactiva.
•	Al seleccionar esa opción, el sistema DEBE mostrar una alerta de confirmación o cancelación que indique cuál de las dos acciones se va a realizar.
•	El sistema DEBE cambiar el estado de la cuenta a Inactivo cuando esté Activa, y a Activo cuando esté Inactiva.
•	El sistema NO DEBE eliminar el registro del usuario ni de su cuenta.
•	El sistema DEBE presentar el mensaje [Estado actualizado] cuando el cambio se haya guardado.
•	El sistema DEBE presentar un mensaje indicando que no se pudo actualizar el estado cuando la operación falle.
•	El sistema DEBE refrescar la lista para reflejar el nuevo estado.
•	El sistema NO DEBE permitir iniciar sesión a una cuenta Inactiva, y DEBE indicar que la cuenta está desactivada (HU001).
OBSERVACIONES
•	El borrado es lógico: la cuenta conserva su registro y su relación con adopciones, contratos y seguimientos. Eliminarla dejaría esos datos sin referencia.
•	Un voluntario desactivado deja de aparecer en la lista de voluntarios a los que se puede asignar un seguimiento (HU012).
```

**Notas de la validación.** Es la historia que llega más entera. Funciona desde el segundo
botón del ítem en `ListaUsuariosAdaptador:138`: el texto del botón cambia entre «Activar» y
«Desactivar» según el estado y el icono y el color cambian con él (`:89-95`), la alerta avisa
«¿Está seguro/a de que desea desactivar este usuario?» o «…activar…» (`:143-144`), y al
terminar muestra «Estado actualizado» (`:155`). El bloqueo en el inicio de sesión ya estaba
verificado en HU001.

**El mensaje del original estaba equivocado.** Decía `[Se ha eliminado correctamente]`, que
parece copiado de la historia de la mascota: ni se elimina nada ni la operación es siempre una
desactivación —el mismo botón también activa—. El código ya dice lo correcto.

Se añadieron dos criterios que el original no recogía y el código sí cumple: que el registro no
se elimina, y que una cuenta inactiva no puede iniciar sesión. El segundo es lo que le da
sentido a toda la historia; sin él, desactivar no se nota en ninguna parte.

Las referencias del original estaban desplazadas: «su información (HU005)» es la lista de
usuarios, HU007; y «[Editar usuario] (HU004)» es HU005, porque HU004 pasó a ser el cambio de
contraseña.

**Nada pendiente en el código para esta historia.**

---

## HU012 — Asignar seguimientos a voluntarios

```
Título: 
Asignar seguimientos a voluntarios
Nro. o Código: 
HU012	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF011	Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO administrador QUIERO asignar los seguimientos a un voluntario en concreto PARA que alguien supervise el estado de cada mascota adoptada.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE presentar una opción para [Gestionar seguimientos].
•	El sistema DEBE presentar la lista de los voluntarios con cuenta activa.
•	Cada ítem de la lista DEBE mostrar: Foto de perfil, Nombre, Número de Cédula y Cantidad de seguimientos asignados, junto con la opción [Ver seguimientos].
•	Al seleccionar [Ver seguimientos], el sistema DEBE presentar una pantalla con dos opciones: [Asignar seguimientos], que lista los seguimientos sin voluntario, y [Seguimientos asignados], que lista los de ese voluntario.
•	Cada ítem de la lista de seguimientos disponibles DEBE mostrar: Foto del adoptante, Nombre del adoptante, Nombre de la mascota y la opción [Asignar al voluntario].
•	Al seleccionar [Asignar al voluntario], el sistema DEBE mostrar una alerta de confirmación o cancelación.
•	El sistema DEBE asignar el seguimiento al voluntario seleccionado.
•	El sistema DEBE presentar el mensaje [Seguimiento asignado] cuando la asignación se haya guardado, y NO DEBE anunciarlo antes.
•	El sistema DEBE enviar una notificación al voluntario al que se le asignó el seguimiento.
•	El sistema DEBE actualizar las dos listas tras la asignación, de modo que el seguimiento salga de los disponibles y aparezca en los del voluntario.
•	El sistema DEBE presentar un mensaje indicando que no se pudo asignar el seguimiento cuando la operación falle.
OBSERVACIONES
•	Solo se listan los voluntarios con cuenta activa: un voluntario desactivado (HU011) no puede recibir seguimientos.
•	La lista de seguimientos del voluntario ofrece además [Quitar seguimiento], que lo devuelve a los disponibles. Eso es la reasignación y corresponde a otra historia.
•	Los recordatorios del plan de seguimiento llegan al voluntario asignado, no al adoptante.
```

**Notas de la validación.** El recorrido completo existe: `GestionarSeguimientosActivity` lista
los voluntarios, `SeguimientosVoluntarioActivity` presenta las dos listas con sus dos botones
—«Asignar seguimientos» y «Seguimientos asignados», sobre `recyViewAsignarSegui` y
`recyViewSeguiAsignados`—, y `ControladorSeguimiento.asignarSeguimientoVoluntario:784` hace la
asignación, avisa «Seguimiento asignado» (`:813`) y **envía la notificación** al voluntario con
`enviarNotificacionAsignacionSeguimiento`. El ítem del voluntario muestra foto, nombre, cédula
y el número de seguimientos; el del seguimiento disponible muestra foto del adoptante, nombre y
mascota, con «Asignar al voluntario».

**Dos cosas del original que no coincidían.**

1. **No existe el campo Apellido.** El modelo `Usuario` guarda un único campo `nombre`; no hay
   `apellido` en ningún modelo del proyecto. El criterio se ajustó a los cuatro datos que la
   lista muestra de verdad.
2. **La lista no es de «todos los voluntarios».** `GestionarSeguimientosActivity` filtra por
   rol Voluntario **y estado activo** (`:95-96`), así que un voluntario desactivado no aparece.
   Es coherente con HU011 y es mejor que lo escrito, así que se recogió como criterio.

**La alerta de confirmación sí existe.** `ListaSeguimientosAdaptador` la monta con
`crearAlertaPersonalizada` y conecta los botones a mano, en lugar de usar
`crearAlertaConfirmacion`: el mensaje es «¿Seguro que desea asignar este seguimiento a
⟨nombre⟩?» y la asignación solo ocurre al pulsar Aceptar. El criterio se cumple.

**Implementado: el conteo salió de `onBindViewHolder`.** El adaptador de la lista de
voluntarios consultaba Firestore para contar los seguimientos de cada uno: una lectura por
fila, cada vez que la fila entraba en pantalla, que es justo lo que el CLAUDE.md prohíbe.
Además pintaba dentro del callback con el `position` capturado, así que si la fila se reciclaba
antes de llegar la respuesta se escribían los datos del voluntario equivocado; la foto no tenía
el `else` de reserva; y si la consulta fallaba no se pintaba nada, de modo que la fila
conservaba el nombre, la cédula y el número del voluntario anterior.

Ahora `ControladorSeguimiento.contarSeguimientosPorVoluntario` cuenta todo en **una sola
consulta** y devuelve un mapa por id de voluntario;  `GestionarSeguimientosActivity` lo pide
antes de armar el adaptador y se lo pasa hecho. `onBindViewHolder` pinta síncrono, con el
`else` de la foto, y si el conteo falla la lista se pinta con cero en lugar de quedarse vacía.
El ítem se infla con el `parent`, no con `null`, para que respete los `LayoutParams`.

También se corrigió el aviso de fallo de la asignación, que decía «Error al asignar el
seguimiento.».

---

## HU013 — Quitar seguimientos a voluntarios

```
Título: 
Quitar seguimientos a voluntarios
Nro. o Código: 
HU013	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF012	Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO administrador QUIERO quitar un seguimiento a un voluntario PARA reasignarlo a otro cuando el primero no pueda continuar.
CRITERIOS DE ACEPTACIÓN
•	Al seleccionar [Ver seguimientos] de un voluntario de la lista (HU012), el sistema DEBE presentar las dos listas: los seguimientos disponibles y los asignados a ese voluntario.
•	Cada ítem de la lista de seguimientos asignados DEBE mostrar: Foto del adoptante, Nombre del adoptante, Nombre de la mascota y la opción [Quitar seguimiento].
•	Al seleccionar [Quitar seguimiento], el sistema DEBE presentar una ventana que permita seleccionar el voluntario al que se reasigna el seguimiento.
•	El sistema DEBE ofrecer en esa ventana únicamente voluntarios con cuenta activa, y DEBE señalar el campo cuando no se haya seleccionado ninguno.
•	El sistema DEBE permitir cerrar esa ventana sin reasignar.
•	Al confirmar la reasignación, el sistema DEBE quitar el seguimiento al voluntario actual y asignarlo al seleccionado.
•	El sistema DEBE presentar el mensaje [Seguimiento reasignado] cuando el cambio se haya guardado, y NO DEBE anunciarlo antes.
•	El sistema DEBE enviar una notificación al voluntario al que se le reasignó el seguimiento.
•	El sistema DEBE actualizar las dos listas tras la reasignación.
•	El sistema DEBE presentar un mensaje indicando que no se pudo reasignar el seguimiento cuando la operación falle.
OBSERVACIONES
•	La reasignación no devuelve el seguimiento a la lista de disponibles: pasa directamente del voluntario actual al nuevo.
•	El historial de la conversación del seguimiento se conserva: lo que cambia es quién lo atiende.
```

**Notas de la validación.** Funciona desde el botón [Quitar seguimiento] del ítem
(`ListaSeguimientosAdaptador`), que abre `configurarDialogReasignar` con el layout
`dialog_asignar_otro_voluntario.xml`: un `Spinner` de voluntarios, el texto «Por favor,
seleccione un voluntario para reasignar el seguimiento actual.» y un botón de salida.
`ControladorSeguimiento.reasignarSeguimientoVoluntario:846` hace el cambio y avisa
«Seguimiento reasignado» (`:877`).

**Un ajuste respecto al original.** El ítem no muestra el **número de cédula** del adoptante:
`lista_item_seguimientos.xml` tiene foto, nombre y mascota. Se corrigió el criterio a los tres
campos reales.

La referencia del original decía «la lista de voluntarios (HU013)», apuntándose a sí misma; la
lista de voluntarios es HU012.

---

## HU014 — Revisar reportes de animales abandonados

> **Estado: Pendiente.** No implementada. Los criterios quedan redactados para cuando se
> construya; la validación contra el código no procede todavía.

```
Título: 
Revisar reportes de animales abandonados
Nro. o Código: 
HU014	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF013	Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO administrador QUIERO visualizar los reportes de animales abandonados PARA determinar el nivel de emergencia y actuar según sea necesario.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE presentar una opción para acceder a los [Reportes de animales abandonados].
•	El sistema DEBE presentar los reportes en forma de lista, dividida en dos secciones: [Rescates] y [Reportes].
•	Cada ítem de la sección [Reportes] DEBE mostrar: Foto, Fecha del reporte, Tamaño del animal (Grande/Mediano/Pequeño), Sexo, Estado del animal (Herido/Enfermo/Sin peligro) y Ubicación, junto con la opción [Aceptar animal].
•	Al seleccionar [Aceptar animal], el sistema DEBE cambiar el estado del reporte a Rescate y DEBE presentarlo en la sección [Rescates].
•	Cada ítem de la sección [Rescates] DEBE mostrar la opción [Concluir rescate].
•	Al seleccionar [Concluir rescate], el sistema DEBE presentar el formulario para agregar una mascota (HU008).
•	El sistema DEBE cambiar el estado del reporte a Rescatado una vez registrada la mascota, y DEBE dejar de presentarlo en la lista.
•	El sistema NO DEBE permitir [Aceptar animal] cuando el refugio esté en su capacidad máxima, y DEBE indicar el motivo.
•	El sistema DEBE notificar al voluntario que envió el reporte cuando su reporte pase a Rescate.
OBSERVACIONES
•	Antes de implementarla hay que definir la capacidad máxima del refugio y dónde se guarda: es el dato del que dependen tanto esta historia como HU023.
•	Los estados del reporte —Reporte, Rescate, Rescatado— habría que añadirlos al enumerado de estados del sistema.
```

**Por qué está pendiente.** No existe nada: ni colección de Firestore, ni nodo de Realtime
Database, ni modelo, ni pantalla, ni un `drawable` relacionado. Las colecciones que el proyecto
usa hoy son `Cuentas`, `Usuarios`, `Domicilios`, `Mascotas`, `Adopciones`, `Seguimientos`,
`ContratosAdopciones`, `ContratoGeneral`, `Clausulas` y `NotificacionesProgramadas`; ninguna
guarda reportes.

Es, junto con HU023 y HU024, un módulo completo sin empezar. Las tres dependen de la misma
pieza que no está decidida: **la capacidad máxima del refugio**, que es la condición de dos de
sus criterios. Conviene redactarla antes, porque sin ese dato ni «Aceptar animal» ni «enviar
reporte» se pueden implementar.

La referencia «la pantalla para agregar una mascota (HU009)» del original es HU008; HU009 es
editar.

---

## HU015 — Aceptar o rechazar solicitudes de adopción

```
Título: 
Aceptar o rechazar solicitudes de adopción
Nro. o Código: 
HU015	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF014	Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO administrador QUIERO aceptar o rechazar las solicitudes de adopción PARA entregar cada mascota solo a quien cumple los requisitos.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE presentar una opción para acceder a las [Solicitudes de adopción].
•	El sistema DEBE presentar las solicitudes en forma de lista, dividida en dos secciones: [Solicitudes pendientes] y [Adopciones en proceso].
•	Cada ítem de la lista DEBE mostrar: Nombre del solicitante, Fecha de emisión y Estado de la solicitud, junto con la opción [Ver solicitud].
•	Al seleccionar [Ver solicitud], el sistema DEBE presentar toda la información enviada: Nombre del adoptante, Mascota seleccionada, Número de cédula, Fecha de emisión, Fotos de la cédula, Foto de servicios básicos, Vídeo del lugar donde vivirá la mascota, Cerramiento (Sí/No) y Tipo de domicilio (Arrendado/Propio).
•	El sistema DEBE permitir ampliar las fotos y reproducir el vídeo enviados.
•	El sistema DEBE presentar las opciones [Aceptar solicitud] y [Rechazar solicitud].
•	Al seleccionar [Rechazar solicitud], el sistema DEBE presentar una ventana para ingresar el motivo del rechazo.
•	El sistema DEBE exigir el campo [Motivo] y DEBE indicarlo cuando esté vacío.
•	El sistema DEBE limitar el campo [Motivo] a 400 caracteres.
•	Al rechazar, el sistema DEBE cambiar el estado de la solicitud a Rechazada.
•	Al aceptar, el sistema DEBE cambiar el estado de la solicitud a Aceptada y DEBE completar el contrato de adopción con la información del adoptante y de la mascota.
•	El sistema DEBE presentar la solicitud aceptada en la sección [Adopciones en proceso], junto con la opción para ver el estado de la adopción.
•	El sistema DEBE notificar al adoptante cada vez que cambie el estado de su solicitud.
•	Al ver el estado de la adopción, el sistema DEBE presentar la información de la solicitud, el contrato lleno, el vídeo de compromiso y el contrato firmado, cuando el adoptante los haya enviado (HU027).
•	El sistema DEBE permitir al administrador validar el vídeo de compromiso y el contrato firmado.
•	El sistema DEBE permitir solicitar el reenvío de los requisitos finales indicando el motivo, cuando no sean válidos.
•	El sistema DEBE presentar la opción de terminar el proceso de adopción una vez validados el vídeo y el contrato.
•	Al terminar el proceso, el sistema DEBE cambiar el estado de la adopción a Completada y DEBE vincular la mascota a la cuenta del adoptante.
OBSERVACIONES
•	El ciclo de la solicitud es: Pendiente → Aceptada → En revisión → Completada, con salida a Rechazada desde Pendiente, y un retorno de En revisión a Aceptada cuando se piden los requisitos de nuevo.
•	Al completarse la adopción se crea el seguimiento, que queda sin voluntario hasta que el administrador lo asigne (HU012).
```

**Notas de la validación.** Es la historia más grande de las validadas y está completa.
`GestionarSolicitudesAdopcionActivity` monta las dos secciones con
`fragment_solicitudes_adopcion_pendientes.xml` y `fragment_adopciones_en_progreso.xml`.
`ResponderSolicitudAdopcionActivity` presenta la solicitud con sus fotos y su vídeo y resuelve
aceptar o rechazar; el motivo del rechazo se pide en `dialog_motivo_rechazo_solicitud.xml` y es
obligatorio («Debe ingresar un motivo para el rechazo de la solicitud»).
`UltimosRequisitosActivity` es la validación final del vídeo y el contrato, con su propio motivo
de reenvío. `ControladorAdopcion` cierra con «Adopción completada».

**Un ajuste numérico.** El campo [Motivo] está limitado a **400 caracteres**, no a 500. Se
corrigió el criterio al valor real; si prefieres 500, es cambiar el `maxLength` del diálogo.

Se añadieron dos criterios que el código cumple y el original no recogía: el reenvío de
requisitos con su motivo, que es una salida real del flujo, y la creación del seguimiento al
completarse la adopción, que es lo que enlaza con HU012.

**Pendiente en el código: la redacción de los avisos de fallo.** «Error al enviar la
solicitud», «Error al obtener los documentos», «Error al obtener la firma del administrador»,
«Error al guardar el PDF» y dos «Ocurrió un error al intentar subir…» no siguen la convención.

---

## HU016 — Agregar vacuna

```
Título: 
Agregar vacuna
Nro. o Código: 
HU016	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF016	Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO voluntario o adoptante QUIERO registrar las vacunas de una mascota PARA mantener al día su información de vacunación.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE presentar, dentro de la información de una mascota, las opciones [Vacunas] y [Desparasitaciones].
•	Al seleccionar [Vacunas], el sistema DEBE presentar en forma de lista todas las vacunas colocadas a la mascota.
•	Cada ítem de la lista DEBE mostrar: Nombre de la vacuna, Fecha de colocación y Fecha de la próxima vacuna, junto con las opciones [Editar vacuna] (HU017) y [Eliminar vacuna] (HU018).
•	El sistema DEBE presentar una opción para [Agregar vacuna].
•	Al seleccionarla, el sistema DEBE presentar un formulario con los campos: Nombre de la vacuna, Fecha de colocación y Fecha de la próxima vacuna.
•	El sistema DEBE completar el campo [Fecha de colocación] con la fecha actual, y DEBE permitir seleccionar otra anterior o posterior.
•	El sistema DEBE mantener bloqueado el campo [Fecha de la próxima vacuna] y calcularlo automáticamente.
•	El sistema DEBE establecer la próxima vacuna un año después de la fecha de colocación cuando la mascota sea adulta.
•	El sistema DEBE establecer la próxima vacuna un mes después de la fecha de colocación cuando la mascota sea cachorro y tenga menos de tres vacunas registradas, y un año después a partir de la tercera.
•	El sistema DEBE presentar el mensaje [Complete todos los campos solicitados] cuando existan campos vacíos, y NO DEBE guardar.
•	El sistema DEBE presentar el mensaje [Vacuna registrada] cuando la vacuna se haya guardado, y NO DEBE anunciarlo antes.
•	El sistema DEBE regresar a la lista de vacunas una vez guardada.
•	El sistema DEBE programar tres recordatorios diarios para la próxima vacuna: dos días antes, un día antes y el día de la fecha.
•	El sistema DEBE enviar esos recordatorios al voluntario que registró la vacuna y también al adoptante, cuando la mascota ya esté adoptada.
•	El sistema DEBE reprogramar los recordatorios con la nueva fecha cuando se registre una vacuna posterior.
OBSERVACIONES
•	El historial médico se guarda en Realtime Database, no en Firestore, porque se consulta y se escucha en vivo.
•	Los recordatorios los dispara el servidor de notificaciones, que lee las notificaciones programadas; la aplicación solo las registra.
```

**Notas de la validación.** El cálculo de la próxima fecha está en
`ControladorUtilidades`: `esAdulto || cantidadVacunas` → `+1 año`, en otro caso `+1 mes`, donde
`cantidadVacunas = cantidad >= 3` se obtiene con
`ControladorHistorialMedico.obtenerNumeroVacunas`. Es **exactamente** la regla que escribiste:
mensual para cachorro durante las tres primeras y anual después.

Los recordatorios los programa `ControladorNotificaciones.enviarNotificacionVacuna:593`, que
usa `obtenerTresFechasConsecutivas`: la fecha de la próxima vacuna y los **dos días
anteriores**. También son tres notificaciones diarias, así que el criterio «con 2 días de
anticipación y de forma diaria» se cumple al pie de la letra.

El mensaje real es «Vacuna registrada», que ya cumple la convención mejor que el «Información
guardada correctamente» del original, así que se adoptó el del código.

**Pendiente en el código: no hay alerta de confirmación.** El criterio
`[¿Seguro que desea continuar?]` no se cumple: ni `AgregarHistorialMedicoActivity` ni
`EditarHistorialMedicoActivity` llaman a `crearAlertaConfirmacion`; al pulsar Guardar se guarda
directamente. Se quitó el criterio de la historia reescrita porque registrar una vacuna no es
irreversible y la convención reserva la confirmación para lo que sí lo es — pero si la quieres,
dilo y se añade en las cuatro pantallas del historial médico.

Las referencias del original, «(HU024)» y «(HU025)», apuntaban a historias que no existen con
ese número; editar es HU017 y eliminar es HU018.

---

## HU017 — Editar vacuna

```
Título: 
Editar vacuna
Nro. o Código: 
HU017	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF016	Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO voluntario o adoptante QUIERO editar una vacuna registrada PARA corregir su información cuando sea necesario.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE presentar las vacunas de la mascota en forma de lista (HU016).
•	Cada ítem de la lista DEBE mostrar su información junto con las opciones [Editar vacuna] y [Eliminar vacuna] (HU018).
•	Al seleccionar [Editar vacuna], el sistema DEBE presentar un formulario con la información actual cargada y DEBE permitir editar: Nombre de la vacuna y Fecha de colocación.
•	El sistema DEBE mantener bloqueado el campo [Fecha de la próxima vacuna] y recalcularlo cuando cambie la fecha de colocación.
•	El sistema DEBE aplicar al recálculo la misma regla que al registrar: un año para adultos, un mes para cachorros con menos de tres vacunas y un año a partir de la tercera.
•	El sistema DEBE presentar el mensaje [Complete todos los campos solicitados] cuando existan campos vacíos, y NO DEBE guardar.
•	El sistema DEBE presentar el mensaje [Vacuna actualizada] cuando los cambios se hayan guardado, y NO DEBE anunciarlo antes.
•	El sistema DEBE regresar a la lista de vacunas una vez guardados los cambios.
•	El sistema DEBE reprogramar los recordatorios cuando cambie la fecha de la próxima vacuna, y DEBE cancelar los anteriores.
OBSERVACIONES
•	Al cambiar la fecha se reprograman los recordatorios: `modificarNotificacionesHistorial` cancela los de la fecha anterior antes de registrar los nuevos, para que no lleguen duplicados.
```

**Notas de la validación.** `EditarHistorialMedicoActivity` llega con la información cargada,
valida con el mensaje unificado y guarda por
`ControladorHistorialMedico.editarVacuna:124`, que avisa «Vacuna actualizada». La
reprogramación la hace `ControladorNotificaciones.modificarNotificacionesHistorial:704`, que
cancela los recordatorios de la fecha vieja antes de poner los nuevos — ese criterio se añadió
porque es comportamiento real que el original no recogía, y es el que evita avisos duplicados.

**No existe el botón [Cancelar]**, igual que en la edición de mascotas: se sale con la flecha de
regresar. El criterio se quitó; si lo quieres, hay que añadirlo.

Tampoco hay alerta de confirmación, por el mismo motivo explicado en HU016.

La referencia «(HU025)» del original es HU018.

---

## HU018 — Eliminar vacunas

```
Título: 
Eliminar vacunas
Nro. o Código: 
HU018	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF017	Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO voluntario o adoptante QUIERO eliminar una vacuna registrada PARA que el historial de la mascota no contenga registros equivocados.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE presentar las vacunas de la mascota en forma de lista (HU016).
•	Cada ítem de la lista DEBE mostrar su información junto con las opciones [Editar vacuna] (HU017) y [Eliminar vacuna].
•	Al seleccionar [Eliminar vacuna], el sistema DEBE presentar una alerta de confirmación o cancelación que indique que la vacuna se va a eliminar.
•	El sistema DEBE eliminar el registro de la vacuna del historial de la mascota.
•	El sistema DEBE cancelar los recordatorios programados de esa vacuna.
•	El sistema DEBE presentar el mensaje [Vacuna eliminada] cuando se haya eliminado.
•	El sistema DEBE presentar un mensaje indicando que no se pudo eliminar la vacuna cuando la operación falle.
•	El sistema DEBE actualizar la lista de vacunas sin recargar la pantalla completa.
OBSERVACIONES
•	A diferencia de las cuentas y las mascotas, la eliminación de una vacuna es un borrado real: el registro desaparece del historial. Un registro médico equivocado no aporta nada conservado, y nada más en el sistema lo referencia.
```

**Notas de la validación.** La alerta existe: «¿Seguro/a que desea eliminar esta vacuna?», en
`ListaVacunasAdaptador`. El borrado lo hace
`ControladorHistorialMedico.eliminarHistorial:147`, que además llama a
`eliminarNotificacionesHistorial` para cancelar los recordatorios de esa vacuna — criterio
añadido, porque es comportamiento real que el original no recogía.

**El criterio del borrado lógico no se cumple, y se corrigió a propósito.** El original decía
«DEBE cambiar el estado de la vacuna a “Inactivo”», pero `eliminarHistorial` usa `removeValue()`:
es un **borrado real**. Aquí el código tiene razón y la historia no, al contrario que en las
cuentas y las mascotas: una vacuna mal registrada no la referencia nada más en el sistema, así
que conservarla solo ensucia el historial médico. Por eso también se cambió
`[Se ha eliminado correctamente]` por `[Vacuna eliminada]`, que además no lleva el adverbio.

**Pendiente en el código: no hay ningún mensaje.** Al eliminar, el adaptador quita la fila y no
avisa de nada; y si el borrado falla, solo se escribe en el log, de modo que el usuario ve la
fila desaparecer de la lista aunque siga guardada. Son los dos últimos criterios.

La referencia «[Editar vacuna] (HU025)» del original es HU017.

---

## HU019 — Agregar desparasitación

```
Título: 
Agregar desparasitación
Nro. o Código: 
HU019	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF018	Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO voluntario o adoptante QUIERO registrar las desparasitaciones de una mascota PARA mantener al día su información.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE presentar, dentro de la información de una mascota, la opción [Desparasitaciones].
•	Al seleccionarla, el sistema DEBE presentar en forma de lista todas las desparasitaciones de la mascota.
•	Cada ítem de la lista DEBE mostrar: Nombre del desparasitante, Fecha de aplicación y Fecha de la próxima aplicación, junto con las opciones [Editar desparasitación] (HU020) y [Eliminar desparasitación] (HU021).
•	El sistema DEBE presentar una opción para [Agregar desparasitación].
•	Al seleccionarla, el sistema DEBE presentar un formulario con los campos: Nombre del desparasitante, Fecha de aplicación, Fecha de la próxima aplicación, Peso de la mascota en libras y Cantidad de desparasitante en centímetros.
•	El sistema DEBE completar el campo [Fecha de aplicación] con la fecha actual, y DEBE permitir seleccionar otra anterior o posterior.
•	El sistema DEBE calcular automáticamente la [Cantidad de desparasitante] a partir del peso, a razón de un centímetro por cada diez libras, redondeando.
•	El sistema DEBE mantener bloqueado el campo [Fecha de la próxima aplicación] y establecerlo seis meses después de la fecha de aplicación.
•	El sistema DEBE presentar el mensaje [Complete todos los campos solicitados] cuando existan campos vacíos, y NO DEBE guardar.
•	El sistema DEBE presentar el mensaje [Desparasitación registrada] cuando se haya guardado, y NO DEBE anunciarlo antes.
•	El sistema DEBE regresar a la lista de desparasitaciones una vez guardada.
•	El sistema DEBE programar tres recordatorios diarios para la próxima aplicación: dos días antes, un día antes y el día de la fecha.
•	El sistema DEBE enviar esos recordatorios al voluntario que registró la desparasitación y también al adoptante, cuando la mascota ya esté adoptada.
•	El sistema DEBE reprogramar los recordatorios con la nueva fecha cuando se registre una desparasitación posterior.
OBSERVACIONES
•	El cálculo de la cantidad es una ayuda, no una restricción: el campo queda editable para corregirlo si hace falta.
```

**Notas de la validación.** `ControladorUtilidades.adaptarCamposPesoCantidad:316` calcula la
cantidad con `Math.round(libras / 10.0)` en cuanto se escribe el peso: es exactamente la regla
de un centímetro por cada diez libras, redondeando. Y la próxima aplicación se fija con
`fechaProx.add(Calendar.MONTH, 6)`, los seis meses del criterio. El mensaje real es
«Desparasitación registrada».

**Un ajuste: la anticipación son dos días, no tres.** El original pedía «con 3 días de
anticipación», pero `enviarNotificacionDesparasitacion:1058` usa la misma
`obtenerTresFechasConsecutivas` que las vacunas, así que programa la fecha de aplicación y los
dos días anteriores. Se ajustó el criterio a dos días, que además deja las dos reglas iguales
—vacuna y desparasitación avisan con la misma antelación—, lo cual es más fácil de explicar.
Si prefieres tres días para la desparasitación, hay que separar el cálculo en dos funciones.

Tampoco hay alerta de confirmación al guardar, por el mismo motivo que en HU016.

Las referencias del original, «(HU027)» y «(HU028)», son HU020 y HU021.

---

## HU020 — Editar desparasitación

```
Título: 
Editar desparasitación
Nro. o Código: 
HU020	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF019	Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO voluntario o adoptante QUIERO editar una desparasitación registrada PARA corregir su información cuando sea necesario.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE presentar las desparasitaciones de la mascota en forma de lista (HU019).
•	Cada ítem de la lista DEBE mostrar su información junto con las opciones [Editar desparasitación] y [Eliminar desparasitación] (HU021).
•	Al seleccionar [Editar desparasitación], el sistema DEBE presentar un formulario con la información actual cargada y DEBE permitir editar: Nombre del desparasitante, Fecha de aplicación y Peso de la mascota.
•	El sistema DEBE recalcular la [Cantidad de desparasitante] cuando cambie el peso, a razón de un centímetro por cada diez libras, redondeando.
•	El sistema DEBE mantener bloqueado el campo [Fecha de la próxima aplicación] y recalcularlo seis meses después de la nueva fecha de aplicación.
•	El sistema DEBE presentar el mensaje [Complete todos los campos solicitados] cuando existan campos vacíos, y NO DEBE guardar.
•	El sistema DEBE presentar el mensaje [Desparasitación actualizada] cuando los cambios se hayan guardado, y NO DEBE anunciarlo antes.
•	El sistema DEBE regresar a la lista de desparasitaciones una vez guardados los cambios.
•	El sistema DEBE reprogramar los recordatorios cuando cambie la fecha de la próxima aplicación, y DEBE cancelar los anteriores.
OBSERVACIONES
•	El recálculo de la cantidad se dispara al escribir el peso, igual que al registrar.
```

**Notas de la validación.** Misma pantalla que la edición de vacunas,
`EditarHistorialMedicoActivity`, con el mismo recálculo de `adaptarCamposPesoCantidad` y la
misma reprogramación de recordatorios. El mensaje real es «Desparasitación actualizada».

Como en HU017, **no existe el botón [Cancelar]** —se sale con la flecha de regresar— y tampoco
hay alerta de confirmación al guardar. Los dos criterios se quitaron de la historia reescrita.

La referencia «(HU028)» del original es HU021.

---

## HU021 — Eliminar desparasitación

```
Título: 
Eliminar desparasitación
Nro. o Código: 
HU021	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF020	Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO voluntario o adoptante QUIERO eliminar una desparasitación registrada PARA que el historial de la mascota no contenga registros equivocados.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE presentar las desparasitaciones de la mascota en forma de lista (HU019).
•	Cada ítem de la lista DEBE mostrar su información junto con las opciones [Editar desparasitación] (HU020) y [Eliminar desparasitación].
•	Al seleccionar [Eliminar desparasitación], el sistema DEBE presentar una alerta de confirmación o cancelación que indique que la desparasitación se va a eliminar.
•	El sistema DEBE eliminar el registro de la desparasitación del historial de la mascota.
•	El sistema DEBE cancelar los recordatorios programados de esa desparasitación.
•	El sistema DEBE presentar el mensaje [Desparasitación eliminada] cuando se haya eliminado.
•	El sistema DEBE presentar un mensaje indicando que no se pudo eliminar la desparasitación cuando la operación falle.
•	El sistema DEBE actualizar la lista de desparasitaciones sin recargar la pantalla completa.
OBSERVACIONES
•	Como en las vacunas, la eliminación es un borrado real y no un cambio de estado: un registro médico equivocado no aporta nada conservado.
```

**Notas de la validación.** Idéntica a HU018 en todo. La alerta dice «¿Seguro/a que desea
eliminar esta desparasitación?» en `ListaDesparasitacionesAdaptador`, y el borrado usa el mismo
`eliminarHistorial`, que cancela también los recordatorios asociados.

**Pendiente en el código: tampoco hay ningún mensaje**, ni de éxito ni de fallo. Si el borrado
falla, la fila desaparece de la lista aunque el registro siga guardado. Es el mismo arreglo que
HU018, en el adaptador de al lado.

El criterio del borrado lógico se corrigió por el mismo motivo que en HU018: el código borra de
verdad, y aquí tiene razón.

---

## HU022 — Chat de seguimiento de adopción

```
Título: 
Chat de seguimiento de adopción
Nro. o Código: 
HU022	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF021	Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO voluntario o adoptante QUIERO enviar y responder mensajes de seguimiento PARA verificar que la mascota adoptada se encuentre bien en su nuevo hogar.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE notificar al voluntario cuando el administrador le asigne un seguimiento (HU012).
•	El sistema DEBE presentar una opción para ver los seguimientos.
•	El sistema DEBE presentar la lista de seguimientos que corresponden al usuario según su rol: los asignados, para el voluntario; los de sus mascotas, para el adoptante.
•	Cada ítem de la lista DEBE mostrar el nombre de la otra parte y el nombre de la mascota, junto con la opción para abrir la conversación.
•	El sistema DEBE presentar la conversación completa al abrirla, ordenada cronológicamente.
•	El sistema DEBE permitir enviar mensajes de texto.
•	El sistema DEBE permitir enviar fotos y vídeos, tanto desde la galería como tomándolos con la cámara.
•	El sistema DEBE permitir ampliar las imágenes y reproducir los vídeos recibidos.
•	El sistema DEBE permitir responder a un mensaje concreto deslizándolo, y DEBE mostrar el mensaje citado en la respuesta.
•	El sistema DEBE notificar al receptor cada mensaje nuevo, salvo cuando ya tenga la conversación abierta.
•	El sistema DEBE desplazar la conversación al último mensaje cuando se abra el teclado.

Para el voluntario:
•	El sistema DEBE presentar un panel con las preguntas de seguimiento para enviarlas al adoptante sin escribirlas.
•	El sistema DEBE presentar una opción para programar el plan de recordatorios del seguimiento.
•	El sistema DEBE presentar una opción para [Retirar mascota].
•	Al seleccionar [Retirar mascota], el sistema DEBE advertir que la mascota volverá al catálogo y que el seguimiento se cerrará, y DEBE exigir el motivo del retiro.
•	El sistema DEBE permitir confirmar o cancelar el retiro.
•	Al confirmar el retiro, el sistema DEBE dejar el motivo escrito dentro de la conversación y DEBE notificar al adoptante de que su proceso de adopción ha finalizado.
•	Al retirar la mascota, el sistema DEBE desvincularla de la cuenta del adoptante, DEBE devolverla al catálogo de mascotas en adopción y DEBE cancelar los recordatorios de vacuna del adoptante.
•	El sistema DEBE dejar el seguimiento y la adopción en estado Rechazada al retirar la mascota, y en estado Completada al finalizar el seguimiento con normalidad.
•	El sistema DEBE presentar el seguimiento cerrado en modo solo lectura para los dos lados, sin permitir mensajes, retiro ni cambios en el plan.
OBSERVACIONES
•	El plan de recordatorios es: 7 controles diarios la primera semana, 8 mensuales y 4 trimestrales, a las 08:00. La primera semana es el periodo en el que tiene sentido decidir un retiro.
•	Los recordatorios del plan llegan al voluntario asignado, no al adoptante.
•	A los usuarios finales no se les muestra el estado Rechazada: la interfaz traduce los seguimientos cerrados a «Cerrado» y «Finalizado».
```

**Notas de la validación.** Es la funcionalidad más extensa del proyecto y está completa.
`SeguimientoVoluntarioChatActivity` y `SeguimientoAdoptanteChatActivity` tienen el chat sobre
Realtime Database, el envío de multimedia por Cloudinary, la respuesta deslizando con
`ItemTouchHelper`, el panel de preguntas, el plan de recordatorios y el retiro. El texto del
retiro ya dice «La mascota volverá al catálogo de adopción y este seguimiento se cerrará.
Indique el motivo del retiro:» —en usted, como corresponde—, y el cierre deja las dos pantallas
en solo lectura en el momento, a través de `escucharEstadoSeguimiento`.

**Tres ajustes respecto al original.**

1. **No existe el campo Apellido**, igual que en HU012: `Usuario` guarda un único campo
   `nombre`. El criterio se ajustó.
2. **Son 14 preguntas, no 13.** El panel incluye una que el original no listaba: «¿Necesita
   algún tipo de cuidado especial?». Se dejó el criterio sin enumerarlas, porque una lista de
   catorce preguntas dentro de un criterio no se puede comprobar de un vistazo y además cambia
   sin que cambie el comportamiento; el panel es lo que hay que verificar.
3. **Los estados son Completada y Rechazada**, no «Aceptado/Rechazado». Son los valores reales
   de `EstadosCuentas`, y conviene nombrarlos bien porque el criterio dice que la adopción
   queda en el mismo estado que el seguimiento.

Se añadieron tres criterios que el código cumple y el original no recogía: el orden cronológico
de la conversación, que no se notifica a quien ya tiene el chat abierto —lo resuelve
`presenciaChats`— y el desplazamiento al último mensaje al abrir el teclado, que es justo el
punto que el CLAUDE.md documenta como obligatorio.

Las referencias del original, «(HU013)» para la asignación y «(HU005)» para el catálogo, son
HU012 y HU007.

**Nada pendiente en el código para esta historia.**

---

## HU023 — Reportar animales abandonados

> **Estado: Pendiente.** No implementada. Los criterios quedan redactados para cuando se
> construya; la validación contra el código no procede todavía.

```
Título: 
Reportar animales abandonados
Nro. o Código: 
HU023	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF022	Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO voluntario QUIERO reportar un animal abandonado PARA que el administrador decida si se realiza el rescate.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE presentar al voluntario una opción para [Reportar animal abandonado].
•	El sistema DEBE presentar un formulario con los campos: Tamaño del animal (Grande/Mediano/Pequeño), Sexo (Macho/Hembra), Estado del animal (Herido/Enfermo/Sin peligro), Ubicación y Fecha del reporte.
•	El sistema DEBE completar la [Fecha del reporte] con la fecha actual y NO DEBE permitir editarla.
•	El sistema DEBE ofrecer [Tomar una foto] y [Subir una foto] para la foto del animal, solicitando los permisos de cámara y de acceso a imágenes cuando no los tenga.
•	El sistema DEBE exigir la foto del animal y DEBE indicarlo cuando falte.
•	El sistema DEBE presentar el mensaje [Complete todos los campos solicitados] cuando existan campos vacíos, y NO DEBE enviar el reporte.
•	El sistema NO DEBE permitir enviar un reporte cuando el refugio esté en su capacidad máxima, y DEBE indicar el motivo.
•	El sistema NO DEBE permitir al voluntario enviar un reporte nuevo mientras tenga uno sin resolver, y DEBE indicarlo.
•	Al seleccionar [Enviar reporte], el sistema DEBE mostrar una alerta de confirmación o cancelación.
•	El sistema DEBE presentar el mensaje [Reporte enviado] cuando el reporte se haya guardado, y NO DEBE anunciarlo antes.
•	El sistema DEBE presentar un mensaje indicando que no se pudo enviar el reporte cuando la operación falle.
•	El sistema DEBE notificar al administrador cuando se reciba un reporte nuevo.
OBSERVACIONES
•	La restricción de un reporte por voluntario estaba escrita en HU024; pertenece aquí, que es donde se envía.
•	La capacidad máxima del refugio hay que definirla antes: es la condición de dos criterios, y la comparte con HU014.
•	La foto puede ir por Cloudinary, como el resto de la multimedia pesada del proyecto.
```

**Por qué está pendiente.** No existe nada en el código: ni modelo, ni colección, ni pantalla.
Ver la nota de HU014.

**Un criterio se movió.** El original de HU024 decía «El sistema DEBE permitir enviar únicamente
un reporte por voluntario hasta que uno sea aceptado», que es una regla sobre el **envío**, no
sobre la edición. Se trasladó aquí.

---

## HU024 — Editar reportes de animales abandonados

> **Estado: Pendiente.** No implementada. Los criterios quedan redactados para cuando se
> construya; la validación contra el código no procede todavía.

```
Título: 
Editar reportes de animales abandonados
Nro. o Código: 
HU024	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF023	Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO voluntario QUIERO consultar y editar los reportes que he enviado PARA corregir su información cuando sea necesario.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE presentar al voluntario la lista de los reportes que ha enviado.
•	Cada reporte DEBE mostrar: Foto, Fecha del reporte, Estado del reporte, Tamaño del animal, Sexo, Estado del animal y Ubicación, junto con la opción [Editar reporte].
•	Al seleccionar [Editar reporte], el sistema DEBE presentar el formulario con la información actual cargada y DEBE permitir editar todos los campos, incluida la foto.
•	El sistema NO DEBE permitir editar un reporte que ya haya pasado a Rescate o a Rescatado, y DEBE indicar el motivo.
•	El sistema DEBE presentar el mensaje [Complete todos los campos solicitados] cuando existan campos vacíos, y NO DEBE guardar.
•	Al seleccionar [Guardar], el sistema DEBE mostrar una alerta de confirmación o cancelación.
•	El sistema DEBE presentar el mensaje [Reporte actualizado] cuando los cambios se hayan guardado, y NO DEBE anunciarlo antes.
•	El sistema DEBE presentar un mensaje indicando que no se pudo guardar el reporte cuando la operación falle.
OBSERVACIONES
•	La restricción de un reporte por voluntario se trasladó a HU023, que es donde se envía.
•	El criterio de no editar un reporte ya aceptado es nuevo: sin él, un voluntario podría cambiar los datos de un rescate en curso.
```

**Por qué está pendiente.** No existe nada en el código. Ver la nota de HU014.

El original empezaba con «La interfaz DEBE mostrar una opción para [Reportar animales
abandonados]», que es el criterio de HU023; aquí el punto de entrada es la lista de los reportes
ya enviados, que es lo que esta historia necesita.

---

## HU025 — Visualizar los requisitos para adoptar una mascota

```
Título: 
Visualizar los requisitos para adoptar una mascota
Nro. o Código: 
HU025	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF024	Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO adoptante QUIERO ver los requisitos necesarios PARA saber qué necesito antes de iniciar el proceso de adopción.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE presentar una opción para acceder al catálogo de mascotas en adopción (HU007).
•	El sistema DEBE presentar, en el catálogo, una opción para [Ver requisitos de adopción].
•	Al seleccionarla, el sistema DEBE presentar la lista completa de los requisitos: fotos de la cédula de identidad (frontal y posterior), foto de una planilla de servicios básicos, vídeo del lugar donde vivirá la mascota, hogar con cerramiento adecuado, tipo de domicilio (propio o arrendado), aceptar y firmar el contrato de adopción, vídeo de compromiso, y responder a los seguimientos después de completar la adopción.
•	El sistema DEBE presentar los requisitos numerados y en forma de texto, sin pedir ningún dato.
•	El sistema DEBE presentar una opción para cerrar la pantalla de requisitos y volver al catálogo.
OBSERVACIONES
•	Esta historia es solo informativa: el envío de la solicitud con esos requisitos es HU026.
```

**Notas de la validación.** Es la más pequeña de las validadas y se cumple entera.
`popup_requisitos_adopcion.xml` presenta los **ocho** requisitos numerados, con el mismo
contenido que tu lista, y el botón de la ventana está en el catálogo
(`activity_catalogo_mascotas.xml`, «Ver requisitos de adopción»).

Las referencias del original estaban mal: «su información respectiva (HU005)» es HU007, y
«[Enviar solicitud de adopción] (HU0032)» —con un cero de más— es HU026.

**Nada pendiente en el código para esta historia.**

---

## HU026 — Enviar solicitud de adopción

```
Título: 
Enviar solicitud de adopción
Nro. o Código: 
HU026	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF025	Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO adoptante QUIERO enviar una solicitud PARA iniciar el proceso de adopción de una mascota.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE presentar una opción para acceder al catálogo de mascotas en adopción (HU007).
•	El sistema DEBE presentar, al seleccionar una mascota del catálogo, su información junto con las opciones [Ver requisitos de adopción] (HU025) y [Enviar solicitud de adopción].
•	Al seleccionar [Enviar solicitud de adopción], el sistema DEBE presentar los datos de la solicitud ya completados: Nombre del adoptante, Mascota seleccionada, Número de cédula y Fecha de emisión.
•	El sistema DEBE presentar un formulario para aportar los requisitos: fotos de la cédula, foto de servicios básicos, vídeo del lugar donde vivirá la mascota, Cerramiento (Sí/No) y Tipo de domicilio (Arrendado/Propio).
•	El sistema DEBE ofrecer [Tomar una foto] y [Subir una foto] para cada imagen, y grabar o subir para el vídeo, solicitando los permisos cuando no los tenga.
•	El sistema DEBE exigir las tres imágenes y el vídeo, y DEBE indicarlo cuando falte alguno.
•	El sistema DEBE presentar las opciones [Enviar solicitud] y [Cancelar].
•	Al seleccionar [Enviar solicitud], el sistema DEBE mostrar una alerta de confirmación o cancelación.
•	El sistema DEBE presentar el mensaje [Solicitud de adopción enviada] cuando la solicitud se haya guardado con su multimedia, y NO DEBE anunciarlo antes.
•	El sistema DEBE presentar un mensaje indicando que no se pudo enviar la solicitud cuando la operación falle, y DEBE mantener la pantalla abierta.
•	El sistema DEBE dejar la solicitud en estado Pendiente.
•	El sistema DEBE notificar al administrador de la solicitud recibida.
•	Al seleccionar [Cancelar], el sistema DEBE regresar al catálogo sin guardar nada.
OBSERVACIONES
•	La multimedia se sube a Cloudinary; el mensaje de éxito llega cuando han terminado las subidas, no al pulsar el botón.
•	El seguimiento de la adopción no se crea aquí: se crea cuando el administrador completa el proceso (HU015).
```

**Notas de la validación.** `ProcesoAdopcionActivity` con `fragment_iniciar_adopcion.xml`
presenta la información ya cargada y el formulario de requisitos; `ControladorAdopcion` exige
las tres imágenes y el vídeo («Debe subir las 3 imágenes y el video») y avisa «Solicitud de
adopción enviada» al terminar. Se adoptó ese texto en lugar del `[Solicitud enviada]` del
original, porque es el que el usuario ve.

**Pendiente en el código: dos cadenas de depuración en pantalla.**
`IniciarAdopcionFragment` muestra «Esta es la posicion del domicilio: …» y «FECHA ACTUAL: …» en
sendos `Toast`. Son del mismo tipo que las que ya se limpiaron en las mascotas. Y «Error al
enviar la solicitud» debería ser `No se pudo enviar la solicitud`.

Las dos referencias del original estaban vacías —«su información respectiva ()» y
«[Ver los requisitos de adopción] ()»—; son HU007 y HU025.

---

## HU027 — Completar proceso de adopción

```
Título: 
Completar proceso de adopción
Nro. o Código: 
HU027	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF026	Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO adoptante QUIERO completar los requisitos finales PARA terminar el proceso y recibir a la mascota.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE presentar al adoptante una opción para ver sus solicitudes de adopción.
•	El sistema DEBE presentar, para cada solicitud: Nombre de la mascota, Fecha de emisión y Estado (Pendiente, Aceptada, En revisión, Completada o Rechazada).
•	El sistema DEBE habilitar la opción de continuar el proceso únicamente cuando la solicitud esté en estado Aceptada.
•	Al continuar el proceso, el sistema DEBE presentar el contrato de adopción y el apartado del vídeo de compromiso.
•	El sistema DEBE presentar el contenido completo del contrato con la información del adoptante y de la mascota ya incorporada.
•	El sistema DEBE permitir adjuntar una foto de la firma del adoptante, y DEBE colocarla dentro del contrato.
•	El sistema NO DEBE permitir continuar sin la firma, y DEBE indicarlo.
•	El sistema DEBE presentar las indicaciones del vídeo de compromiso y DEBE permitir grabarlo o subirlo.
•	El sistema NO DEBE permitir continuar sin el vídeo, y DEBE indicarlo.
•	El sistema DEBE advertir que, una vez concluida la adopción, habrá que responder al seguimiento correspondiente.
•	Al enviar la información, el sistema DEBE mostrar una alerta de confirmación o cancelación.
•	El sistema DEBE presentar un mensaje confirmando que el proceso se completó y que queda pendiente la aprobación del administrador, cuando la firma y el vídeo se hayan guardado.
•	El sistema DEBE cambiar el estado de la adopción a En revisión.
•	El sistema DEBE notificar al administrador cuando se hayan enviado los requisitos finales.
•	El sistema DEBE permitir al adoptante descargar el contrato de adopción.
•	El sistema DEBE notificar al adoptante cuando el administrador concluya el proceso, indicando que la mascota queda vinculada a su cuenta.
•	El sistema DEBE permitir al adoptante volver a enviar los requisitos cuando el administrador los haya devuelto, indicando el motivo por el que se devolvieron.
OBSERVACIONES
•	Si el administrador no valida la firma o el vídeo, la adopción vuelve de En revisión a Aceptada y el adoptante puede enviarlos de nuevo (HU015).
•	La mascota queda vinculada a la cuenta del adoptante cuando el administrador termina el proceso, no al enviar los requisitos.
```

**Notas de la validación.** `CompletarProcesoAdopcionActivity` monta `fragment_contrato.xml` y
`fragment_video_compromiso.xml`, exige las dos piezas («No ha firmado el contrato de adopción.»,
«No se ha grabado un video»), coloca la firma en el contrato y deja la adopción En revisión con
el aviso «Proceso de adopción completado, espere la aprobación del administrador.». Se adoptó
ese texto en lugar del `[Acción completada]` del original: dice qué pasó y qué falta, que es
más útil. La descarga del contrato en PDF también existe («Contrato guardado en Descargas») y se
añadió como criterio, porque el original no la recogía.

También se añadió el reenvío de requisitos, que es la contrapartida de lo que el administrador
puede pedir en HU015 y cierra el lazo En revisión → Aceptada.

**Pendiente en el código: la redacción de tres avisos.** «Error al guardar el PDF», «Error al
obtener los documentos» y dos «Ocurrió un error al intentar subir…» no siguen la convención de
`No se pudo` + infinitivo.

---

## HU028

```

```

---

## HU029

```

```

---

## HU030

```

```

---

## Anotación — Código QR de la mascota (pendiente de número)

Desprendida de HU008 al validarla, **sin número asignado**. La generación del código QR no
está implementada en absoluto.

**Estado en el código.** `Modelo/Mascota.java` declara el campo `codigoQR` con su getter, su
setter y su serialización en el `Parcelable` (líneas 18, 64, 92 y 201-207), pero **ningún otro
archivo del proyecto lo escribe ni lo lee**, y no hay ninguna dependencia de generación de
códigos —ni ZXing ni equivalente— en `app/build.gradle`. Es un campo muerto en el modelo: se
guarda vacío en Firestore y nunca se usa.

Antes de redactar la historia conviene decidir tres cosas, porque cambian los criterios por
completo:

1. **Qué contiene el QR.** El id del documento de la mascota, una URL a una pantalla de la app,
   o un enlace web. Si es un enlace, hace falta decidir a dónde apunta cuando quien lo escanea
   no tiene la aplicación instalada.
2. **Quién lo escanea y para qué.** No es lo mismo un QR para que el refugio identifique a la
   mascota en una ficha impresa, que uno para que un adoptante potencial abra su información
   desde un cartel.
3. **Dónde se ve y si se puede guardar o imprimir.** Generarlo sin una pantalla donde
   mostrarlo y una forma de exportarlo no sirve de nada.

Hasta que eso esté decidido, el criterio «El sistema DEBE generar el código QR una vez que se
haya guardado la información» no es comprobable: no dice qué codifica ni dónde aparece.

---

## Anotación — Lista de mascotas adoptadas (pendiente de número)

Historia desprendida de HU007 al validarla, **sin número asignado todavía** porque los
siguientes están ocupados. Se separó porque la funcionalidad **no está construida**, y
mezclarla con lo que ya funciona dejaría HU007 sin poder cerrarse nunca.

**Estado en el código: no implementada.** El botón «Lista de mascotas adoptadas» existe en
`activity_ver_mascotas.xml` (`lLMascotasAdoptadas`), pero `VerMascotasActivity` no le asigna
ningún listener y monta siempre `ListaMascotasAdopcionFragment`. No hay fragment, adaptador ni
layout de ítem para las adoptadas: pulsar el botón no hace nada. `MisMascotasActivity` es otra
cosa —muestra «Última vacuna» y «Última desparasitación» al adoptante y al voluntario— y no
tiene búsqueda ni filtros.

Criterios ya redactados, listos para copiar cuando se le asigne número:

```
Título: 
Consultar la lista de mascotas adoptadas
Nro. o Código: 
(pendiente)    Prioridad:
Media    Estimación:
    Ref. Requerimiento:
RF005    Fecha de creación: 
01/05/2024
HISTORIA DE USUARIO
COMO administrador QUIERO consultar la lista de las mascotas ya adoptadas PARA saber con quién está cada una y qué voluntario la sigue.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE presentar, en la pantalla de mascotas registradas, una opción para alternar entre [Lista de mascotas en adopción] y [Lista de mascotas adoptadas].
•	El sistema DEBE presentar las mascotas adoptadas en forma de lista.
•	Cada ítem de la lista DEBE mostrar: Foto, Nombre, Vacunación, Desparasitación, Esterilización y Estado.
•	Cada ítem DEBE mostrar además: Nombre del adoptante, Número de teléfono del adoptante, Voluntario asignado y Número de teléfono del voluntario.
•	El sistema DEBE permitir la búsqueda por nombre de la mascota.
•	El sistema DEBE presentar los filtros: Por edad (Cachorro/Adulto), Por sexo, Por estado sanitario (Vacunados/No vacunados), Por nombre del adoptante y Por voluntario asignado.
•	El sistema DEBE permitir quitar los filtros seleccionados y volver a la lista completa.
•	El sistema DEBE presentar un aviso de que no se encontraron resultados cuando la búsqueda no devuelva ninguno.
•	El sistema DEBE presentar un aviso cuando no se pueda obtener la lista.
OBSERVACIONES
•	El domicilio de la mascota puede ser el refugio, el domicilio de un voluntario o el del adoptante, en caso de que ya se haya culminado el proceso de adopción.
•	Los datos del adoptante y del voluntario conviene guardarlos desnormalizados en el modelo, como ya hace Seguimiento con nombreAdoptante, nombreMascota y nombreVoluntario: la lista no debe consultar Firestore dentro de onBindViewHolder.
```

**Por dónde entrar.** Hace falta conectar los dos botones para alternar fragments, y crear un
fragment nuevo con su adaptador y su ítem, siguiendo el patrón del que ya existe.

Lo que la hace más grande de lo que parece son los datos: una sola fila necesita información de
cuatro colecciones — la mascota de `Mascotas`, la relación con el adoptante de `Adopciones`, el
voluntario asignado de `Seguimientos` y los dos teléfonos de `Usuarios`. Conviene resolverlo en
el controlador y pasarle al adaptador los datos ya reunidos. Además, los filtros «por nombre
del adoptante» y «por voluntario asignado» solo son posibles si esos nombres viajan con cada
fila.

---

## Qué queda por cubrir

Con HU001 a HU027, el backlog cubre casi todo lo implementado. Lo que sigue sin historia, por
si quieres usar los bloques HU028 a HU030:

- **Consultar las notificaciones recibidas** (CU-G04). Existe la pantalla, con su pestaña en el
  menú inferior, y no tiene historia.
- **Consultar sus mascotas adoptadas** (CU-ADO07). Es `MisMascotasActivity`, el punto de entrada
  del adoptante al historial médico de su mascota. HU016 a HU021 dan por supuesto que se llega
  ahí, pero nadie describe esa pantalla.
- **Finalizar el seguimiento** (CU-VOL08). HU022 cubre el retiro, que es la salida excepcional,
  pero no la normal: cuando el voluntario da el seguimiento por terminado y el chat se cierra
  con estado Completada.

Y hay tres cosas sin número, cada una en su anotación o en su historia:

- El **código QR** de la mascota, desprendido de HU008.
- La **lista de mascotas adoptadas**, desprendida de HU007.
- El **módulo de reportes de animales abandonados**, que sí tiene números —HU014, HU023 y
  HU024— pero está entero sin construir.

**Un hueco en la numeración de requisitos:** las historias saltan de RF014 a RF016. Si RF015
corresponde a algo, le falta la historia; si no existe, conviene anotarlo para que no parezca
un olvido en la defensa.
