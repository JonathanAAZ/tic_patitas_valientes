---
name: validar-historia
description: Valida una historia de usuario de Patitas Valientes contra el comportamiento real del código: comprueba la redacción (plantilla, criterios de aceptación, INVEST), verifica en los controladores y las pantallas qué hace la aplicación hoy, y entrega un plan de qué añadir, quitar o modificar — tanto en el texto de la historia como en el código: que el rol exista y tenga el permiso, que no duplique un caso de uso ya implementado, y que los estados y el vocabulario sean los reales del sistema. Úsala siempre que el usuario pegue o escriba una historia de usuario, un «Como… quiero… para…», criterios de aceptación o un elemento de backlog, y también cuando pregunte si una historia está bien redactada, si es válida, si se puede estimar, si ya existe algo así en la app, o qué habría que cambiar para implementarla — incluso si no usa la palabra «historia».
---

# Validar una historia de usuario

Una historia de usuario mal redactada se detecta leyendo la plantilla. Una historia que
**miente sobre el sistema** solo se detecta comparándola con el sistema, y es el error que
más caro sale: se estima, se planifica y se descubre en desarrollo que el rol no tenía ese
permiso, o que eso ya estaba hecho con otro nombre.

Este skill hace las dos cosas, y la segunda es la que aporta. Empieza por ella.

## Paso 1 — Sitúate con la referencia

Lee `references/catalogo.md`. Trae los tres actores, la matriz de permisos, los 37 casos de
uso con su código, los estados que existen de verdad, el vocabulario de la casa y las diez
reglas que el sistema ya impone.

La referencia sirve para orientarte rápido, pero es un resumen y lleva escrito el commit al
que corresponde. **No es la autoridad.** Lo que decide es el código.

## Paso 2 — Comprueba el comportamiento real en el código

Esto no es opcional. Una historia se valida contra lo que la aplicación hace, no contra lo
que un resumen dice que hace, porque el resumen puede estar desactualizado y porque las
historias fallan justo en los detalles que un resumen no guarda: qué pasa cuando el estado ya
cambió, quién recibe el aviso, qué se guarda y qué no.

Empieza por localizar la funcionalidad:

```
sh scripts/rastrear.sh <término>
```

Dale un término del dominio de la historia —`retirar`, `vacuna`, `contrato`, `seguimiento`,
`solicitud`— y devuelve los métodos de controlador implicados, las pantallas, los archivos con
las colecciones que tocan y los estados relacionados. Es un punto de partida: **lee lo que
encuentre**, no te quedes en la lista.

Con eso en mano, responde para la historia concreta:

- **¿Dónde empieza?** La Activity o el Fragment que el actor usaría.
- **¿Qué método lo resuelve?** En `Controlador/`. Si no existe ninguno, eso ya es un hallazgo.
- **¿Qué escribe y dónde?** Colecciones de Firestore, nodos de Realtime Database, Storage.
- **¿A quién notifica?** `ControladorNotificaciones` dice quién recibe qué.
- **¿Qué guarda la acción?** Comprobaciones de estado, permisos, campos obligatorios.
- **¿Qué pasa si falla a medias?** Varias acciones tocan más de una colección.

Lee también `docs/casos-de-uso.html` cuando necesites el flujo completo de un caso, con sus
flujos alternos y postcondiciones.

No adivines. Una validación inventada es peor que ninguna, porque el usuario la va a creer y
va a planificar sobre ella. Si algo no lo puedes comprobar, dilo con esas palabras.

## Paso 3 — Las cuatro preguntas que importan

**1. ¿El actor existe y tiene el permiso?**

Solo hay Administrador, Voluntario y Adoptante. «Como usuario», «como gestor», «como
sistema» no son actores de esta aplicación. Y existir no basta: contrasta la acción con la
matriz. «Como adoptante quiero registrar una mascota» tiene un actor real y un permiso que no
tiene — en la matriz, las mascotas las crea el administrador. Eso no es un detalle de
redacción, es la historia pidiendo otra cosa de la que cree pedir.

Cuando el permiso falte, ofrece las dos lecturas: o el actor está mal, o la historia quiere
cambiar los permisos, y entonces hay que decirlo explícitamente porque arrastra consecuencias.

**2. ¿Ya existe?**

Compara contra los 37 casos de uso. Hay tres respuestas, y conviene nombrarlas sin rodeos:

- **Duplica** un caso de uso: di cuál. No es una historia nueva; si acaso es una petición de
  cambio sobre ese caso, y vale la pena redactarla así.
- **Extiende** un caso: di cuál y qué añade. Es la más común y la más útil, porque hereda los
  criterios de aceptación que ya existen.
- **Es nueva**: entonces pasa a la pregunta 4.

Las subfunciones (CU-S01 a CU-S05) no son objetivo de nadie. «Como voluntario quiero subir
una foto» no es una historia: subir una foto es un medio, nunca el fin.

**3. ¿Los estados y las palabras son los del sistema?**

Si la historia nombra un estado, tiene que estar en la lista real. «Cuando la adopción esté
archivada» no se puede implementar porque no hay `ARCHIVADA`. Lo mismo con el vocabulario:
«cancelar el seguimiento» obliga a adivinar si se refiere a retirar la mascota (CU-VOL07) o a
finalizar el seguimiento (CU-VOL08), que son cosas muy distintas — una devuelve la mascota al
catálogo y la otra no.

Comprueba también las diez reglas de la referencia. Una historia que pide que el adoptante
responda en un seguimiento cerrado no es ambigua: contradice una regla implementada, y eso
hay que señalarlo como cambio de comportamiento.

**4. ¿Qué distancia hay entre la historia y lo que el código hace hoy?**

Esta es la pregunta que cierra el análisis, y se responde con lo que viste en el paso 2. Hay
cuatro distancias posibles, y conviene nombrarla sin rodeos:

- **Cero.** Ya funciona así. Di dónde —el método y la pantalla— para que se pueda comprobar.
  No hay nada que hacer salvo cerrar la historia o convertirla en una prueba.
- **Parcial.** Funciona para unos casos y no para otros, o le falta una pieza. Lo más común.
- **Contraria.** El código hace deliberadamente lo opuesto. Entonces la historia no es una
  incorporación, es un cambio de comportamiento, y hay que decir qué deja de funcionar.
- **Nueva.** No hay nada. Nombra lo que habría que tocar.

## Paso 4 — El plan de cambios

Lo que el usuario necesita al final no es un diagnóstico, es saber qué mover. Dalo en dos
niveles, porque son decisiones distintas y las toman en momentos distintos.

**En la historia** — qué añadir, quitar o modificar en el texto. Sé concreto: no «falta
precisión», sino «quitar “de forma automática”, que decide la implementación» o «añadir qué
pasa cuando el seguimiento ya está cerrado». Esto es lo que el usuario copia y pega.

**En la aplicación** — qué habría que añadir, quitar o modificar en el código para que la
historia sea cierta. Nombra archivos y métodos reales, los que viste en el paso 2. No
diseñes la solución entera: basta con que quien estime sepa si son dos horas o dos semanas, y
que quien implemente sepa por dónde entrar.

Cuando la distancia sea cero, dilo y no rellenes la sección con trabajo imaginario. Una
validación que siempre encuentra cosas que cambiar deja de ser información.

## La forma, que también cuenta

Revisa, sin convertirlo en burocracia:

- **Plantilla** «Como ⟨rol⟩, quiero ⟨acción⟩, para ⟨beneficio⟩». El «para» tiene que ser un
  beneficio, no la acción repetida. «Para poder retirar la mascota» cuando la acción ya era
  retirar la mascota no explica nada.
- **Qué, no cómo.** Si la historia menciona Firestore, `startActivity` o un nombre de clase,
  está decidiendo la implementación en el sitio equivocado.
- **Criterios de aceptación**, en forma verificable. Si no están, escríbelos tú en la
  reescritura: son la mitad del valor de una historia.

  No hay un formato correcto. El proyecto usa el estilo «El sistema DEBE…» (RFC 2119), que
  enlaza con los códigos de requisito funcional, y hay historias en `Dado/Cuando/Entonces`.
  **Respeta el formato de la historia que te pasan** y no propongas cambiar de convención: lo
  que hace válido un criterio es otra cosa, y es lo único que debes exigir:

  1. Nombra un resultado **observable** — «DEBE validar las credenciales» no lo es, porque no
     dice qué se ve; «DEBE presentar un único mensaje de error» sí.
  2. Nombra la **condición** en la que aplica, porque sin ella no se sabe cuándo probarlo.
  3. Es **falsable**: se puede imaginar el caso en que no se cumple.

  El estilo `DEBE` tiende a olvidar el punto 2, así que ahí es donde conviene mirar.
- **Tamaño.** Una historia que abarca el proceso de adopción completo es una épica. Pártela y
  di por dónde.
- **Independiente y valiosa.** Si solo tiene sentido junto a otras tres, dilo.

## Cómo presentar el resultado

Sigue esta estructura. El veredicto va primero porque es lo que el usuario necesita saber
antes que nada.

```markdown
## Veredicto
**Lista para desarrollo** | **Necesita ajustes** | **Hay que rehacerla**
Una frase con el motivo principal.

## Qué hace la aplicación hoy
Lo que comprobaste en el código, en dos o tres frases, con los nombres reales:
«⟨Pantalla⟩ llama a ⟨Controlador.metodo⟩, que escribe ⟨campo⟩ en ⟨colección⟩ y notifica a
⟨quién⟩. Está guardado por ⟨comprobación⟩.»
Esto es lo que sostiene todo lo demás, así que va antes que el análisis.

**Distancia con la historia**: cero / parcial / contraria / nueva.

## Encaje
- **Actor**: ⟨rol⟩ — existe / no existe
- **Permiso**: lo tiene / no lo tiene, según la matriz ⟨entidad⟩
- **Caso de uso**: duplica CU-XXX / extiende CU-XXX / es nueva
- **Estados y vocabulario**: ⟨lo que haya que corregir, o «correctos»⟩
- **Reglas**: ⟨la regla que contradice, si contradice alguna⟩

## Forma
Solo los puntos que fallan, una línea cada uno. Si está bien, dilo en una línea.

## Qué cambiar en la historia
- **Añadir**: ⟨…⟩
- **Quitar**: ⟨…⟩
- **Modificar**: ⟨…⟩

Omite la línea que no aplique. Si no hay nada que cambiar, dilo y pasa a la siguiente sección.

## Qué cambiar en la aplicación

| | Dónde | Qué |
|---|---|---|
| Añadir | `Controlador/X.java` | ⟨…⟩ |
| Modificar | `Vista/YActivity.java` | ⟨…⟩ |
| Quitar | ⟨…⟩ | ⟨…⟩ |

Si la distancia es cero, sustituye la tabla por una línea: «Nada: ya funciona así en
⟨método⟩». Si es contraria, añade debajo qué comportamiento actual se pierde.

## Historia reescrita
⟨La historia, en el mismo formato y la misma convención que te pasaron.⟩

**Criterios de aceptación**
⟨En el formato del original. Si venían como «El sistema DEBE…», devuélvelos así; si venían
como «Dado/Cuando/Entonces», así. Cambiar de convención obliga a rehacer el resto del
backlog y no aporta nada.⟩

Los criterios salen del comportamiento que comprobaste, no de lo que sería razonable: si el
código exige un motivo escrito, eso es un criterio. Y si inviertes o añades alguno respecto
al original, dilo en una línea al final para que se note qué cambió de fondo.
```

Si la historia está bien, dilo en tres líneas y no inventes problemas para justificar el
análisis. Una validación que siempre encuentra defectos deja de ser útil.

Si te pasan varias historias a la vez, valida cada una con esta estructura y añade al final
una línea sobre si se solapan entre ellas o si alguna depende de otra — eso no se ve mirando
una sola.
