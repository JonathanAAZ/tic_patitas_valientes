package com.example.tic_pv.Vista;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.Fragments.PerfilFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class EditarDomicilioActivity extends AppCompatActivity {

    private EditText etPaisEditar, etParroquiaEditar, etBarrioEditar, etCallesEditar;
    private TextView tvEditarProvincia, tvEditarCanton;
    private Spinner spEditarProvincia, spEditarCanton;
    private String paisDomicilio, provinciaDomicilio, cantonDomicilio,
            parroquiaDomicilio, barrioDomicilio, callesDomicilio, domicilioID;
    private String idCuenta, rol, provinciaSeleccionada, cantonSeleccionado;
    private ArrayAdapter <CharSequence> provinciaEditarAdaptador, cantonEditarAdaptador;
    private FirebaseAuth authCuenta;
    private ProgressBar barraProgreso;
    private boolean procesando, enAdministracion;
    private FirebaseFirestore dbUsuarios = FirebaseFirestore.getInstance();
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_domicilio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Ajustar el padding del contenedor principal
            int bottomPadding = imeInsets.bottom > 0 ? imeInsets.bottom : systemBars.bottom;
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding);

            // Verificar si hay un EditText enfocado
            View focusedView = getCurrentFocus();
            if (focusedView instanceof EditText && imeInsets.bottom > 0) {
                int[] location = new int[2];
                focusedView.getLocationInWindow(location); // Cambiar a `getLocationInWindow` para mayor precisión

                // Altura disponible para el contenido visible (pantalla - teclado)
                int availableHeight = getResources().getDisplayMetrics().heightPixels - imeInsets.bottom;

                // Asegurar que el campo no sea cubierto por el teclado
                int focusedViewBottom = location[1] + focusedView.getHeight();
                int scrollDistance = Math.max(0, focusedViewBottom - availableHeight); // Evitar valores negativos

                if (scrollDistance > 0) {
                    // Limitar el desplazamiento para evitar que el EditText desaparezca en la parte superior
                    ScrollView scrollView = findViewById(R.id.scrollEditarDomicilio);
                    if (scrollView != null) {
                        int maxScroll = scrollView.getChildAt(0).getHeight() - scrollView.getHeight();
                        int currentScrollY = scrollView.getScrollY();
                        int finalScroll = Math.min(currentScrollY + scrollDistance + 20, maxScroll);

                        scrollView.post(() -> scrollView.smoothScrollTo(0, finalScroll));
                    }
                }
            }

            return insets;
        });

        Intent i = getIntent();
        idCuenta = i.getStringExtra("id");
        rol = i.getStringExtra("rol");
        enAdministracion = i.getBooleanExtra("enAdministracion", false);
//        esAdmin = i.getBooleanExtra("esAdmin", false);

        barraProgreso = findViewById(R.id.barraProgresoEditarDomicilio);
        etPaisEditar = findViewById(R.id.etEditarPais);
        spEditarProvincia = findViewById(R.id.spEditarProvinciaDomicilio);
        spEditarCanton = findViewById(R.id.spEditarCantonDomicilio);
        etParroquiaEditar = findViewById(R.id.etEditarParroquia);
        etBarrioEditar = findViewById(R.id.etEditarBarrio);
        etCallesEditar = findViewById(R.id.etEditarCalles);

        //Definir los TextView para mostrar los errores de los spinner
        tvEditarProvincia = findViewById(R.id.labelEditarProvincia);
        tvEditarCanton = findViewById(R.id.labelEditarCanton);

        authCuenta = FirebaseAuth.getInstance();


        //Llenar los spinner con los datos
        provinciaEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this, R.array.array_provincias_ecuador, R.layout.spinner_layout);

        //Especificar el texto que aparece en el spinner antes de ser seleccionado
        provinciaEditarAdaptador.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

        //Colocar el adaptador dentro del spinner
        spEditarProvincia.setAdapter(provinciaEditarAdaptador);






        FirebaseUser usuarioAuth = authCuenta.getCurrentUser();

        Button btnGuardarDomicilio = findViewById(R.id.btnGuardarCambiosDomicilio);

        //Mostrar información del domicilio
        mostrarInformacionDomicilio(idCuenta);


        btnGuardarDomicilio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Verificar si ya está en proceso
                if (procesando) {
                    return; //Salir si ya está procesando
                }

                //Marcar como "Procesando"
                procesando = true;

                //Deshabilitar el botón inmediantamente
                v.setEnabled(false);
                editarDomicilio();
                v.setEnabled(true);
            }
        });
    }

    private void mostrarInformacionDomicilio(String usuario) {
//        String usuarioRegistradoID = usuario.getUid();

        barraProgreso.setVisibility(View.VISIBLE);

        dbUsuarios.collection("Usuarios").whereEqualTo("cuentaUsuario", usuario).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (!querySnapshot.isEmpty()) {
                    for (QueryDocumentSnapshot documento : querySnapshot) {
                        obtenerDomiciliosBD(documento.getString("domicilioUsuario"));
                    }
                } else {
                    Toast.makeText(this, "No se encontró el usuario", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error al obtener el usuario", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditarDomicilioActivity.this, "Algo salió mal", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void obtenerDomiciliosBD (String id) {
        dbUsuarios.collection("Domicilios").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    int posicionProvincia, posicionCanton;

                    paisDomicilio = documentSnapshot.getString("pais");
                    provinciaDomicilio = documentSnapshot.getString("provincia");
                    cantonDomicilio = documentSnapshot.getString("canton");
                    parroquiaDomicilio = documentSnapshot.getString("parroquia");
                    barrioDomicilio = documentSnapshot.getString("barrio");
                    callesDomicilio = documentSnapshot.getString("calles");
                    domicilioID = documentSnapshot.getId().toString();

                    //Seleccionar la provincia guardada
                    posicionProvincia = provinciaEditarAdaptador.getPosition(provinciaDomicilio);

                    Toast.makeText(EditarDomicilioActivity.this, "PROVINCIA BDD" + provinciaDomicilio, Toast.LENGTH_SHORT).show();
                    if (posicionProvincia != -1) {
                        spEditarProvincia.setSelection(posicionProvincia);
                    }

                    provinciaSeleccionada = spEditarProvincia.getSelectedItem().toString();

                    cargarSpinners(provinciaSeleccionada);

                    //Seleccionar el cantón si corresponde a la provincia seleccionada
                    if (provinciaSeleccionada.equals(provinciaDomicilio)) {
                        posicionCanton = cantonEditarAdaptador.getPosition(cantonDomicilio);

                        if (posicionCanton != -1) {
                            spEditarCanton.setSelection(posicionCanton);
                        }
                    }

                    spEditarProvincia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        //Boolean para que solo al momento de seleccionar una nueva provincia, se actualice el spinner del cantón
                        private boolean primeraSeleccion = true;

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            tvEditarProvincia.setError(null);

                            if (primeraSeleccion) {
                                primeraSeleccion = false;
                                return;
                            }

                            provinciaSeleccionada = spEditarProvincia.getSelectedItem().toString();

                            cargarSpinners(provinciaSeleccionada);

                            //Obtener el canton seleccionado
                            spEditarCanton.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    tvEditarCanton.setError(null);
                                    cantonSeleccionado = spEditarCanton.getSelectedItem().toString();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });


                    etPaisEditar.setText(paisDomicilio);
                    etParroquiaEditar.setText(parroquiaDomicilio);
                    etBarrioEditar.setText(barrioDomicilio);
                    etCallesEditar.setText(callesDomicilio);
                }

                barraProgreso.setVisibility(View.GONE);
            }
        });
    }

    private void editarDomicilio () {

        paisDomicilio = etPaisEditar.getText().toString();
        provinciaDomicilio = spEditarProvincia.getSelectedItem().toString();
        cantonDomicilio = spEditarCanton.getSelectedItem().toString();
        parroquiaDomicilio = etParroquiaEditar.getText().toString();
        barrioDomicilio = etBarrioEditar.getText().toString();
        callesDomicilio = etCallesEditar.getText().toString();

        boolean formularioValido = validarFormulario();

        if (formularioValido) {
            ocultarTeclado();
            tvEditarCanton.setError(null);
            tvEditarProvincia.setError(null);

            Map<String, Object> mapActualizacionesDom = new HashMap<>();
            mapActualizacionesDom.put("pais", paisDomicilio);
            mapActualizacionesDom.put("provincia", provinciaDomicilio);
            mapActualizacionesDom.put("canton", cantonDomicilio);
            mapActualizacionesDom.put("parroquia", parroquiaDomicilio);
            mapActualizacionesDom.put("barrio", barrioDomicilio);
            mapActualizacionesDom.put("calles", callesDomicilio);

            barraProgreso.setVisibility(View.VISIBLE);
            cargarInformacionDomBD(domicilioID, mapActualizacionesDom);
        } else {

            ocultarTeclado();
            barraProgreso.setVisibility(View.GONE);
            procesando = false;
            Toast.makeText(this, "Existe campos vacíos", Toast.LENGTH_SHORT).show();
        }

//        if (TextUtils.isEmpty(paisDomicilio)) {
//            Toast.makeText(EditarDomicilioActivity.this, "Ingrese un país", Toast.LENGTH_LONG).show();
//            etPaisEditar.setError("El nombre es obligatorio");
//            etPaisEditar.requestFocus();
//        } else if (contieneNumeros(paisDomicilio)) {
//            Toast.makeText(EditarDomicilioActivity.this, "No se permiten nùmeros", Toast.LENGTH_LONG).show();
//        } else if(provinciaDomicilio.equals("--- Selecciona tu provincia ---")) {
//            Toast.makeText(EditarDomicilioActivity.this, "Seleccione una provincia", Toast.LENGTH_LONG).show();
//            tvEditarProvincia.setError("La provincia es obligatoria");
//            tvEditarProvincia.requestFocus();
//        } else if (contieneNumeros(provinciaDomicilio)) {
//            Toast.makeText(EditarDomicilioActivity.this, "No se permiten números", Toast.LENGTH_LONG).show();
//        } else if (cantonDomicilio.equals("--- Selecciona tu cantón ---")) {
//            Toast.makeText(EditarDomicilioActivity.this, "Seleccione un cantón", Toast.LENGTH_LONG).show();
//            tvEditarCanton.setError("El cantón es obligatorio");
//            tvEditarCanton.requestFocus();
//        } else if (TextUtils.isEmpty(parroquiaDomicilio)) {
//            Toast.makeText(EditarDomicilioActivity.this, "Ingrese una parroquia", Toast.LENGTH_LONG).show();
//            etParroquiaEditar.setError("La parroquia es obligatoria");
//            etParroquiaEditar.requestFocus();
//        } else if (TextUtils.isEmpty(barrioDomicilio)) {
//            Toast.makeText(EditarDomicilioActivity.this, "Ingrese un barrio", Toast.LENGTH_LONG).show();
//            etBarrioEditar.setError("El barrio es obligatorio");
//            etBarrioEditar.requestFocus();
//        } else if (TextUtils.isEmpty(callesDomicilio)) {
//            Toast.makeText(EditarDomicilioActivity.this, "Ingrese las calles", Toast.LENGTH_LONG).show();
//            etCallesEditar.setError("Las calles son obligatorias");
//            etCallesEditar.requestFocus();
//        } else {
//
//
//
//
//        }
    }

    private void cargarInformacionDomBD(String idDom, Map<String, Object> mapAct) {
        procesando = false;
        dbUsuarios.collection("Domicilios").document(idDom).update(mapAct).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(EditarDomicilioActivity.this, "Información guardada correctamente", Toast.LENGTH_SHORT).show();

                if (enAdministracion) {
                    Intent i = new Intent(EditarDomicilioActivity.this, VerInformacionPerfilActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("id", idCuenta);
                    i.putExtra("rol", rol);
                    startActivity(i);
                } else {
                    Intent intent = new Intent(EditarDomicilioActivity.this, BottomNavigationMenu.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("id", R.id.bMPerfil);
                    intent.putExtra("rol", rol);
                    startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditarDomicilioActivity.this, "Error al guardar la información en la BD", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Mètodo para cargar el spinner del cantón con respecto a la provincia seleccionada
    private void cargarSpinners (String provincia) {
        switch (provincia) {
            case "--- Selecciona tu provincia ---": cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                    R.array.array_provincias_defecto, R.layout.spinner_layout);
                break;

            case "Azuay":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_azuay_cantones, R.layout.spinner_layout);
                break;

            case "Bolívar":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_bolivar_cantones, R.layout.spinner_layout);
                break;

            case "Cañar":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_canar_cantones, R.layout.spinner_layout);
                break;

            case "Carchi":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_carchi_cantones, R.layout.spinner_layout);
                break;

            case "Chimborazo":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_chimborazo_cantones, R.layout.spinner_layout);
                break;

            case "Cotopaxi":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_cotopaxi_cantones, R.layout.spinner_layout);
                break;

            case "El Oro":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_el_oro_cantones, R.layout.spinner_layout);
                break;

            case "Esmeraldas":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_esmeraldas_cantones, R.layout.spinner_layout);
                break;

            case "Galápagos":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_galapagos_cantones, R.layout.spinner_layout);
                break;

            case "Guayas":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_guayas_cantones, R.layout.spinner_layout);
                break;

            case "Imbabura":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_imbabura_cantones, R.layout.spinner_layout);
                break;

            case "Loja":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_loja_cantones, R.layout.spinner_layout);
                break;

            case "Los Ríos":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_los_rios_cantones, R.layout.spinner_layout);
                break;

            case "Manabí":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_manabi_cantones, R.layout.spinner_layout);
                break;

            case "Morona Santiago":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_morona_santiago_cantones, R.layout.spinner_layout);
                break;

            case "Napo":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_napo_cantones, R.layout.spinner_layout);
                break;

            case "Orellana":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_orellana_cantones, R.layout.spinner_layout);
                break;

            case "Pastaza":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_pastaza_cantones, R.layout.spinner_layout);
                break;

            case "Pichincha":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_pichincha_cantones, R.layout.spinner_layout);
                break;

            case "Santa Elena":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_santa_elena_cantones, R.layout.spinner_layout);
                break;

            case "Santo Domingo de los Tsáchilas":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_santo_domingo_cantones, R.layout.spinner_layout);
                break;

            case "Sucumbíos":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_sucumbios_cantones, R.layout.spinner_layout);
                break;

            case "Tungurahua":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_tungurahua_cantones, R.layout.spinner_layout);
                break;

            case "Zamora Chinchipe":
                cantonEditarAdaptador = ArrayAdapter.createFromResource(EditarDomicilioActivity.this,
                        R.array.array_zamora_chinchipe_cantones, R.layout.spinner_layout);
                break;
            default: break;
        }
        cantonEditarAdaptador.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spEditarCanton.setAdapter(cantonEditarAdaptador);
    }

    private boolean validarFormulario() {
        boolean valido = true;
        //Valores por defecto de los spinners
        String [] arrayProvincias = getResources().getStringArray(R.array.array_provincias_ecuador);
        String provinciaDefecto = arrayProvincias[0];

        String [] arrayCantones = getResources().getStringArray(R.array.array_provincias_defecto);
        String cantonDefecto = arrayCantones[0];

        if(provinciaDomicilio.equals(provinciaDefecto)) {
            tvEditarProvincia.setError("Seleccione una provincia");
            valido = false;
        }
        if (provinciaDomicilio.equals(cantonDefecto)) {
            tvEditarCanton.setError("Seleccione un cantón");
            valido = false;
        }
        if (!controladorUtilidades.validarTextoVacio(etParroquiaEditar, "Ingrese una parroquia")) valido = false;
        if (!controladorUtilidades.validarTextoVacio(etBarrioEditar, "Ingrese un barrio")) valido = false;
        if (!controladorUtilidades.validarTextoVacio(etCallesEditar, "Ingrese las calles del domicilio")) valido = false;

        return valido;
    }

    public boolean contieneNumeros(String texto) {
        return texto.matches(".*\\d.*");
    }

    private void ocultarTeclado () {
        // Obtener el servicio InputMethodManager
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // Ocultar el teclado
        if (imm != null) {
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
        }
    }
}