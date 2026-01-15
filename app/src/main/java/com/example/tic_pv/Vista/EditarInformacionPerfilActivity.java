package com.example.tic_pv.Vista;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tic_pv.Controlador.ControladorUsuario;
import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditarInformacionPerfilActivity extends AppCompatActivity {

    private EditText etNombreUsuarioEditar, etCedulaUsuarioEditar, etFechaNacUsuarioEditar,
            etOcupacionUsuarioEditar, etTelefonoUsuarioEditar, etInstaUsuarioEditar, etFbUsuarioEditar;

    private TextView tvErrorCedula, tvErrorEstadoCiv, tvErrorTelefono;
    private RadioGroup estadoCivOpsEditar;
    private RadioButton estadoCivSeleccionadoEditar;
    private DatePickerDialog pickerDialog;
    private String nombreUsuarioEditar, cedulaUsuarioEditar,
            fechaNacUsuarioEditar, estadoCivUsuarioEditar, ocupacionUsuarioEditar,
            telefonoUsuarioEditar, igUsuarioEditar, fbUsuarioEditar , idUsuarioEditado;
    private String idCuenta, rol;
    private Button btnGuardarCambiosPerfil;
    private boolean procesando, enAdministracion;
    private FirebaseAuth authPerfil;
    private ProgressBar barraProgresoEditarPerfil;
    private FirebaseFirestore dbUsuarios = FirebaseFirestore.getInstance();
    private ControladorUsuario controladorUsuario = new ControladorUsuario();
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_informacion_perfil);
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
                    ScrollView scrollView = findViewById(R.id.scrollEditarInformacionPerfil);
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

        barraProgresoEditarPerfil = findViewById(R.id.barraProgresoEditarPerfil);
        etNombreUsuarioEditar = findViewById(R.id.etEditarNombreUsuario);
        etCedulaUsuarioEditar = findViewById(R.id.etEditarCedula);
        etFechaNacUsuarioEditar = findViewById(R.id.etEditarFechaNac);
        etOcupacionUsuarioEditar = findViewById(R.id.etEditarOcupacionUsuario);
        etTelefonoUsuarioEditar = findViewById(R.id.etEditarTelefono);
        etInstaUsuarioEditar = findViewById(R.id.etEditarInstaUsuario);
        etFbUsuarioEditar = findViewById(R.id.etEditarFbUsuario);
        estadoCivOpsEditar = findViewById(R.id.rgEditarEstadoCiv);

        //Text Views para mostrar errores
        tvErrorCedula = findViewById(R.id.tVErrorCedulaEditarInfo);
        tvErrorEstadoCiv = findViewById(R.id.labelEditarEstadoCiv);
        tvErrorTelefono = findViewById(R.id.tVErrorTelefonoEditarInfo);

        authPerfil = FirebaseAuth.getInstance();

        FirebaseUser usuarioAuth = authPerfil.getCurrentUser();

        btnGuardarCambiosPerfil = findViewById(R.id.btnGuardarCambiosPerfil);


        //Verificar que se hayan realizado las validaciones correspondientes
        controladorUtilidades.setActualizarBotonCallback(() -> {
            boolean todasValidas = !controladorUtilidades.getEstadoValidaciones().containsValue(false);
            btnGuardarCambiosPerfil.setEnabled(todasValidas);
        });

        //Agregar las validaciones en tiempo real
        controladorUtilidades.agregarValidacionEnTiempoReal(etCedulaUsuarioEditar, controladorUtilidades::validadorDeCedula, tvErrorCedula, "cedula");
        controladorUtilidades.agregarValidacionEnTiempoReal(etTelefonoUsuarioEditar, controladorUtilidades::validarNumeroCelular, tvErrorTelefono, "telefono");
        //Mostrar Información del Usuario
        mostrarInformacionPerfil(idCuenta);

        btnGuardarCambiosPerfil.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
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

                editarPerfil();
            }
        });

    }

    //Obtener la información desde la BD
    private void mostrarInformacionPerfil(String cuentaID) {

        barraProgresoEditarPerfil.setVisibility(View.VISIBLE);

        dbUsuarios.collection("Usuarios").whereEqualTo("cuentaUsuario", cuentaID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (!querySnapshot.isEmpty()) {
                    for (QueryDocumentSnapshot documento : querySnapshot) {
                        nombreUsuarioEditar = documento.getString("nombre");
                        cedulaUsuarioEditar = documento.getString("cedula");
                        fechaNacUsuarioEditar = documento.getString("fechaNac");
                        estadoCivUsuarioEditar = documento.getString("estadoCiv");
                        ocupacionUsuarioEditar = documento.getString("ocupacion");
                        telefonoUsuarioEditar = documento.getString("telefono");
                        igUsuarioEditar = documento.getString("ig");
                        fbUsuarioEditar = documento.getString("fb");
                        idUsuarioEditado = documento.getId();
                    }

                    etNombreUsuarioEditar.setText(nombreUsuarioEditar);
                    etCedulaUsuarioEditar.setText(cedulaUsuarioEditar);
                    etFechaNacUsuarioEditar.setText(fechaNacUsuarioEditar);
                    etOcupacionUsuarioEditar.setText(ocupacionUsuarioEditar);
                    etTelefonoUsuarioEditar.setText(telefonoUsuarioEditar);

                    controladorUtilidades.colocarFechaEnDatePicker(etFechaNacUsuarioEditar, fechaNacUsuarioEditar, this);

                    // Configuración de Instagram
                    etInstaUsuarioEditar.setText(igUsuarioEditar.equals("Sin usuario de Instagram") ? "" : igUsuarioEditar);

                    // Configuración de Facebook
                    etFbUsuarioEditar.setText(fbUsuarioEditar.equals("Sin usuario de Facebook") ? "" : fbUsuarioEditar);

                    // Configuración del estado civil
                    int estadoCivId;
                    switch (estadoCivUsuarioEditar) {
                        case "Soltero":
                            estadoCivId = R.id.editarOpcionSoltero;
                            break;
                        case "Casado":
                            estadoCivId = R.id.editarOpcionCasado;
                            break;
                        case "Divorciado":
                            estadoCivId = R.id.editarOpcionDivorciado;
                            break;
                        case "Viudo":
                            estadoCivId = R.id.editarOpcionViudo;
                            break;
                        default:
                            estadoCivId = R.id.editarOpcionUnion;
                            break;
                    }
                    estadoCivSeleccionadoEditar = findViewById(estadoCivId);

//                    if (igUsuarioEditar.equals("Sin usuario de Instagram")) {
//                        etInstaUsuarioEditar.setText("");
//                    } else {
//                        etInstaUsuarioEditar.setText(igUsuarioEditar);
//                    }
//
//                    if(fbUsuarioEditar.equals("Sin usuario de Facebook")) {
//                        etFbUsuarioEditar.setText("");
//                    } else {
//                        etFbUsuarioEditar.setText(fbUsuarioEditar);
//                    }
//
//                    if (estadoCivUsuarioEditar.equals("Soltero")) {
//                        estadoCivSeleccionadoEditar = findViewById(R.id.editarOpcionSoltero);
//                    } else if (estadoCivUsuarioEditar.equals("Casado")) {
//                        estadoCivSeleccionadoEditar = findViewById(R.id.editarOpcionCasado);
//                    } else if (estadoCivUsuarioEditar.equals("Divorciado")) {
//                        estadoCivSeleccionadoEditar = findViewById(R.id.editarOpcionDivorciado);
//                    } else if (estadoCivUsuarioEditar.equals("Viudo")) {
//                        estadoCivSeleccionadoEditar = findViewById(R.id.editarOpcionViudo);
//                    } else {
//                        estadoCivSeleccionadoEditar = findViewById(R.id.editarOpcionUnion);
//                    }

                    estadoCivSeleccionadoEditar.setChecked(true);

                } else {
                    Toast.makeText(this, "No se encontraró el usuario", Toast.LENGTH_SHORT).show();
                }

                barraProgresoEditarPerfil.setVisibility(View.GONE);

            } else {
                Toast.makeText(this, "Error al obtener el usuario", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditarInformacionPerfilActivity.this, "Algo salió mal", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void editarPerfil() {
        barraProgresoEditarPerfil.setVisibility(View.VISIBLE);
        int estadoCivSeleccionado = estadoCivOpsEditar.getCheckedRadioButtonId();
        estadoCivSeleccionadoEditar = findViewById(estadoCivSeleccionado);

        nombreUsuarioEditar = etNombreUsuarioEditar.getText().toString();
        cedulaUsuarioEditar = etCedulaUsuarioEditar.getText().toString();
        fechaNacUsuarioEditar = etFechaNacUsuarioEditar.getText().toString();
        ocupacionUsuarioEditar = etOcupacionUsuarioEditar.getText().toString();
        telefonoUsuarioEditar = etTelefonoUsuarioEditar.getText().toString();
        igUsuarioEditar = etInstaUsuarioEditar.getText().toString();
        fbUsuarioEditar = etFbUsuarioEditar.getText().toString();

        int edad;
        boolean formularioValido = validarFormulario();

        if (formularioValido) {
            ocultarTeclado();
            edad = ControladorUsuario.calcularEdad(fechaNacUsuarioEditar);
            estadoCivUsuarioEditar = estadoCivSeleccionadoEditar.getText().toString();
            if (TextUtils.isEmpty(igUsuarioEditar)) {
                igUsuarioEditar = "Sin usuario de Instagram";
            }
            if (TextUtils.isEmpty(fbUsuarioEditar)) {
                fbUsuarioEditar = "Sin usuario de Facebook";
            }


            Map<String, Object> mapActualizaciones = new HashMap<>();
            mapActualizaciones.put("nombre", nombreUsuarioEditar);
            mapActualizaciones.put("cedula", cedulaUsuarioEditar);
            mapActualizaciones.put("edad", edad);
            mapActualizaciones.put("fechaNac", fechaNacUsuarioEditar);
            mapActualizaciones.put("estadoCiv", estadoCivUsuarioEditar);
            mapActualizaciones.put("ocupacion", ocupacionUsuarioEditar);
            mapActualizaciones.put("telefono", telefonoUsuarioEditar);
            mapActualizaciones.put("ig", igUsuarioEditar);
            mapActualizaciones.put("fb", fbUsuarioEditar);

            System.out.println("ID USUARIO EDITANDO; " + idUsuarioEditado);

            cargarInformacionBD(idUsuarioEditado, mapActualizaciones);
            barraProgresoEditarPerfil.setVisibility(View.GONE);
            btnGuardarCambiosPerfil.setEnabled(true);
        } else {
            ocultarTeclado();
            barraProgresoEditarPerfil.setVisibility(View.GONE);
            procesando = false;
            btnGuardarCambiosPerfil.setEnabled(true);
            Toast.makeText(this, "Existen campos vacíos", Toast.LENGTH_SHORT).show();
        }

//        if (TextUtils.isEmpty(nombreUsuarioEditar)) {
//            Toast.makeText(EditarInformacionPerfilActivity.this, "Por favor ingresa el nombre completo", Toast.LENGTH_LONG).show();
//            etNombreUsuarioEditar.setError("El nombre es obligatorio");
//            etNombreUsuarioEditar.requestFocus();
//        } else if (TextUtils.isEmpty(cedulaUsuarioEditar)) {
//            Toast.makeText(EditarInformacionPerfilActivity.this, "Por favor ingresa el número de cédula", Toast.LENGTH_LONG).show();
//            etCedulaUsuarioEditar.setError("El número de cédula es obligatorio");
//            etCedulaUsuarioEditar.requestFocus();
//        } else if (!validadorDeCedula(cedulaUsuarioEditar)) {
//            Toast.makeText(EditarInformacionPerfilActivity.this, "Por favor ingresa un número de cédula válido", Toast.LENGTH_LONG).show();
//            etCedulaUsuarioEditar.setError("El número de cédula es incorrecto");
//            etCedulaUsuarioEditar.requestFocus();
//        } else if (TextUtils.isEmpty(fechaNacUsuarioEditar)) {
//            Toast.makeText(EditarInformacionPerfilActivity.this, "Por favor ingresa una fecha de nacimiento", Toast.LENGTH_LONG).show();
//            etFechaNacUsuarioEditar.setError("La fecha de nacimiento es obligatoria");
//            etFechaNacUsuarioEditar.requestFocus();
//        } else if (calcularEdad(fechaNacUsuarioEditar) == -1) {
//            Toast.makeText(EditarInformacionPerfilActivity.this, "Por favor ingresa correctamente la fecha", Toast.LENGTH_LONG).show();
//            etFechaNacUsuarioEditar.setError("El formato de fecha debe ser (dd/mm/aaaa)");
//            etFechaNacUsuarioEditar.requestFocus();
//        } else if (TextUtils.isEmpty(estadoCivSeleccionadoEditar.getText())) {
//            Toast.makeText(EditarInformacionPerfilActivity.this, "Por favor selecciona un estado civil", Toast.LENGTH_LONG).show();
////                    estadoCivSeleccionado.setError("El estado civil es obligatorio");
////                    estadoCivSeleccionado.requestFocus();
//        } else if (TextUtils.isEmpty(ocupacionUsuarioEditar)) {
//            Toast.makeText(EditarInformacionPerfilActivity.this, "Por favor ingresa una profesión/ocupación", Toast.LENGTH_LONG).show();
//            etOcupacionUsuarioEditar.setError("La profesión/ocupación es obligatoria");
//            etOcupacionUsuarioEditar.requestFocus();
//        } else if (TextUtils.isEmpty(telefonoUsuarioEditar)) {
//            Toast.makeText(EditarInformacionPerfilActivity.this, "Por favor ingresa un número de teléfono", Toast.LENGTH_LONG).show();
//            etTelefonoUsuarioEditar.setError("El número de teléfono es obligatorio");
//            etTelefonoUsuarioEditar.requestFocus();
//        } else if (telefonoUsuarioEditar.length() < 10 || telefonoUsuarioEditar.length() > 10) {
//            Toast.makeText(EditarInformacionPerfilActivity.this, "Por favor ingresa nuevamente el número de teléfono", Toast.LENGTH_LONG).show();
//            etTelefonoUsuarioEditar.setError("El número de teléfono debe tener 10 dígitos");
//            etTelefonoUsuarioEditar.requestFocus();
//        } else {
//
//        }
    }

    private void cargarInformacionBD (String id, Map<String, Object> actualizaciones) {
        dbUsuarios.collection("Usuarios").document(id).update(actualizaciones).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
//                Toast.makeText(EditarInformacionPerfilActivity.this, "Información guardada correctamente", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(EditarInformacionPerfilActivity.this, EditarDomicilioActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("id", idCuenta);
                i.putExtra("rol", rol);
                i.putExtra("enAdministracion", enAdministracion);
                startActivity(i);
                procesando = false;
                btnGuardarCambiosPerfil.setEnabled(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                procesando = false;
                Toast.makeText(EditarInformacionPerfilActivity.this, "Error al cargar la informacion en la BD", Toast.LENGTH_SHORT).show();
                btnGuardarCambiosPerfil.setEnabled(true);
            }
        });

    }

    private boolean validarFormulario() {
        boolean valido = true;
        if (!controladorUtilidades.validarTextoVacio(etNombreUsuarioEditar, "Ingrese el nombre")) valido = false;
        if (!controladorUtilidades.validarTextoVacio(etFechaNacUsuarioEditar, "Ingrese la fecha de nacimiento")) valido = false;
        if (!controladorUtilidades.validarEstadoCiv(estadoCivOpsEditar, estadoCivSeleccionadoEditar)) valido = false;
        if (!controladorUtilidades.validarTextoVacio(etOcupacionUsuarioEditar, "Ingrese la profesión/ocupación")) valido = false;

        return valido;
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