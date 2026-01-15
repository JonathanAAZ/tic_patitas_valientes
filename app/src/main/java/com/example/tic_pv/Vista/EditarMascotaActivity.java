package com.example.tic_pv.Vista;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.tic_pv.Controlador.ControladorDomicilio;
import com.example.tic_pv.Controlador.ControladorMascota;
import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.Modelo.Mascota;
import com.example.tic_pv.R;
import com.example.tic_pv.Vista.Fragments.AgregarFotoMascotaFragment;
import com.example.tic_pv.databinding.ActivityEditarMascotaBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;

public class EditarMascotaActivity extends AppCompatActivity {

    private ActivityEditarMascotaBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String idMascota;
    private ArrayAdapter <CharSequence> edadAdaptadorEditar, sexoAdaptadorEditar, especieAdaptadorEditar;
    private ArrayAdapter <String> domicilioAdaptadorEditar;
    private String nombreMascota, fotoMascotaFirebase, especieMascota, edadMascota, sexoMascota, colorMascota, razaMascota, caracterMascota, fechaEsterilizacion, domicilioMascota, nombreVoluntario;
    private int idEsterilizacionSeleccionada, idDomicilioMascotaSeleccionado;
    private RadioButton opcionEsterilizacionSeleccionada, opcionDomicilioSeleccionado;
    private boolean vacunacion, desparasitacion, esterilizacion, mascotaAdoptada;
    private int posicionEspecie, posicionEdad, posicionSexo, posicionDomicilio;
    private RadioButton esterilizacionSeleccionada, hogarSeleccionado;
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private ControladorDomicilio controladorDomicilio = new ControladorDomicilio();
    private ControladorMascota controladorMascota = new ControladorMascota();
    private EstadosCuentas estadoObj;
    private Uri uriFotoMascota;
    private File archivoFotoMascota;
    private Mascota mascota;
    private static final int REQUEST_CAMARA_PERMISSION = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_mascota);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
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
                    ScrollView scrollView = findViewById(R.id.scrollEditarMascota);
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
        idMascota = i.getStringExtra("id");


        binding = ActivityEditarMascotaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adaptarVista();

        limpiarRadioGroups();

        configurarRadioButtons();

//        mostrarInformacionMascota(idMascota);

        binding.lLBarraProgresoEditarMascota.setVisibility(View.GONE);

        binding.btnTomarFotoMascotaEditar.setOnClickListener(v -> {
            ocultarTeclado();
            verificarPermisoCamara();
        });
        binding.btnSeleccionarFotoMascotaEditar.setOnClickListener(v -> {
            ocultarTeclado();
            verificarPermisoGaleria();
        });
        binding.btnGuardarCambiosMascota.setOnClickListener(v -> {
            ocultarTeclado();
            binding.lLBarraProgresoEditarMascota.setVisibility(View.VISIBLE);
            guardarInformacionMascota();
        });

    }

    private void mostrarInformacionMascota(String idMascota) {

//        binding.barraProgresoEditarMascota.setVisibility(View.VISIBLE);

        db.collection("Mascotas").document(idMascota).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {

                    fotoMascotaFirebase = documentSnapshot.getString("fotoMascota");
                    nombreMascota = documentSnapshot.getString("nombre");
                    especieMascota = documentSnapshot.getString("especie");
                    edadMascota = documentSnapshot.getString("edad");
                    sexoMascota = documentSnapshot.getString("sexo");
                    colorMascota = documentSnapshot.getString("color");
                    razaMascota = documentSnapshot.getString("raza");
                    caracterMascota = documentSnapshot.getString("caracter");
                    fechaEsterilizacion = documentSnapshot.getString("fechaEsterilizacion");
                    domicilioMascota = documentSnapshot.getString("domicilioMascota");
                    mascotaAdoptada = documentSnapshot.getBoolean("mascotaAdoptada");
                    vacunacion = documentSnapshot.getBoolean("vacunacionMascota");
                    desparasitacion = documentSnapshot.getBoolean("desparasitacionMascota");
                    esterilizacion = documentSnapshot.getBoolean("esterilizacionMascota");

//                    Glide.with(this)
//                            .load(fotoMascotaFirebase)
//                            .fitCenter()
//                            .into(binding.iVFotoMascotaEditar);

                    controladorUtilidades.insertarImagenDesdeBDD(fotoMascotaFirebase,
                            binding.iVFotoMascotaEditar,
                            this);

                    binding.eTEditarNombreMascota.setText(nombreMascota);
                    binding.eTEditarColorMascota.setText(colorMascota);
                    binding.eTEditarRazaMascota.setText(razaMascota);
                    binding.eTEditarCaracterMascota.setText(caracterMascota);


                    //Obtener el nombre del voluntario del hogar temporal

                    if (!domicilioMascota.equals("I4KmbuJjjVyy4mrJqzb3")) {
                        hogarSeleccionado = binding.rBOpcionHogarTemporalEditar;
                        hogarSeleccionado.setChecked(true);
                        controladorDomicilio.obtenerNombreUsuario(domicilioMascota, new ControladorDomicilio.OnNombreUsuarioObtenidoListener() {
                            @Override
                            public void onNombreObtenido(String nombre) {
                                if (nombre != null) {
                                    nombreVoluntario = nombre;
//                                    Toast.makeText(EditarMascotaActivity.this, "IMPRIMIR NOMBRE " + nombre, Toast.LENGTH_SHORT).show();

                                    //Cargar el spinner y colocar por defecto el voluntario guardado en la base de  datos.
                                    controladorDomicilio.cargarSpinnerVoluntariosEditar(binding.spHogarTemporalEditar, binding.tVEditarHogarMascota, new ControladorDomicilio.OnAdapterReadyListener() {
                                        @Override
                                        public void onAdapterReady(ArrayAdapter<String> adaptador) {
                                            posicionDomicilio = adaptador.getPosition(nombreVoluntario);
                                            if (posicionDomicilio != -1) {
                                                binding.spHogarTemporalEditar.setSelection(posicionDomicilio);
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(EditarMascotaActivity.this, "No se ha encontrado el voluntario", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    } else {
                        hogarSeleccionado = binding.rBOpcionRefugioEditar;
                        hogarSeleccionado.setChecked(true);
                        controladorDomicilio.cargarVoluntariosSpinner(binding.spHogarTemporalEditar, binding.tVEditarHogarMascota);
                    }

                    //Seleccionar los datos guardados
                    posicionEspecie = especieAdaptadorEditar.getPosition(especieMascota);
                    posicionEdad = edadAdaptadorEditar.getPosition(edadMascota);
                    posicionSexo = sexoAdaptadorEditar.getPosition(sexoMascota);

                    if (posicionEspecie != -1) {
                        binding.spEspecieMascotaEditar.setSelection(posicionEspecie);
                    }
                    if (posicionEdad != -1) {
                        binding.spEdadMascotaEditar.setSelection(posicionEdad);
                    }
                    if (posicionSexo != -1) {
                        binding.spSexoMascotaEditar.setSelection(posicionSexo);
                    }

                    //Obtener la fecha desde la BDD y colocarla en el Date Picker
//                    if (esterilizacion) {
//                        esterilizacionSeleccionada = binding.rBOpcionEsterilizadoEditar;
//
//                        if (!fechaEsterilizacion.equals("00/00/0000")) {
//                            binding.eTEditarFechaEsterilizacionMascota.setText(fechaEsterilizacion);
//                            controladorUtilidades.colocarFechaEnDatePicker(binding.eTEditarFechaEsterilizacionMascota, fechaEsterilizacion, this);
//                        } else {
//                            controladorUtilidades.colocarDatePicker(binding.eTEditarFechaEsterilizacionMascota, this);
//                        }
//                    } else {
//                        esterilizacionSeleccionada = binding.rBOpcionNoEsterilizadoEditar;
//                        controladorUtilidades.colocarDatePicker(binding.eTEditarFechaEsterilizacionMascota, this);
//                    }
                    esterilizacionSeleccionada = esterilizacion
                            ? binding.rBOpcionEsterilizadoEditar
                            : binding.rBOpcionNoEsterilizadoEditar;

                    if (esterilizacion) {
                        binding.eTEditarFechaEsterilizacionMascota.setText(fechaEsterilizacion);
                        controladorUtilidades.colocarFechaEnDatePicker(binding.eTEditarFechaEsterilizacionMascota, fechaEsterilizacion, this);
                    } else {
                        controladorUtilidades.colocarDatePicker(binding.eTEditarFechaEsterilizacionMascota, this);
                    }

                    esterilizacionSeleccionada.setChecked(true);

                }
                //Quitar barra de progreso
//                binding.barraProgresoEditarMascota.setVisibility(View.GONE);
            }
        });
    }

    private void adaptarVista () {
        //Deshabilitar el spinner de hogar por defecto
        binding.tVEditarHogarMascota.setTextColor(Color.GRAY);
        binding.spHogarTemporalEditar.setEnabled(false);
        binding.spHogarTemporalEditar.setAlpha(0.5f);

        //Colocar el DatePicker en el Edit Text
//        controladorUtilidades.colocarDatePicker(binding.eTEditarFechaEsterilizacionMascota, this);

//        binding.eTEditarFechaEsterilizacionMascota.setText("PRUEBA");

        //Deshabilitar el Date Picker por defecto
        binding.tVFechaEsterilizacionMascotaEditar.setTextColor(Color.GRAY);
        binding.eTEditarFechaEsterilizacionMascota.setEnabled(false);
        binding.eTEditarFechaEsterilizacionMascota.setAlpha(0.5f);

        //Llenar los spinner con los datos
        edadAdaptadorEditar = ArrayAdapter.createFromResource(this, R.array.array_edad_mascota, R.layout.spinner_layout);
        sexoAdaptadorEditar = ArrayAdapter.createFromResource(this, R.array.array_genero_mascota, R.layout.spinner_layout);
        especieAdaptadorEditar = ArrayAdapter.createFromResource(this, R.array.array_especie_mascota, R.layout.spinner_layout);

        //Especificar el texto que aparece en el spinner antes de ser seleccionado
        edadAdaptadorEditar.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item);
        sexoAdaptadorEditar.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item);
        especieAdaptadorEditar.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
//        controladorDomicilio.cargarVoluntariosSpinner(binding.spHogarTemporalEditar);
//        domicilioAdaptadorEditar = controladorDomicilio.obtenerAdaptador();

        //Colocar el adaptador dentro del spinner para rellenarlo
        binding.spEdadMascotaEditar.setAdapter(edadAdaptadorEditar);
        binding.spSexoMascotaEditar.setAdapter(sexoAdaptadorEditar);
        binding.spEspecieMascotaEditar.setAdapter(especieAdaptadorEditar);

        //Limpiar los errores de los TextViews relacionados con los spinners
        controladorUtilidades.limpiarErroresSpinners(binding.spEdadMascotaEditar, binding.tVEdadMascotaEditar);
        controladorUtilidades.limpiarErroresSpinners(binding.spSexoMascotaEditar, binding.tVSexoMascotaEditar);
        controladorUtilidades.limpiarErroresSpinners(binding.spEspecieMascotaEditar, binding.tVEspecieMascotaEditar);

        //Limpiar los errores de los TextView relacionados con los Radio Groups
//        controladorUtilidades.limpiarErroresRadioGroup(binding.rGVacunacion, binding.tVVacunacionMascota);
        controladorUtilidades.limpiarErroresRadioGroup(binding.rGEsterilizacionEditar, binding.tVEsterilizacionMascotaEditar);
//        controladorUtilidades.limpiarErroresRadioGroup(binding.rGDesparasitacion, binding.tVDesparasitacionMascota);
        controladorUtilidades.limpiarErroresRadioGroup(binding.rGDomicilioMascotaEditar, binding.tVDomicilioMascotaEditar);

        //Llenar con la información obtenida de la base de datos
        mostrarInformacionMascota(idMascota);
    }

    private void guardarInformacionMascota() {
//        int idVacunacionSeleccionada = binding.rGVacunacion.getCheckedRadioButtonId();
        idEsterilizacionSeleccionada = binding.rGEsterilizacionEditar.getCheckedRadioButtonId();
//        int idDesparasitacionSeleccionada = binding.rGDesparasitacion.getCheckedRadioButtonId();
        idDomicilioMascotaSeleccionado = binding.rGDomicilioMascotaEditar.getCheckedRadioButtonId();

        //Obtener los Radio Button con las opciones seleccionadas
//        RadioButton opcionVacunacionSeleccionada = view.findViewById(idVacunacionSeleccionada);
        opcionEsterilizacionSeleccionada = findViewById(idEsterilizacionSeleccionada);
//        RadioButton opcionDesparasitacionSeleccionada = view.findViewById(idDesparasitacionSeleccionada);
        opcionDomicilioSeleccionado = findViewById(idDomicilioMascotaSeleccionado);

        String nombreMascota = binding.eTEditarNombreMascota.getText().toString();
        String fotoMascota = fotoMascotaFirebase;
        String especieMascota = binding.spEspecieMascotaEditar.getSelectedItem().toString();
        String razaMascota = binding.eTEditarRazaMascota.getText().toString();
        String edadMascota = binding.spEdadMascotaEditar.getSelectedItem().toString();
        String sexoMascota = binding.spSexoMascotaEditar.getSelectedItem().toString();
        String colorMascota = binding.eTEditarColorMascota.getText().toString();
        String caracterMascota = binding.eTEditarCaracterMascota.getText().toString();
        String domicilio = "";
//        String nombreVoluntarioDomicEditar = binding.spHogarTemporalEditar.getSelectedItem().toString();
        String fechaEsterilizacion = "00/00/0000";
//        Boolean mascotaAdoptada = false;
//        Boolean vacunacionMascota = false;
        Boolean esterilizacionMascota = false;
//        Boolean desparasitacionMascota = false;
        String estadoMascota = estadoObj.ACTIVO.toString();

        //Valores por defecto de los spinners
//        String [] arrayEdad = getResources().getStringArray(R.array.array_edad_mascota);
//        String edadMascotaDefecto = arrayEdad[0];
//
//        String [] arrayGenero = getResources().getStringArray(R.array.array_genero_mascota);
//        String generoMascotaDefecto = arrayGenero[0];
//
//        String [] arrayEspecie = getResources().getStringArray(R.array.array_especie_mascota);
//        String especieMascotaDefecto = arrayEspecie[0];

        boolean formularioValido = validarFormulario();

        if (formularioValido) {
//            if (opcionEsterilizacionSeleccionada.getText().equals(binding.rBOpcionEsterilizadoEditar.getText())) {
//                esterilizacionMascota = true;
//                fechaEsterilizacion = binding.eTEditarFechaEsterilizacionMascota.getText().toString();
//            } else {
//                esterilizacionMascota = false;
//            }
            if (opcionEsterilizacionSeleccionada.getText().equals(binding.rBOpcionEsterilizadoEditar.getText())) {
                esterilizacionMascota = true;
                if (!controladorUtilidades.validarTextoVacio(binding.eTEditarFechaEsterilizacionMascota, "Seleccione una fecha de esterilización")) {
                    return;
                } else {
                    fechaEsterilizacion = binding.eTEditarFechaEsterilizacionMascota.getText().toString();
                }
            } else {
                esterilizacionMascota = false;
            }

            if (opcionDomicilioSeleccionado.getText().equals(binding.rBOpcionRefugioEditar.getText())) {
                domicilio = "I4KmbuJjjVyy4mrJqzb3";
            } else {
                String nombreVoluntarioDomicilio = binding.spHogarTemporalEditar.getSelectedItem().toString();
                if (nombreVoluntarioDomicilio.equals("--- Seleccione un voluntario ---")){
                    binding.tVDomicilioMascotaEditar.setError("Por favor seleccione un voluntario");
                    return;
                } else {
                    domicilio = controladorDomicilio.obtenerDomicilioSeleccionado();
                    if (domicilio == null) {
                        domicilio = domicilioMascota;
                    }
                }
            }
            // Determinar el domicilio de la mascota
//            domicilio = opcionDomicilioSeleccionado.getText().equals(binding.rBOpcionRefugioEditar.getText())
//                    ? "I4KmbuJjjVyy4mrJqzb3"
//                    : controladorDomicilio.obtenerDomicilioSeleccionado();


//            Toast.makeText(this, "ESTE ES EL DOMICILIO: " + domicilioMascota, Toast.LENGTH_SHORT).show();

            Mascota mascota = new Mascota(estadoMascota, fotoMascota, nombreMascota,
                    especieMascota, razaMascota, edadMascota, sexoMascota, colorMascota,
                    caracterMascota, domicilio, fechaEsterilizacion, mascotaAdoptada,
                    vacunacion, esterilizacionMascota, desparasitacion);
            mascota.setId(idMascota);

            controladorMascota.editarInformacionMascota(uriFotoMascota, mascota, this, binding.lLBarraProgresoEditarMascota);
//            controladorMascota.crearMascotaConFoto(uriFotoMascota, mascota, this);
//            Bundle bundle = new Bundle();
//            bundle.putParcelable("mascotaTemporal", mascota);
//                controladorMascota.crearMascota(this.getContext(), mascota, this.getParentFragmentManager());

//            AgregarFotoMascotaFragment fragment = new AgregarFotoMascotaFragment();
//            fragment.setArguments(bundle);
        } else {
            Toast.makeText(this, "Existen campos vacíos", Toast.LENGTH_SHORT).show();
        }

//        if (TextUtils.isEmpty(nombreMascota)) {
//            Toast.makeText(this, "Por favor ingrese el nombre de la mascota", Toast.LENGTH_LONG).show();
//            binding.eTEditarNombreMascota.setError("El nombre de la mascota es obligatorio");
//            binding.eTEditarNombreMascota.requestFocus();
//        } else if (especieMascota.equals(especieMascotaDefecto)) {
//            Toast.makeText(this, "Por favor seleccione la especie de la mascota", Toast.LENGTH_LONG).show();
//            binding.tVEspecieMascotaEditar.setError("La especie de la mascota es obligatoria");
//            binding.tVEspecieMascotaEditar.requestFocus();
//        } else if (TextUtils.isEmpty(razaMascota)) {
//            Toast.makeText(this, "Por favor seleccione la raza de la mascota", Toast.LENGTH_LONG).show();
//            binding.tVRazaMascotaEditar.setError("La raza de la mascota es obligatoria");
//            binding.tVRazaMascotaEditar.requestFocus();
//        }else if (edadMascota.equals(edadMascotaDefecto)) {
//            Toast.makeText(this, "Por favor seleccione la edad de la mascota", Toast.LENGTH_LONG).show();
//            binding.tVEdadMascotaEditar.setError("La edad de la mascota es obligatoria");
//            binding.tVEdadMascotaEditar.requestFocus();
//        } else if (sexoMascota.equals(generoMascotaDefecto)) {
//            Toast.makeText(this, "Por favor seleccione el género de la mascota", Toast.LENGTH_SHORT).show();
//            binding.tVSexoMascotaEditar.setError("El género de la mascota es obligatorio");
//            binding.tVSexoMascotaEditar.requestFocus();
//        } else if (TextUtils.isEmpty(colorMascota)) {
//            Toast.makeText(this, "Por favor ingrese el color de la mascota", Toast.LENGTH_SHORT).show();
//            binding.tVColorMascotaEditar.setError("El color de la mascota es obligatorio");
//            binding.tVColorMascotaEditar.requestFocus();
//        } else if (TextUtils.isEmpty(caracterMascota)) {
//            Toast.makeText(this, "Por favor ingrese el caracter que posee la mascota", Toast.LENGTH_SHORT).show();
//            binding.tvCaracterMascotaEditar.setError("El carácter de la mascota es obligatorio");
//            binding.tvCaracterMascotaEditar.requestFocus();
//        } else if (idEsterilizacionSeleccionada == -1) {
//            Toast.makeText(this, "Por favor seleccione una respuesta", Toast.LENGTH_SHORT).show();
//            binding.tVEsterilizacionMascotaEditar.setError("Debe seleccionar una respuesta");
//            binding.tVEsterilizacionMascotaEditar.requestFocus();
//        } else if (idDomicilioMascotaSeleccionado == -1){
//            Toast.makeText(this, "Por favor seleccione el domicilio de la mascota", Toast.LENGTH_SHORT).show();
//            binding.tVDomicilioMascotaEditar.setError("Debe seleccionar un domicilio");
//            binding.tVDomicilioMascotaEditar.requestFocus();
//        } else {
//            // Determinar los estados de vacunación, desparasitación y esterilización
////            vacunacionMascota = opcionVacunacionSeleccionada.getText().equals(binding.rBOpcionVacunado.getText());
////            desparasitacionMascota = opcionDesparasitacionSeleccionada.getText().equals(binding.rBOpcionDesparasitado.getText());
//
//
//        }
    }

    private boolean validarFormulario() {
        boolean valido = true;

        //Valores por defecto de los spinners
        String [] arrayEdad = getResources().getStringArray(R.array.array_edad_mascota);
        String edadMascotaDefecto = arrayEdad[0];

        String [] arrayGenero = getResources().getStringArray(R.array.array_genero_mascota);
        String sexoMascotaDefecto = arrayGenero[0];

        String [] arrayEspecie = getResources().getStringArray(R.array.array_especie_mascota);
        String especieMascotaDefecto = arrayEspecie[0];

        if (!controladorUtilidades.validarTextoVacio(binding.eTEditarNombreMascota, "Ingrese el nombre de la mascota")) valido = false;
        if (edadMascota.equals(edadMascotaDefecto)) {
            binding.tVEdadMascotaEditar.setError("Seleccione la edad de la mascota");
            valido = false;
        }
        if (especieMascota.equals(especieMascotaDefecto)) {
            binding.tVEspecieMascotaEditar.setError("Seleccione la especie de la mascota");
            valido = false;
        }
        if (sexoMascota.equals(sexoMascotaDefecto)) {
            binding.tVSexoMascotaEditar.setError("Seleccione el sexo de la mascota");
            valido = false;
        }
        if (!controladorUtilidades.validarTextoVacio(binding.eTEditarColorMascota, "Ingrese el color de la mascota")) valido = false;
        if (!controladorUtilidades.validarTextoVacio(binding.eTEditarRazaMascota, "Ingrese la raza de la mascota")) valido = false;
        if (!controladorUtilidades.validarTextoVacio(binding.eTEditarCaracterMascota, "Ingrese el caracter de la mascota")) valido = false;
        if (controladorUtilidades.validarSeleccionRadioGroups(idEsterilizacionSeleccionada, "Debe seleccionar una respuesta", binding.tVEdadMascotaEditar))valido = false;
        if (controladorUtilidades.validarSeleccionRadioGroups(idDomicilioMascotaSeleccionado, "Debe seleccionar una opción de domicilio", binding.tVDomicilioMascotaEditar)) valido = false;

        return valido;
    }

    private void limpiarRadioGroups () {
//        binding.rGVacunacion.clearCheck();
        binding.rGEsterilizacionEditar.clearCheck();
//        binding.rGDesparasitacion.clearCheck();
        binding.rGDomicilioMascotaEditar.clearCheck();
    }

    private void configurarRadioButtons() {
        //Detectar los cambios en el estado del Radio Button
        RadioButton rBHogarTemporal = binding.rBOpcionHogarTemporalEditar;
        rBHogarTemporal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.tVEditarHogarMascota.setTextColor(Color.BLACK);
                binding.spHogarTemporalEditar.setEnabled(true); // Habilitar Spinner
                binding.spHogarTemporalEditar.setAlpha(1f); // Restaurar opacidad
            } else {
//                controladorUtilidades.colocarDatePicker(binding.eTEditarFechaEsterilizacionMascota, this);
                binding.tVEditarHogarMascota.setTextColor(Color.GRAY);
                binding.spHogarTemporalEditar.setEnabled(false); // Deshabilitar Spinner
                binding.spHogarTemporalEditar.setAlpha(0.5f); // Reducir opacidad
            }
        });

        //Detectar los cambios en el estado del Radio Button de Esterilizacion
        binding.rBOpcionEsterilizadoEditar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.tVFechaEsterilizacionMascotaEditar.setTextColor(Color.BLACK);
                binding.eTEditarFechaEsterilizacionMascota.setEnabled(true); // Habilitar Spinner
                binding.eTEditarFechaEsterilizacionMascota.setAlpha(1f); // Restaurar opacidad
            } else {
                binding.tVFechaEsterilizacionMascotaEditar.setTextColor(Color.BLACK);
                binding.eTEditarFechaEsterilizacionMascota.setEnabled(false); // Deshabilitar Spinner
                binding.eTEditarFechaEsterilizacionMascota.setAlpha(0.5f); // Reducir opacidad
            }
        });
    }

    private void verificarPermisoCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            solicitarPermisosCamaraLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void abrirCamara() {
        // Crear un archivo temporal para guardar la foto
        archivoFotoMascota = new File(this.getCacheDir(), "foto_" + System.currentTimeMillis() + ".jpg");
        uriFotoMascota = FileProvider.getUriForFile(this, "com.example.tic_pv.fileprovider", archivoFotoMascota);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFotoMascota);  // Guardar en archivo
        System.out.println("DATOS DE MASCOTA" + mascota);
        seleccionarFotoLauncher.launch(intent);
    }

    private final ActivityResultLauncher<String> solicitarPermisosCamaraLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            abrirCamara();
        } else {
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_LONG).show();
        }
    });

    private final ActivityResultLauncher<Intent> seleccionarFotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Mostrar la foto en alta calidad
//                    binding.ivAgregarFotoMascota.setImageURI(photoUri);

                    //Limpiar la imagen anterior
                    binding.iVFotoMascotaEditar.setImageDrawable(null);
//                    binding.ivAgregarFotoMascota.setImageURI(uriFotoMascota);

                    Glide.with(this)
                            .load(uriFotoMascota)
                            .fitCenter()  // Ajustar la imagen al ImageView
                            .into(binding.iVFotoMascotaEditar);
                }
            }
    );

    private void verificarPermisoGaleria() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria();
            } else {
                solicitarPermisosGaleriaLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria();
            } else {
                solicitarPermisosGaleriaLauncher.launch(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria();
            } else {
                solicitarPermisosGaleriaLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        seleccionarFotoGaleriaLauncher.launch(intent);
    }

    private final ActivityResultLauncher <Intent> seleccionarFotoGaleriaLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    //Obtener la URI de la foto seleccionada
                    uriFotoMascota = result.getData().getData();

//                    binding.ivAgregarFotoMascota.setImageURI(uriFotoMascota);
                    //Mostrar la imagen seleccionada en el ImageView
                    Glide.with(this)
                            .load(uriFotoMascota)
                            .fitCenter()
                            .into(binding.iVFotoMascotaEditar);
                }
            }
    );

    private final ActivityResultLauncher <String> solicitarPermisosGaleriaLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    abrirGaleria();
                } else {
                    Toast.makeText(this, "Permiso para acceder a la galería denegado", Toast.LENGTH_LONG).show();
                }
            }
    );

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