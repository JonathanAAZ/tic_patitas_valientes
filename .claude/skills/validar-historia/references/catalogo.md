# Catálogo de referencia — Patitas Valientes

Derivado del código en `master · 0a15d96`. Si una comprobación es importante, verifícala
contra el código: esta referencia puede haber quedado atrás. La fuente completa está en
`docs/casos-de-uso.html` y `docs/arquitectura.html`.

## Actores

| Actor | Cómo se crea la cuenta |
|---|---|
| Administrador | Provisionada fuera de la app |
| Voluntario | Solo la crea un administrador (CU-AD05). No hay autorregistro. |
| Adoptante | Autorregistro desde la app (CU-ADO01) |

No existe ningún otro rol. `SessionManager.getUserRole()` devuelve exactamente uno de esos
tres strings, y toda ramificación por rol los compara con `equalsIgnoreCase`.

Actor secundario: el **servidor de notificaciones** (Node en Render) y el **cron**
(cron-job.org). Ninguno inicia un caso de uso, así que no pueden ser el «como» de una
historia.

## Matriz de responsabilidades

| Entidad | Administrador | Voluntario | Adoptante |
|---|---|---|---|
| Mascotas | crea, edita, activa | consulta | consulta el catálogo |
| Cuentas | crea voluntarios, edita, activa | edita solo la suya | se registra y edita la suya |
| Adopciones | acepta, rechaza, termina | cierra al retirar | inicia y firma |
| Contratos | revisa | anula al retirar | firma y descarga |
| Seguimientos | asigna y reasigna | conduce, retira, finaliza | responde |
| Historial médico | sin acceso | registra y edita | registra el de su mascota |

Esta matriz es la prueba más útil: una historia cuyo actor no tiene el permiso que pide está
mal, o es un cambio de permisos que hay que declarar como tal.

## Casos de uso

**Comunes a los tres roles**
- CU-G01 Iniciar sesión
- CU-G02 Consultar y editar el perfil
- CU-G03 Cambiar la contraseña
- CU-G04 Consultar las notificaciones recibidas

**Administrador**
- CU-AD01 Registrar una mascota
- CU-AD02 Editar la información de una mascota
- CU-AD03 Activar o desactivar una mascota
- CU-AD04 Consultar y filtrar la lista de mascotas
- CU-AD05 Crear la cuenta de un voluntario
- CU-AD06 Consultar y buscar usuarios
- CU-AD07 Editar los datos de un usuario
- CU-AD08 Activar o desactivar una cuenta
- CU-AD09 Revisar y aceptar una solicitud de adopción
- CU-AD10 Rechazar una solicitud de adopción
- CU-AD11 Aprobar los requisitos finales y terminar la adopción
- CU-AD12 Solicitar el reenvío de los requisitos finales
- CU-AD13 Asignar un seguimiento a un voluntario
- CU-AD14 Reasignar un seguimiento a otro voluntario

**Voluntario**
- CU-VOL01 Consultar la lista de mascotas registradas
- CU-VOL02 Registrar una vacuna o una desparasitación
- CU-VOL03 Editar o eliminar un registro del historial médico
- CU-VOL04 Consultar sus seguimientos asignados
- CU-VOL05 Conversar con el adoptante
- CU-VOL06 Programar el plan de recordatorios
- CU-VOL07 Retirar la mascota del adoptante
- CU-VOL08 Finalizar el seguimiento
- CU-VOL09 Consultar un seguimiento cerrado

**Adoptante**
- CU-ADO01 Crear una cuenta
- CU-ADO02 Explorar el catálogo de mascotas
- CU-ADO03 Enviar una solicitud de adopción
- CU-ADO04 Consultar sus solicitudes enviadas
- CU-ADO05 Firmar el contrato y enviar los requisitos finales
- CU-ADO06 Descargar el contrato en PDF
- CU-ADO07 Consultar sus mascotas adoptadas
- CU-ADO08 Registrar y consultar el historial médico de su mascota
- CU-ADO09 Responder el seguimiento en el chat
- CU-ADO10 Consultar un seguimiento cerrado

**Subfunciones** (no son objetivo de nadie; no pueden ser una historia por sí solas)
- CU-S01 Aportar una imagen · CU-S02 Grabar o subir un video · CU-S03 Notificar a un usuario
- CU-S04 Programar un recordatorio · CU-S05 Cancelar los recordatorios

## Estados reales

Están en `Modelo/EstadosCuentas.java`. Si una historia nombra un estado que no está aquí, se
lo está inventando.

- **Cuentas y mascotas**: `ACTIVO`, `INACTIVO`
- **Adopciones y seguimientos**: `PENDIENTE`, `ACEPTADA`, `EN_REVISION`, `COMPLETADA`, `RECHAZADA`
- **Contratos**: `NO_FIRMADO`, `FIRMADO`, `ANULADO`
- **Fases del seguimiento**: `SEGUIMIENTO_DIARIO`, `SEGUIMIENTO_MENSUAL`, `SEGUIMIENTO_FINALIZADO`
- **Historial médico**: `VACUNA`, `DESPARASITACION`
- **Domicilio**: `PROPIO`, `ARRENDADO`
- **Filtros de mascota**: `CACHORRO`, `ADULTO`, `CANINO`, `FELINO`, `MACHO`, `HEMBRA`

Ciclo de la adopción: `PENDIENTE → ACEPTADA → EN_REVISION → COMPLETADA`, con salida a
`RECHAZADA` desde `PENDIENTE`, y un lazo `EN_REVISION → ACEPTADA` cuando se devuelven los
requisitos. Un retiro posterior lleva la adopción de `COMPLETADA` a `RECHAZADA`.

Ciclo del seguimiento: nace `ACTIVO` sin voluntario, pasa a `ACTIVO` con voluntario al
asignarlo, y cierra en `COMPLETADA` (finalizar) o `RECHAZADA` (retirar).

## Redacción de los mensajes

Cuando una historia cita un mensaje —`[Cuenta creada con éxito]`, `[Cédula no válida]`—
contrástalo con la convención de la sección «Mensajes al usuario» de `CLAUDE.md`, que es la
autoridad: usted siempre, `No se pudo` + infinitivo para los fallos, participio corto para
los éxitos, imperativo para lo que el usuario debe corregir, y nada de lenguaje de
desarrollador ni adverbios de relleno. Un mensaje por resultado.

Si el texto de la historia se ajusta mejor a esa convención que el del código, el que cambia
es el código: dilo así en el plan de cambios.

## Vocabulario de la casa

Usa estas palabras; si la historia usa otras para lo mismo, conviene alinearla.

- **seguimiento**, no «tracking» ni «monitoreo»
- **retirar la mascota**, no «cancelar la adopción» (retirar es la acción del voluntario)
- **finalizar el seguimiento**, no «cerrar» ni «terminar» (terminar es de la adopción)
- **adoptante**, **voluntario**, **administrador** — nunca «usuario» a secas
- **solicitud de adopción** para lo que está en `PENDIENTE`; **adopción** una vez aceptada
- **historial médico** cubre vacunas y desparasitaciones
- **plan de recordatorios** para los 19 avisos del seguimiento

A los usuarios finales nunca se les muestra `RECHAZADA`: la interfaz traduce los seguimientos
cerrados a «Cerrado» y «Finalizado».

## Reglas que el sistema ya impone

Una historia que las contradiga no es una historia nueva: es un cambio de comportamiento, y
hay que decirlo.

1. Una cuenta `INACTIVO` no puede iniciar sesión.
2. Un seguimiento cerrado es de solo lectura para los dos lados: ni mensajes, ni retiro, ni
   cambios en el plan.
3. Un seguimiento sin voluntario asignado no aparece en la lista del adoptante.
4. Solo un administrador crea voluntarios.
5. El retiro exige un motivo escrito, y ese motivo queda dentro de la conversación.
6. Los recordatorios del plan llegan al voluntario asignado, no al adoptante.
7. Los recordatorios de vacuna llegan al voluntario que los registró y también al adoptante,
   si la mascota ya está adoptada.
8. Un dispositivo queda asociado a una sola cuenta: al iniciar sesión, el token se libera de
   las demás.
9. El plan es 7 controles diarios, 8 mensuales y 4 trimestrales, a las 08:00.
10. Retirar devuelve la mascota al catálogo y anula el contrato, sin borrarlo.

## Pantallas, por si hay que estimar

35 Activities en `Vista/`, 16 Fragments, 16 adaptadores, 11 controladores. Todo acceso a datos
pasa por `Controlador/`. Las colecciones de Firestore son `Cuentas`, `Usuarios`, `Domicilios`,
`Mascotas`, `Adopciones`, `Seguimientos`, `ContratosAdopciones`, `ContratoGeneral`,
`Clausulas`, `NotificacionesProgramadas`. En Realtime Database: `chats`, `notificaciones`,
`historial_medico`, `presenciaChats`.
