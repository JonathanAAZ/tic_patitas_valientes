# Plan de mejoras — TIC_PV

Documento de trabajo con las mejoras detectadas tras el análisis estructural del proyecto.
Cada punto incluye la evidencia medida, el porqué y el cambio concreto.

**Estado del proyecto al momento del análisis:** Android nativo en Java, minSdk 24 / targetSdk 34,
MVC manual (`Modelo/`, `Vista/`, `Adaptadores/`, `Controlador/`, `Interfaces/`),
Firebase (Firestore, Realtime Database, Storage, Auth, FCM) + Cloudinary + servidor Node en Render.

---

## Resumen ejecutivo

| # | Acción | Esfuerzo | Impacto |
|---|---|---|---|
| 1 | Rotar credenciales de Cloudinary + unsigned preset | 1 tarde | Crítico (seguridad) |
| 2 | `toObject()` + `@PropertyName` en los modelos | 2-3 días | Muy alto |
| 3 | Constantes para colecciones y claves de Intent | 2 horas | Alto |
| 4 | Corregir el doble `setContentView` (17 Activities) | 2 horas | Alto (bug real) |
| 5 | `requireArguments()` en lugar de `assert` (8 Fragments) | 30 min | Alto (bug en release) |
| 6 | Unificar en Glide + rol como enum | 2 horas | Medio |
| 7 | Desnormalizar `Adopcion` (sacar Firestore de los adaptadores) | 1 día | Medio-alto |
| 8 | Helper de insets en `ControladorUtilidades` | 3 horas | Medio |
| 9 | Aplanar callbacks de `ControladorNotificaciones` | 1 día | Medio |
| 10 | Unificar en ViewBinding | 1-2 días | Medio |

Los puntos 1, 3, 4, 5 y 6 son mecánicos y de bajo riesgo. **El punto 2 es el que transforma el código.**

---

# Nivel 1 — Corregir (son defectos, no estilo)

## 1.1 Credenciales de Cloudinary expuestas

**Evidencia:** `MainActivity.java:96-103` y `AndroidManifest.xml:82-84`.

```java
private void configuracionInicialCloudinary() {
    Map<String, String> config = new HashMap<>();
    config.put("cloud_name", "de3pikkwa");
    config.put("api_key", "176417194926829");
    config.put("api_secret", "LITvQ_VpkeqIZcvbZwrS2JAl0as");  // ← secreto en el APK
    MediaManager.init(this, config);
}
```

El `api_secret` está en el código **y ya commiteado en el historial de git**. Cualquiera que
descompile el APK obtiene control total de la cuenta de Cloudinary (subir, borrar, consumir cuota).

**Acción:**
1. **Rotar el secret** en el panel de Cloudinary. Esto es lo primero, porque el valor actual ya
   no puede considerarse privado (borrarlo del código no lo borra del historial ni de los APK ya generados).
2. Migrar a **unsigned upload preset**: se crea un preset en el panel de Cloudinary y la app solo
   necesita el `cloud_name`, que no es secreto.

```java
private void configuracionInicialCloudinary() {
    Map<String, String> config = new HashMap<>();
    config.put("cloud_name", "de3pikkwa");   // no es secreto
    MediaManager.init(this, config);
}

// en cada subida
MediaManager.get().upload(uri)
        .unsigned("nombre_del_preset")
        .option("folder", "mascotas")
        .dispatch();
```

3. Eliminar el `meta-data` `CLOUDINARY_URL` del `AndroidManifest.xml`.

> Hay **10 puntos de subida** en el proyecto que hay que ajustar al modo unsigned.

**Nota:** es el único punto de esta lista con consecuencias fuera del código.

---

## 1.2 Doble `setContentView` en 17 de 34 Activities

**Evidencia:** patrón repetido en la mitad de las Activities.

```java
setContentView(R.layout.activity_x);                       // ← infla una jerarquía...
ViewCompat.setOnApplyWindowInsetsListener(
        findViewById(R.id.main), ...);                     // ← ...los insets se aplican AQUÍ...
binding = ActivityXBinding.inflate(getLayoutInflater());
setContentView(binding.getRoot());                         // ← ...y esta jerarquía la reemplaza
```

No es solo desperdicio de un inflado: **los insets se aplican sobre una vista que se descarta**.
La que el usuario ve nunca recibe el padding de las barras del sistema.

**Corrección:**

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);

    binding = ActivityXBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());                     // un solo setContentView

    ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        return insets;
    });
    // ...
}
```

---

## 1.3 `assert getArguments() != null` en 8 de 15 Fragments

Las aserciones de Java están **deshabilitadas por defecto en tiempo de ejecución**. Ese `assert`
no protege nada en el APK que entregues: solo pospone el `NullPointerException` a la línea siguiente,
sin mensaje útil.

```java
// antes
assert getArguments() != null;
String id = getArguments().getString("id");

// después
String id = requireArguments().getString("id");
```

`requireArguments()` falla igual si no hay argumentos, pero con un mensaje claro
(`Fragment X does not have any arguments`) y **en debug y en release por igual**.

---

# Nivel 2 — Máxima ganancia, esfuerzo bajo

## 2.1 `toObject()` + `@PropertyName` — la mejora más importante

**Evidencia medida:**
- `toObject()` usado en **0 archivos**
- **314 llamadas manuales** a `getString("...")` / `getLong(...)` / `getBoolean(...)`

Hoy cada consulta mapea a mano, campo por campo:

```java
Adopcion solicitud = new Adopcion();
solicitud.setId(documentSnapshot.getId());
solicitud.setEstadoAdopcion(documentSnapshot.getString("estado"));
solicitud.setFechaEmision(documentSnapshot.getString("fechaEmision"));
solicitud.setIdMascota(documentSnapshot.getString("idMascota"));
solicitud.setIdAdoptante(documentSnapshot.getString("idAdoptante"));
// ...y así 12 líneas más, repetidas en CADA consulta de CADA controlador
```

Esto genera la clase de bug más común del proyecto: **si escribes mal el nombre del campo,
compila perfecto y recibes `null` en silencio**.

### El problema de los nombres desalineados

Los nombres en Firestore no coinciden con los del modelo (`nombre` vs `nombreMascota`,
`estado` vs `estadoMascota`). Eso se resuelve **una sola vez** en el modelo, sin tocar la base de datos:

```java
@IgnoreExtraProperties
public class Mascota implements Parcelable {

    private String nombreMascota;
    private String estadoMascota;

    public Mascota() { }   // obligatorio para toObject()

    @PropertyName("nombre")
    public String getNombreMascota() { return nombreMascota; }

    @PropertyName("nombre")
    public void setNombreMascota(String nombreMascota) { this.nombreMascota = nombreMascota; }

    @PropertyName("estado")
    public String getEstadoMascota() { return estadoMascota; }

    @PropertyName("estado")
    public void setEstadoMascota(String estadoMascota) { this.estadoMascota = estadoMascota; }
}
```

### El resultado

Cada consulta pasa de ~14 líneas a 2:

```java
Mascota mascota = document.toObject(Mascota.class);
mascota.setId(document.getId());   // el id del documento no viaja en los campos
```

### Requisitos antes de migrar

- Constructor vacío público en cada modelo (ya lo tienen por `Parcelable`).
- Getter y setter públicos para cada campo que se quiera mapear.
- `@IgnoreExtraProperties` en la clase: sin esto, si el documento trae un campo que la clase
  no conoce, Firestore **lanza excepción**.
- Los campos que no deben persistirse se marcan con `@Exclude` en el getter.

### Cómo hacerlo sin romper todo

Migrar **una entidad a la vez**, probarla a fondo y recién entonces seguir.
Orden sugerido por frecuencia de uso: `Mascota` → `Adopcion` → `Cuenta` → `Usuario` → el resto.

> Estimación: elimina entre 400 y 500 líneas de código y una categoría entera de bugs.

---

## 2.2 Constantes para las colecciones de Firestore

**Evidencia:** nombres de colección repetidos como string crudo.

| Colección | Repeticiones |
|---|---|
| `"Cuentas"` | 37 |
| `"Usuarios"` | 24 |
| `"Adopciones"` | 20 |
| `"Mascotas"` | 19 |
| `"Domicilios"` | 9 |
| `"Seguimientos"` | 8 |
| `"NotificacionesProgramadas"` | 7 |
| `"ContratosAdopciones"` | 6 |

Un typo en cualquiera de estas 130 apariciones compila sin problema y falla en runtime,
normalmente sin error visible (una consulta a una colección inexistente devuelve vacío).

```java
package com.example.tic_pv.Modelo;

public final class Colecciones {
    public static final String CUENTAS = "Cuentas";
    public static final String USUARIOS = "Usuarios";
    public static final String ADOPCIONES = "Adopciones";
    public static final String MASCOTAS = "Mascotas";
    public static final String DOMICILIOS = "Domicilios";
    public static final String SEGUIMIENTOS = "Seguimientos";
    public static final String NOTIFICACIONES_PROGRAMADAS = "NotificacionesProgramadas";
    public static final String CONTRATOS_ADOPCIONES = "ContratosAdopciones";

    private Colecciones() { }   // clase de constantes, no se instancia
}
```

Uso: `db.collection(Colecciones.CUENTAS)` — con autocompletado y verificación del compilador.

### Lo mismo para las claves de Intent y Bundle

`"id"` aparece 24 veces, `"rol"` 11, `"idMascota"` 11. Mismo riesgo: si el `putExtra` y el
`getStringExtra` no coinciden exactamente, el valor llega `null`.

```java
public final class Extras {
    public static final String ID = "id";
    public static final String ROL = "rol";
    public static final String ID_MASCOTA = "idMascota";
    public static final String ID_VOLUNTARIO = "idVoluntario";

    private Extras() { }
}
```

---

## 2.3 Elegir una sola librería de imágenes

**Evidencia:** Glide en 16 archivos, Picasso en 4 — haciendo exactamente lo mismo.

Dos cachés independientes, dos comportamientos de reintento, dos librerías en el APK.

**Acción:** migrar los 4 usos de Picasso a Glide y eliminar la dependencia de `build.gradle.kts`.
La traducción es directa:

```java
// Picasso
Picasso.get().load(url).placeholder(R.drawable.x).into(imageView);

// Glide
Glide.with(context).load(url).placeholder(R.drawable.x).into(imageView);
```

---

## 2.4 Rol como enum

**Evidencia:** los roles son los únicos estados que viajan como string crudo
(`"Voluntario"`, `"Adoptante"`, `"Administrador"`), mientras que especies, sexos,
tipos de domicilio y estados de notificación ya están en el enum `EstadosCuentas`.

Como en todo el código se compara con `equalsIgnoreCase`, un enum encaja **sin migrar la base de datos**:

```java
public enum Rol {
    ADMINISTRADOR, VOLUNTARIO, ADOPTANTE
}
```

```java
// antes
if (rolUsuario.equalsIgnoreCase("Voluntario")) { ... }

// después
if (rolUsuario.equalsIgnoreCase(Rol.VOLUNTARIO.toString())) { ... }
```

### Renombrar `EstadosCuentas`

Ese enum ya no contiene solo estados de cuentas: acumula especies, sexos, tipos de domicilio
y estados de notificación. El nombre miente. `Estados` o `Catalogos` describe mejor lo que es.

### Bonus: la rama sin `else`

`MisSeguimientosActivity.java:43` tiene una comparación de rol a medio terminar:

```java
if (rolUsuario.equalsIgnoreCase("Voluntario")) {
    controladorUtilidades.reemplazarFragments(...);
}
// sin else → si el usuario es Adoptante, la pantalla queda en blanco sin explicación
```

Si la vista del adoptante todavía no existe, al menos mostrar un mensaje en lugar de una pantalla vacía.

---

# Nivel 3 — Estructura

## 3.1 Sacar las consultas de Firestore de los Adaptadores

**Evidencia:** `SolicitudesPendientesAdaptador.java:100` y `:121` consultan Firestore
dentro de `onBindViewHolder`.

`onBindViewHolder` se ejecuta **cada vez que un ítem entra en pantalla**. Al hacer scroll
arriba y abajo, las mismas consultas se repiten indefinidamente: se pagan lecturas de más,
las respuestas llegan desordenadas y en vistas recicladas aparecen nombres cruzados
(el nombre de una mascota en la tarjeta de otra).

**La buena noticia: ya sabes resolverlo.** El modelo `Seguimiento` guarda `nombreAdoptante`,
`nombreMascota` y `nombreVoluntario` **desnormalizados**, precisamente para no consultar al pintar.

**Acción:** aplicar el mismo criterio a `Adopcion`. Al crear la solicitud, guardar junto a los
ids el nombre de la mascota y el del adoptante. El adaptador pasa a ser lo que debe ser:
un traductor de datos a vistas, sin red.

---

## 3.2 Extraer el boilerplate de insets

**Evidencia:** las mismas 6 líneas repetidas en 34 Activities.

Un helper en `ControladorUtilidades`, que es donde ya vive el resto de utilidades del proyecto:

```java
public void aplicarInsetsSistema(View root) {
    ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        return insets;
    });
}
```

En cada Activity queda una línea: `controladorUtilidades.aplicarInsetsSistema(binding.main);`

> Se prefiere el helper sobre una `BaseActivity` porque encaja con la organización actual del
> proyecto y no obliga a cambiar la jerarquía de herencia de 34 clases.
> Conviene hacerlo **junto con el punto 1.2**, ya que se tocan las mismas líneas.

---

## 3.3 Aplanar los callbacks anidados

**Evidencia:** `ControladorNotificaciones.java` tiene **29 cierres `});`** y anidamiento
de hasta **6 niveles** entre las líneas 580 y 720.

No hace falta introducir RxJava ni corrutinas. Basta extraer cada nivel a un método privado
con nombre descriptivo:

```java
// antes: una pirámide de 6 niveles imposible de leer completa en pantalla

// después
private void eliminarNotificacionesDeHistorial(String idHistorial, String idMascota) {
    buscarNotificaciones(idHistorial, notificaciones ->
            eliminarCadaNotificacion(notificaciones, idMascota));
}

private void eliminarCadaNotificacion(List<Notificacion> notificaciones, String idMascota) {
    // ...
}
```

Cada método cabe en pantalla y el flujo se lee de arriba abajo. Es refactor puro:
no cambia el comportamiento, solo la forma.

**Contexto:** los controladores suman **4909 líneas** en total. `ControladorNotificaciones`
es el peor caso, pero el criterio aplica a todos.

---

## 3.4 Unificar en ViewBinding

**Evidencia:** ViewBinding en 33 archivos, `findViewById` en 36 — muchos con **ambos**.

`viewBinding = true` ya está activo. Mantener las dos vías significa tener dos referencias
distintas a la misma vista, y es justo donde aparecen los `null` difíciles de explicar.

`MainActivity.java:71,82` es un ejemplo: usa `findViewById` con cast explícito
(`(Button) findViewById(...)`), innecesario desde Android API 26.

---

# Nivel 4 — Si queda tiempo

## 4.1 `strings.xml`

**Evidencia:** 557 textos hardcodeados en los layouts vs 31 entradas en `strings.xml`.

Migrarlos todos es mucho trabajo para poco retorno inmediato. **Sí vale la pena** migrar
los mensajes de error y validación, que son los que se repiten literalmente en varias pantallas
y donde una corrección de redacción hoy obliga a buscar y editar en N sitios.

## 4.2 Tests

Actualmente solo existen los `ExampleUnitTest` autogenerados por Android Studio.

`validadorDeCedula()` es **lógica pura, sin dependencias de Android**: es un test unitario de
10 líneas que se ejecuta en la JVM sin emulador. Para un proyecto de titulación, tener tests
sobre las validaciones de negocio da respaldo formal con muy poco esfuerzo.

```java
@Test
public void cedulaValida_devuelveTrue() {
    assertTrue(validador.validadorDeCedula("1710034065"));
}

@Test
public void cedulaConDigitoVerificadorIncorrecto_devuelveFalse() {
    assertFalse(validador.validadorDeCedula("1710034066"));
}
```

Buenos siguientes candidatos: validación de correo, de teléfono y de edad.

## 4.3 Limpieza menor

- **`package` en el `AndroidManifest.xml`**: el build ya avisa que `package="com.example.tic_pv"`
  está obsoleto y se ignora (el valor real está en `namespace` de `build.gradle.kts`). Borrarlo
  silencia el warning.
- **Nombres de campos inconsistentes**: `db` en unos controladores y `bd` en otros para la misma
  instancia de Firestore. Elegir uno.
- **Código comentado**: `MainActivity` tiene el método `enviarNotificacion()` completo comentado
  (líneas 160-188) y varias llamadas comentadas. Git ya guarda el historial; borrarlo.
- **`limpiarFotosViejas()`**: el comentario dice "24 horas" pero el valor es `5 * 60 * 1000`
  (5 minutos). Uno de los dos está mal.
- **`signingConfig`**: `app/build.gradle.kts` no define ninguno, así que el APK de release saldría
  sin firmar. Configurarlo antes de la entrega final.

---

# Orden de ataque recomendado

1. **Rotar Cloudinary** (1.1) — es el único riesgo hacia afuera, no depende de nada más.
2. **Constantes** (2.2) — 2 horas, mecánico, y deja el terreno listo para el resto.
3. **`setContentView` + helper de insets** (1.2 y 3.2 juntos) — se tocan las mismas líneas.
4. **`requireArguments()`** (1.3) — 30 minutos.
5. **Glide único + enum de rol** (2.3 y 2.4).
6. **`toObject()` + `@PropertyName`** (2.1) — el grande. Una entidad a la vez, empezando por `Mascota`.
7. **Desnormalizar `Adopcion`** (3.1).
8. El resto, según tiempo disponible.

---

## Tareas pendientes fuera de esta lista

- [ ] Eliminar la carpeta vacía `app/src/main/java/com/example/tic_pv/.claude`
      (Android Studio mantenía un handle abierto; requiere cerrar el IDE o *Invalidate Caches*).
- [ ] Commitear los cambios pendientes: `.gitignore` y `SeguimientoVoluntarioChatActivity.java`
      (listeners de las 14 preguntas frecuentes).
- [ ] Funcionalidad de respuesta al seguimiento por parte del **adoptante** — verificado que
      nunca existió en el historial de git; faltan 4 piezas.
- [ ] Archivo de convenciones (`CLAUDE.md`) para mantener la estructura en nuevas funcionalidades.
