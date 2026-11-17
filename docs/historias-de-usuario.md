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
| HU007 | Buscar y filtrar información | RF005 | Sí | Tres defectos en VerMascotasActivity |
| HU008 | Consultar la lista de mascotas adoptadas | RF005 | Sí | La lista no existe todavía |
| HU009 – HU030 | *(bloques vacíos listos)* | — | — | — |


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
•	La lista de mascotas adoptadas, con los datos del adoptante y del voluntario asignado, no forma parte de esta historia porque todavía no está construida. Ver HU008.
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

**Pendiente en el código: `VerMascotasActivity` tiene tres defectos de la lista de «errores a
NO repetir».**

1. `setContentView(R.layout.activity_ver_mascotas)` en la línea 25 y después
   `ActivityVerMascotasBinding.inflate(...)` en la 32, **sin** `setContentView(binding.getRoot())`.
   El binding infla una segunda copia del layout que nunca se muestra, de modo que cualquier
   `binding.algo` manipularía vistas invisibles. Hoy no se nota porque el único uso del binding
   es la propia asignación.
2. Los insets se aplican sobre `findViewById(R.id.main)` en lugar de `binding.main`.
3. `iVRegresarVerMascotas` no tiene listener: la flecha de regresar no hace nada.

Y los ids del ítem de mascota repiten el problema de la lista de usuarios:
`ivEliminarMascotaLista` y `lYBtnEliminarMascota` dicen *eliminar* y el texto que muestran es
«Activar» o «Desactivar».

---

## HU008 — Consultar la lista de mascotas adoptadas

> Historia desprendida de HU007. Se separó porque **no está construida**, y mezclarla con lo
> que ya funciona dejaría HU007 sin poder cerrarse nunca. El número es provisional: si ya
> tenías otra HU008 prevista, renumera.

```
Título: 
Consultar la lista de mascotas adoptadas
Nro. o Código: 
HU008	Prioridad:
Media	Estimación:
	Ref. Requerimiento:
RF005	Fecha de creación: 
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

**Por dónde entrar.** El botón ya existe en el layout (`lLMascotasAdoptadas` en
`activity_ver_mascotas.xml`), pero `VerMascotasActivity` no le pone listener y monta
`ListaMascotasAdopcionFragment` sin condición. `MisMascotasActivity` es otra cosa: muestra
«Última vacuna» y «Última desparasitación» para el adoptante y el voluntario, sin búsqueda ni
filtros.

Hace falta un fragment nuevo para la lista de adoptadas, su adaptador y su ítem, con el patrón
del que ya existe. Los teléfonos están en `Usuarios`, y la relación mascota–adoptante–voluntario
en `Adopciones` y `Seguimientos`; conviene resolverla en el controlador y pasarle al adaptador
los datos ya reunidos, no consultarlos por fila.

---

## HU009

```

```

---

## HU010

```

```

---

## HU011

```

```

---

## HU012

```

```

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
