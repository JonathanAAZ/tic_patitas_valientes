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
| HU003 | Recuperar Contraseña | — | No | La función no existe todavía |
| HU004 | Cambiar contraseña | RF003 | Sí | Nada |
| HU005 | Editar información del perfil | RF004 | Sí | Nada |
| HU006 | Visualizar información del perfil | RF004 | Sí | Nada |
| HU007 | Buscar y filtrar información | RF005 | Sí | Nada |
| HU008 | Agregar mascota al catálogo | RF007 | Sí | Nada |
| HU009 | Editar mascotas del catálogo | RF008 | Sí | Nada |
| HU010 | Activar o desactivar mascotas | RF009 | Sí | Nada |
| HU011 | Activar o desactivar usuarios | RF010 | Sí | Nada |
| HU012 | Asignar seguimientos a voluntarios | RF011 | Sí | Nada |
| HU013 – HU030 | *(bloques vacíos listos)* | — | — | — |

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

## HU013

```

```

---

## HU014

```

```

---

## HU015

```

```

---

## HU016

```

```

---

## HU017

```

```

---

## HU018

```

```

---

## HU019

```

```

---

## HU020

```

```

---

## HU021

```

```

---

## HU022

```

```

---

## HU023

```

```

---

## HU024

```

```

---

## HU025

```

```

---

## HU026

```

```

---

## HU027

```

```

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

## Candidatas pendientes de redactar

Casos de uso implementados que todavía no tienen historia. Sirve de guía para llenar los
bloques de arriba; los números los asignas tú.

**Mascotas** — registrar una mascota (CU-AD01) · editar su información (CU-AD02) ·
activar o desactivar una mascota (CU-AD03) · consultar y filtrar la lista (CU-AD04) ·
explorar el catálogo como adoptante (CU-ADO02)

**Cuentas** — consultar y editar el perfil (CU-G02) · consultar y buscar usuarios (CU-AD06) ·
editar los datos de un usuario (CU-AD07) · activar o desactivar una cuenta (CU-AD08)

**Adopciones** — enviar una solicitud (CU-ADO03) · consultar las solicitudes enviadas
(CU-ADO04) · revisar y aceptar una solicitud (CU-AD09) · rechazar una solicitud (CU-AD10) ·
aprobar los requisitos finales y terminar la adopción (CU-AD11) · solicitar el reenvío de los
requisitos (CU-AD12)

**Contratos** — firmar el contrato y enviar los requisitos finales (CU-ADO05) ·
descargar el contrato en PDF (CU-ADO06)

**Seguimientos** — asignar un seguimiento a un voluntario (CU-AD13) · reasignarlo a otro
(CU-AD14) · consultar los seguimientos asignados (CU-VOL04) · conversar con el adoptante
(CU-VOL05) · programar el plan de recordatorios (CU-VOL06) · retirar la mascota (CU-VOL07) ·
finalizar el seguimiento (CU-VOL08) · responder en el chat como adoptante (CU-ADO09) ·
consultar un seguimiento cerrado (CU-VOL09 y CU-ADO10)

**Historial médico** — registrar una vacuna o desparasitación (CU-VOL02) · editar o eliminar
un registro (CU-VOL03) · registrar y consultar el de su mascota como adoptante (CU-ADO08)

**Mascotas adoptadas y avisos** — consultar sus mascotas adoptadas (CU-ADO07) ·
consultar las notificaciones recibidas (CU-G04)

Son 32 candidatas para 26 bloques, así que algunas conviene agruparlas —activar y desactivar
suelen caber en una sola historia—. Las subfunciones CU-S01 a CU-S05 (aportar una imagen,
grabar un video, notificar, programar y cancelar recordatorios) no dan historia propia: son
medios, no objetivos, y aparecen dentro de las historias que las usan.
