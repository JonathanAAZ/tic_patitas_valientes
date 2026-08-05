# CLAUDE.md — Convenciones del proyecto TIC_PV

Guía para trabajar en este proyecto. **Toda funcionalidad nueva debe seguir estas reglas.**
El plan de mejoras pendientes está en [MEJORAS.md](MEJORAS.md).

---

## El proyecto

App Android nativa en **Java** (sin Kotlin ni Compose) para gestión de adopción de mascotas.

- minSdk 24 · targetSdk 34 · compileSdk 34
- **ViewBinding** activo (`viewBinding = true`), layouts XML
- MVC manual: sin inyección de dependencias, sin ViewModels, sin LiveData
- Firebase: Firestore, Realtime Database (chats), Storage, Auth, Cloud Messaging
- Cloudinary para multimedia pesada · Retrofit a servidor Node en Render

### Compilar

Gradle necesita Java 11+. El JDK que funciona es el que trae Android Studio:

```bash
export JAVA_HOME="/c/Program Files/Android/Android Studio/jbr"
./gradlew compileDebugJavaWithJavac
```

Sin ese `JAVA_HOME` el build falla con *"Dependency requires at least JVM runtime version 11"*.

---

## Estructura de paquetes

| Paquete | Qué contiene |
|---|---|
| `Modelo/` | POJOs, todos `Parcelable`. También enums (`EstadosCuentas`) y `RetrofitClient` |
| `Vista/` | Activities |
| `Vista/Fragments/` | Fragments |
| `Adaptadores/` | Adaptadores de RecyclerView |
| `Controlador/` | Acceso a datos y lógica. **Todo Firestore/Storage/Cloudinary vive aquí** |
| `Interfaces/` | Interfaces de Retrofit (`ApiService`) |

### Reglas de ubicación

- **Ninguna Activity, Fragment ni Adaptador consulta Firestore directamente.** Se llama a un
  controlador y se recibe el resultado por callback.
- Los controladores exponen callbacks con interfaces anidadas:
  ```java
  public interface Callback<T> {
      void onComplete(T result);
      void onError(Exception e);
  }
  ```
- Helpers compartidos (cargar imágenes, validaciones, reemplazar fragments) van en
  `ControladorUtilidades`, no duplicados en cada pantalla.

---

## ⚠️ Teclado e insets — OBLIGATORIO en toda pantalla con entrada de texto

**Este es el punto que más se olvida y el que rompió el chat del adoptante.**

La app usa `EdgeToEdge.enable()`, lo que desactiva el ajuste automático de la ventana
(`setDecorFitsSystemWindows(false)`). Eso significa que **el sistema ya no sube el contenido
cuando aparece el teclado**: hay que manejarlo a mano. Sin esto, el campo de texto y los botones
quedan tapados.

Son **tres piezas** y las tres son necesarias.

### 1. Manifest

```xml
<activity
    android:name=".Vista.MiActivity"
    android:exported="false"
    android:windowSoftInputMode="adjustResize" />
```

Sin `adjustResize` los insets del teclado no se despachan a la vista.

### 2. Padding que sigue al teclado

```java
ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
    Insets teclado = insets.getInsets(WindowInsetsCompat.Type.ime());
    v.setPadding(systemBars.left,
            systemBars.top,
            systemBars.right,
            Math.max(systemBars.bottom, teclado.bottom));
    return insets;
});
```

El `Math.max` es importante: evita sumar dos veces. Con el teclado abierto manda su altura,
con el teclado cerrado manda la barra de navegación.

### 3. Desplazar la lista para que el último elemento no quede oculto

En pantallas con RecyclerView (chats, listas largas):

```java
// Al abrirse el teclado el RecyclerView se encoge, así que volvemos al último elemento
binding.recyclerView.addOnLayoutChangeListener(
        (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                desplazarAlUltimoMensaje();
            }
        });
```

```java
private void desplazarAlUltimoMensaje() {
    if (!listaMensajes.isEmpty()) {
        binding.recyclerView.post(() ->
                binding.recyclerView.scrollToPosition(listaMensajes.size() - 1));
    }
}
```

Se reacciona a que la vista **se encoja**, no al evento del teclado: así también funciona
si la lista se acorta por otro motivo. El `post()` espera a que termine el layout antes
de hacer scroll — sin él el scroll se calcula con la altura vieja y no llega al final.

> **Referencia:** `SeguimientoVoluntarioChatActivity` y `SeguimientoAdoptanteChatActivity`
> tienen las tres piezas aplicadas. Copiar de ahí.

---

## Activity nueva — plantilla

```java
public class MiActivity extends AppCompatActivity {

    private ActivityMiActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // UN SOLO setContentView, y el binding primero
        binding = ActivityMiActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets teclado = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right,
                    Math.max(systemBars.bottom, teclado.bottom));
            return insets;
        });

        // Validar los extras ANTES de configurar nada
        MiModelo modelo = getIntent().getParcelableExtra("modelo");
        if (modelo == null) {
            Toast.makeText(this, "No se pudo abrir la pantalla", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ... resto de la configuración

        // Si el layout tiene flecha de regresar, conectarla
        binding.iVRegresar.setOnClickListener(v -> finish());
    }
}
```

**Registrar siempre en `AndroidManifest.xml`** (`android:exported="false"`, más
`windowSoftInputMode="adjustResize"` si hay campos de texto).

### Errores a NO repetir

| ❌ No hacer | ✅ Hacer |
|---|---|
| `setContentView(R.layout.x)` y luego `setContentView(binding.getRoot())` | Un solo `setContentView`, con el binding |
| Aplicar insets sobre `findViewById(R.id.main)` antes de inflar el binding | Aplicarlos sobre `binding.main` |
| `assert modelo != null;` | `if (modelo == null) { ...; finish(); return; }` |
| Dejar la flecha de regresar sin listener | Conectarla a `finish()` |
| Mezclar `findViewById` con ViewBinding | Solo ViewBinding |

> `assert` está **deshabilitado en release**: no protege nada en el APK que se entrega.

---

## Fragment nuevo — plantilla

```java
public class MiFragment extends Fragment {

    private FragmentMiFragmentBinding binding;
    private final ControladorX controlador = new ControladorX();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMiFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // requireArguments(), NO assert getArguments() != null
        String id = requireArguments().getString("id");

        return view;
    }
}
```

Se instancia siempre con `Bundle` + `setArguments()`, y se inserta con
`controladorUtilidades.reemplazarFragments(contenedorId, fragmentManager, fragment)`.

---

## Lista nueva — Adaptador + item

1. Layout del item: `lista_item_<entidad>.xml`, normalmente un `CardView`.
2. Adaptador en `Adaptadores/`, nombre `Lista<Entidad>Adaptador`.
3. Inflar con el `parent`, para que respete los `LayoutParams`:
   ```java
   LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_x, parent, false);
   ```
4. El listener de cada fila se registra **en el ViewHolder**, usando
   `getAbsoluteAdapterPosition()` (nunca la variable `position` de `onBindViewHolder`).

### No consultar Firestore dentro de `onBindViewHolder`

`onBindViewHolder` se ejecuta **cada vez que una fila entra en pantalla**. Consultar ahí
significa pagar lecturas repetidas al hacer scroll, respuestas que llegan desordenadas y
datos cruzados en vistas recicladas.

**Preferir datos desnormalizados en el modelo.** `Seguimiento` guarda `nombreAdoptante`,
`nombreMascota` y `nombreVoluntario` justo para esto.

Cuando el dato no está en el modelo (por ejemplo fotos de perfil) y el conjunto es acotado,
**consultarlo una sola vez y pasárselo al adaptador**:

```java
// En el controlador: una consulta para todo el chat
controladorSeguimiento.obtenerFotosPerfilChat(seguimiento, callback);

// En el adaptador: se pinta desde memoria
String url = fotosPerfil.get(mensaje.getIdEmisor());
```

**Siempre poner el `else` del fallback.** Sin él, el reciclado deja la imagen del ítem
anterior en la vista reutilizada:

```java
if (url != null && !url.isEmpty()) {
    controladorUtilidades.insertarImagenDesdeBDD(url, holder.iv, contexto);
} else {
    holder.iv.setImageResource(R.drawable.logo_patitas_valientes);
}
```

---

## Datos

### Firestore

Colecciones: `Cuentas`, `Usuarios`, `Mascotas`, `Adopciones`, `Domicilios`, `Seguimientos`,
`NotificacionesProgramadas`, `ContratosAdopciones`.

- El id del documento de `Cuentas` **es igual al UID de Firebase Auth**.
  Para el usuario actual: `FirebaseAuth.getInstance().getCurrentUser().getUid()`.
- Ojo: **los nombres de campo en Firestore no siempre coinciden con los del modelo**
  (`nombre` → `nombreMascota`, `estado` → `estadoMascota`). Verificar antes de escribir un
  `getString(...)`; un nombre mal escrito compila y devuelve `null` en silencio.

### Realtime Database

Solo para los chats: `chats/{listaMensajes}/{mensajeId}`, escuchado con `ChildEventListener`.
`listaMensajes` es un UUID que se genera al crear el seguimiento.

### Modelos

Todos `Parcelable`, con constructor vacío, constructor completo, `CREATOR` y getters/setters.
Se pasan entre pantallas con `putExtra` / `getParcelableExtra`.

### Roles

Se comparan **siempre** con `equalsIgnoreCase`: `"Administrador"`, `"Voluntario"`, `"Adoptante"`.
El rol de la sesión se lee con `new SessionManager(this).getUserRole()`.

**Toda ramificación por rol necesita su `else`.** Un `if` suelto deja la pantalla en blanco
sin explicación para los demás roles:

```java
if (rol.equalsIgnoreCase("Voluntario")) {
    // ...
} else if (rol.equalsIgnoreCase("Adoptante")) {
    // ...
} else {
    Toast.makeText(this, "Su rol no tiene acceso a esta sección", Toast.LENGTH_SHORT).show();
    finish();
}
```

### Funcionalidad con dos lados (voluntario / adoptante)

Cuando una función existe para ambos roles, el emisor/receptor **no se puede fijar**.
Se parametriza:

```java
String nombreEmisor = enviaVoluntario ? seguimiento.getNombreVoluntario()
                                      : seguimiento.getNombreAdoptante();
String idEmisor = enviaVoluntario ? seguimiento.getIdVoluntario()
                                  : seguimiento.getIdAdoptante();
```

---

## Nombres

| Elemento | Convención | Ejemplo |
|---|---|---|
| Activity | `<Función>Activity` | `SeguimientoAdoptanteChatActivity` |
| Layout de Activity | `activity_<snake_case>` | `activity_seguimiento_adoptante_chat.xml` |
| Fragment | `<Función>Fragment` | `MisSeguimientosAdoptanteFragment` |
| Layout de Fragment | `fragment_<snake_case>` | `fragment_mis_seguimientos_adoptante.xml` |
| Adaptador | `Lista<Entidad>Adaptador` | `ListaSeguimientosAdoptanteAdaptador` |
| Layout de item | `lista_item_<entidad>` | `lista_item_seguimiento_adoptante.xml` |
| Controlador | `Controlador<Dominio>` | `ControladorSeguimiento` |

**IDs de vistas:** prefijo por tipo en camelCase — `tV` TextView, `iV` ImageView,
`lL` LinearLayout, `rL` RelativeLayout, `eT` EditText, `cIV` CircleImageView,
`fL` FrameLayout, `recyView` / `recyclerView` RecyclerView.

El id de la raíz del layout es `main` (es lo que espera el listener de insets).

**Código y comentarios en español.** Los nombres de métodos también (`enviarMensajeTexto`,
`desplazarAlUltimoMensaje`, `verificarPermisoCamara`).

---

## Multimedia

- **Imágenes: Glide**, vía `controladorUtilidades.insertarImagenDesdeBDD(url, imageView, context)`.
  No usar Picasso en código nuevo.
- **Video en listas:** Media3/ExoPlayer. Liberar siempre los players en `onPause()` y
  `onDestroy()` con `adaptador.releaseAllPlayers()`, y en `onViewRecycled()` del adaptador.
- **Subidas:** Cloudinary vía `MediaManager`, siempre desde un controlador.
- **Cámara y galería:** `ActivityResultLauncher` (no `startActivityForResult`), con
  verificación de permisos previa. Para galería, el permiso cambia según la versión:
  `READ_MEDIA_IMAGES` / `READ_MEDIA_VIDEO` desde API 33, `READ_EXTERNAL_STORAGE` antes.
- **Archivos de cámara:** `FileProvider` con la autoridad `com.example.tic_pv.fileprovider`.

---

## Checklist antes de dar por terminada una funcionalidad

- [ ] ¿La Activity está registrada en `AndroidManifest.xml`?
- [ ] ¿Tiene `windowSoftInputMode="adjustResize"` si hay campos de texto?
- [ ] ¿Los insets incluyen `Type.ime()` con `Math.max`?
- [ ] ¿Las listas se desplazan al último elemento cuando sube el teclado?
- [ ] ¿Un solo `setContentView`, con el binding?
- [ ] ¿Los extras y argumentos se validan sin `assert`?
- [ ] ¿La flecha de regresar tiene listener?
- [ ] ¿Las ramificaciones por rol tienen `else`?
- [ ] ¿El acceso a Firestore está en un controlador, y fuera de `onBindViewHolder`?
- [ ] ¿Las imágenes tienen fallback para el reciclado?
- [ ] ¿Se liberan los players de video?
- [ ] ¿Compila? `./gradlew compileDebugJavaWithJavac` con el `JAVA_HOME` correcto

---

## Cosas que NO se deben agregar

- **Credenciales en el código.** Cloudinary ya tiene el `api_secret` expuesto en
  `MainActivity.java` y en el manifest; está pendiente de rotar (ver MEJORAS.md 1.1).
  No agregar más secretos: usar unsigned upload presets.
- **Código comentado.** Git guarda el historial.
- **Nuevas dependencias** que dupliquen algo que ya existe (ya hay dos librerías de imágenes,
  y sobra una).
