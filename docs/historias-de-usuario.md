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
| HU005 – HU030 | *(bloques vacíos listos)* | — | — | — |

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

## HU005

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
COMO usuario del sistema QUIERO acceder a una sección de información personal PARA editar mis datos personales.
CRITERIOS DE ACEPTACIÓN
•	El sistema DEBE presenta un formulario con la siguiente información: Nombre, Correo, Número de Cédula, Fecha de Nacimiento, Edad, Estado Civil, Ocupación. Número de Teléfono, Usuario de Instagram (Opcional) y Nombre en Facebook (Opcional).
•	El sistema DEBE presentar una sección para la información del domicilio: 
•	El sistema DEBE permitir [Editar foto de perfil].
•	El sistema DEBE permitir [Tomar una foto] o [Subir una foto] al editar la foto de perfil.
•	En caso de no tenerlos, el sistema DEBE solicitar los permisos de almacenamiento y cámara.
•	Al seleccionar la opción [Guardar] en la edición de la foto de perfil, el sistema DEBE presentar un mensaje en caso de que no se haya seleccionado o subido una nueva foto.
•	El sistema DEBE permitir editar la información de Nombre, Cédula, Fecha de Nacimiento, Estado Civil, Profesión/Ocupación. Número de teléfono, Usuario de Instagram (Opcional) y Nombre en Facebook (Opcional).
•	El sistema DEBE mostrar un mensaje de error en caso de que existan campos vacíos: [Complete todos los campos solicitados].
•	Al guardar la edición de la información personal, el sistema DEBE presentar el formulario de edición del domicilio permitiendo editar los siguientes campos: País, Provincia, Cantón, Parroquia, Barrio y Calles.
•	El sistema DEBE mostrar una opción para [Guardar].
•	El sistema DEBE mostrar una alerta de guardado exitoso: [Información guardada con éxito].

En el caso del usuario con rol de administrador:
•	El sistema DEBE presentar una opción dentro de la interfaz para acceder a la lista de todos los usuarios que existen dentro del sistema.
•	Para cada ítem de la lista, el sistema DEBE presentar su información (HU005), junto con dos opciones para [Editar usuario] y [Eliminar usuario] (HU012).
•	Cuando se seleccione la opción [Editar usuario], el sistema DEBE presentar la pantalla para visualizar la [Información de perfil] (HU00) 
OBSERVACIONES
•	Debido a que la foto de perfil no es obligatoria, se presentará una imagen básica de perfil en ese espacio.
•	El Administrador no podrá editar la foto de perfil de los usuarios ni la información del domicilio.

```

---

## HU006

```

```

---

## HU007

```

```

---

## HU008

```

```

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
