package com.example.tic_pv.Vista;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.example.tic_pv.Modelo.CuentaUsuario;
import com.example.tic_pv.Modelo.Domicilio;
import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.Modelo.Usuario;
import com.example.tic_pv.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CrearVoluntarioActivity extends AppCompatActivity {

    private EditText nombreVoluntario, correoVoluntario, cedulaVoluntario,
            fechaNacVoluntario, ocupacionVoluntario, telefonoVoluntario;
    private TextView tVFechaNac, tVEstadoCiv, tVErrorCorreo, tVErrorCedula, tVErrorTelefono;
    private boolean procesando;
    private DatePickerDialog pickerDialog;
    private RadioGroup estadoCivOpsVoluntario;
    private RadioButton estadoCivSeleccionadoVolun;
    private Usuario nuevoVoluntario;
    private CuentaUsuario nuevaCuentaVoluntario;
    private ControladorUsuario controladorUsuario = new ControladorUsuario();
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private FirebaseAuth authVoluntario;
    //    private DatabaseReference dbRef;
    private FirebaseFirestore mFirestore;
    private EstadosCuentas estadoObj;
    private StorageReference storageReference;
    private String urlSinFotoPerfil = "sin_foto_perfil.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_voluntario);
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
                    ScrollView scrollView = findViewById(R.id.scrollCrearVoluntario);
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

        authVoluntario = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("FotosPerfil");
//        dbRef = FirebaseDatabase.getInstance().getReference();

        Toast.makeText(this, "Ingresa los datos personales", Toast.LENGTH_LONG).show();

        nombreVoluntario = findViewById(R.id.etNombreVoluntario);
        correoVoluntario = findViewById(R.id.etCorreoVoluntario);
        cedulaVoluntario = findViewById(R.id.etCedulaVoluntario);
        fechaNacVoluntario = findViewById(R.id.etFechaNacVoluntario);
        ocupacionVoluntario = findViewById(R.id.etOcupacionVoluntario);
        telefonoVoluntario = findViewById(R.id.etTelefonoVoluntario);

        //TextViews para mostrar errores en la fecha de nacimiento y estado civil
        tVFechaNac = findViewById(R.id.tvFechaNacVoluntario);
        tVEstadoCiv = findViewById(R.id.tvEstadoCivVoluntario);
        tVErrorCorreo = findViewById(R.id.tVErrorCorreoVoluntario);
        tVErrorCedula = findViewById(R.id.tVErrorCedulaVoluntario);
        tVErrorTelefono = findViewById(R.id.tVErrorTelefonoVoluntario);

        //Radio Group Estado Civil Voluntario
        estadoCivOpsVoluntario = findViewById(R.id.rgEstCivOpsVoluntario);
        estadoCivOpsVoluntario.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                tVEstadoCiv.setError(null);
            }
        });
        estadoCivOpsVoluntario.clearCheck();

        //Botón para guardar información
        Button btnGuardarVoluntario = findViewById(R.id.btnGuardarVoluntario);
        btnGuardarVoluntario.setEnabled(false);

        //Colocar un Date Picker en el Edit Text de la fecha de nacimiento
        controladorUtilidades.colocarDatePicker(fechaNacVoluntario, this);

        //Verificar que se hayan realizado las validaciones correspondientes
        controladorUtilidades.setActualizarBotonCallback(() -> {
            boolean todasValidas = !controladorUtilidades.getEstadoValidaciones().containsValue(false);
            btnGuardarVoluntario.setEnabled(todasValidas);
        });

        controladorUtilidades.agregarValidacionEnTiempoReal(correoVoluntario, text -> controladorUtilidades.validarCorreo(correoVoluntario), tVErrorCorreo, "correo");
        controladorUtilidades.agregarValidacionEnTiempoReal(cedulaVoluntario, controladorUtilidades::validadorDeCedula, tVErrorCedula, "cedula");
        controladorUtilidades.agregarValidacionEnTiempoReal(telefonoVoluntario, controladorUtilidades::validarNumeroCelular, tVErrorTelefono, "telefono");

//        fechaNacVoluntario.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //Establecer el Locale en español
//                Locale locale = new Locale("es", "ES");
//                Locale.setDefault(locale);
//                Configuration config = getResources().getConfiguration();
//                config.setLocale(locale);
//                getResources().updateConfiguration(config, getResources().getDisplayMetrics());
//
//                final Calendar calendario = Calendar.getInstance();
//                int dia = calendario.get(Calendar.DAY_OF_MONTH);
//                int mes = calendario.get(Calendar.MONTH - 1);
//                int anio = calendario.get(Calendar.YEAR);
//
//                //Ventana con DatePicker
//                pickerDialog = new DatePickerDialog(CrearVoluntarioActivity.this, new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                        String fechaFormateada = String.format("%02d/%02d/%04d", dayOfMonth, month, year);
//                        fechaNacVoluntario.setText(fechaFormateada);
//                    }
//                }, anio, mes, dia);
//                pickerDialog.show();
//            }
//        });

        //Crear Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(CrearVoluntarioActivity.this);
        builder.setTitle("Mensaje de confirmación");
        builder.setMessage("¿Está seguro/a de que desea crear una nueva cuenta de voluntario con la información ingresada?");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                crearVoluntario(nuevoVoluntario, nuevaCuentaVoluntario);
                btnGuardarVoluntario.setEnabled(false);
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                btnGuardarVoluntario.setEnabled(true);
            }
        });

//        Button btnGuardarVoluntario = findViewById(R.id.btnGuardarVoluntario);

        btnGuardarVoluntario.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                int IDestadoCivSeleccionado = estadoCivOpsVoluntario.getCheckedRadioButtonId();
                estadoCivSeleccionadoVolun = findViewById(IDestadoCivSeleccionado);

                String textNombre = nombreVoluntario.getText().toString();
                String textCorreo = correoVoluntario.getText().toString();
                String textCedula = cedulaVoluntario.getText().toString();
                String textFechaNac = fechaNacVoluntario.getText().toString();
                String textOcupacion = ocupacionVoluntario.getText().toString();
                String textTelefono = telefonoVoluntario.getText().toString();
                String textInsta = "Sin usuario de Instagram";
                String textFB = "Sin usuario de Facebook";
                String textEstadoVoluntario = EstadosCuentas.ACTIVO.toString();
                String textClave;
                String idDispositivo = "";


                int edad;
                String textEstadoCivVoluntario;   //Se debe confirmar que se ha seleccionado una opción

                boolean formularioValido = validarFormulario();

                if (formularioValido) {
                    edad = ControladorUsuario.calcularEdad(textFechaNac);
                    textEstadoCivVoluntario = estadoCivSeleccionadoVolun.getText().toString();
                    textClave = cedulaVoluntario.getText().toString();

                    nuevoVoluntario = new Usuario(textNombre, textCedula, edad, textFechaNac, textEstadoCivVoluntario, textOcupacion, textTelefono, textEstadoVoluntario, textInsta, textFB);
                    nuevaCuentaVoluntario = new CuentaUsuario("Por seleccionar", textCorreo, textClave, "Voluntario", EstadosCuentas.ACTIVO.toString(), idDispositivo);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    v.setEnabled(true);
                    btnGuardarVoluntario.setEnabled(true);
                    Toast.makeText(CrearVoluntarioActivity.this, "Existen campos vacíos", Toast.LENGTH_SHORT).show();
                }

//                if (TextUtils.isEmpty(textNombre)) {
//                    Toast.makeText(CrearVoluntarioActivity.this, "Por favor ingresa el nombre completo", Toast.LENGTH_LONG).show();
//                    nombreVoluntario.setError("El nombre es obligatorio");
//                    nombreVoluntario.requestFocus();
//                } else if (TextUtils.isEmpty(textCorreo)) {
//                    Toast.makeText(CrearVoluntarioActivity.this, "Por favor ingresa el correo electrónico", Toast.LENGTH_LONG).show();
//                    correoVoluntario.setError("El correo electrónico es obligatorio");
//                    correoVoluntario.requestFocus();
//                } else if (!Patterns.EMAIL_ADDRESS.matcher(textCorreo).matches()) {
//                    Toast.makeText(CrearVoluntarioActivity.this, "Por favor ingresa una dirección de correo válida", Toast.LENGTH_LONG).show();
//                    correoVoluntario.setError("El correo electrónico no es válido");
//                    correoVoluntario.requestFocus();
//                } else if (TextUtils.isEmpty(textCedula)) {
//                    Toast.makeText(CrearVoluntarioActivity.this, "Por favor ingresa el número de cédula", Toast.LENGTH_LONG).show();
//                    cedulaVoluntario.setError("El número de cédula es obligatorio");
//                    cedulaVoluntario.requestFocus();
//                } else if (!controladorUtilidades.validadorDeCedula(textCedula)) {
//                    Toast.makeText(CrearVoluntarioActivity.this, "Por favor ingresa un número de cédula válido", Toast.LENGTH_LONG).show();
//                    cedulaVoluntario.setError("El número de cédula es incorrecto");
//                    cedulaVoluntario.requestFocus();
//                } else if (TextUtils.isEmpty(textFechaNac)) {
//                    Toast.makeText(CrearVoluntarioActivity.this, "Por favor ingresa una fecha de nacimiento", Toast.LENGTH_LONG).show();
//                    tVFechaNac.setError("La fecha de nacimiento es obligatoria");
//                    tVFechaNac.requestFocus();
//                } else if (controladorUsuario.calcularEdad(textFechaNac) == -1) {
//                    Toast.makeText(CrearVoluntarioActivity.this, "Por favor ingresa correctamente la fecha", Toast.LENGTH_LONG).show();
//                    fechaNacVoluntario.setError("El formato de fecha debe ser (dd/mm/aaaa)");
//                    fechaNacVoluntario.requestFocus();
//                } else if (estadoCivOpsVoluntario.getCheckedRadioButtonId() == -1) {
//                    Toast.makeText(CrearVoluntarioActivity.this, "Por favor selecciona un estado civil", Toast.LENGTH_LONG).show();
//                    tVEstadoCiv.setError("El estado civil es obligatorio");
//                    tVEstadoCiv.requestFocus();
//                } else if (TextUtils.isEmpty(textOcupacion)) {
//                    Toast.makeText(CrearVoluntarioActivity.this, "Por favor ingresa una profesión/ocupación", Toast.LENGTH_LONG).show();
//                    ocupacionVoluntario.setError("La profesión/ocupación es obligatoria");
//                    ocupacionVoluntario.requestFocus();
//                } else if (TextUtils.isEmpty(textTelefono)) {
//                    Toast.makeText(CrearVoluntarioActivity.this, "Por favor ingresa un número de teléfono", Toast.LENGTH_LONG).show();
//                    telefonoVoluntario.setError("El número de teléfono es obligatorio");
//                    telefonoVoluntario.requestFocus();
//                } else if (!controladorUtilidades.validarNumeroCelular(textTelefono)) {
//                    Toast.makeText(CrearVoluntarioActivity.this, "Por favor ingresa nuevamente el número de teléfono", Toast.LENGTH_LONG).show();
//                    telefonoVoluntario.setError("El número de teléfono debe tener 10 dígitos");
//                    telefonoVoluntario.requestFocus();
//                } else {
//
//                }
            }
        });

    }

    private void crearVoluntario(Usuario voluntario, CuentaUsuario cuenta) {
        String correoString = cuenta.getCorreo();
        String claveString = cuenta.getClave();

        Toast.makeText(this, "CUENTA CREADA PRUEBA", Toast.LENGTH_SHORT).show();

        authVoluntario.createUserWithEmailAndPassword(correoString, claveString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    String idDB = authVoluntario.getCurrentUser().getUid();

                    StorageReference referenciaArchivo = storageReference.child(urlSinFotoPerfil); // Ruta de la imagen predeterminada

                    referenciaArchivo.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri descargarUri = uri;

                            UserProfileChangeRequest subirFotoPerfil = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(descargarUri).build();
                            authVoluntario.getCurrentUser().updateProfile(subirFotoPerfil);
//                            System.out.println("DIRECCION DE LA FOTO DE PERFIL: " + descargarUri.toString());

                            cuenta.setIdCuenta(idDB);
                            cuenta.setFotoPerfil(descargarUri.toString());

                            Map<String, Object> mapCuentas = new HashMap<>();
//                    mapCuentas.put("id", cuenta.getIdCuenta());
                            mapCuentas.put("estado", cuenta.getEstadoCuenta());
                            mapCuentas.put("fotoPerfil", cuenta.getFotoPerfil());
                            mapCuentas.put("correo", cuenta.getCorreo());
                            mapCuentas.put("clave", cuenta.getClave());
                            mapCuentas.put("rol", cuenta.getRol());
                            mapCuentas.put("dispositivo", cuenta.getIdDispositivo());

                            mFirestore.collection("Cuentas").document(cuenta.getIdCuenta()).set(mapCuentas).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(CrearVoluntarioActivity.this, "Se creó la cuenta correctamente", Toast.LENGTH_LONG).show();
                                    crearVoluntarioDB(cuenta.getIdCuenta(), voluntario);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CrearVoluntarioActivity.this, "No se creó la cuenta en DB", Toast.LENGTH_LONG).show();
                                    System.out.println("Error crear cuentas en DB" + e);
                                }
                            });

                            Toast.makeText(CrearVoluntarioActivity.this, "Cuenta creada correctamente", Toast.LENGTH_SHORT).show();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CrearVoluntarioActivity.this, "No se pudo obtener la imagen predeterminada", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Exception excepcion = task.getException();
                    if (excepcion instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(CrearVoluntarioActivity.this, "El correo ingresado ya se encuentran registrado en la aplicación", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CrearVoluntarioActivity.this, "Error al registrar al voluntario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CrearVoluntarioActivity.this, "No se pudo crear la cuenta", Toast.LENGTH_LONG).show();
                System.out.println("Excepcion: " + e.getMessage());
            }
        });
    }

    private void crearVoluntarioDB (String idCuenta, Usuario voluntario) {
        String domicilioVacio = "N/A";
        Domicilio domicilio = new Domicilio(domicilioVacio, domicilioVacio, domicilioVacio, domicilioVacio, domicilioVacio, domicilioVacio, estadoObj.ACTIVO.toString());

        Map<String, Object> mapDomicilios = new HashMap<>();
        mapDomicilios.put("estado", domicilio.getEstadoDomicilio());
        mapDomicilios.put("pais", domicilio.getPais());
        mapDomicilios.put("provincia", domicilio.getProvincia());
        mapDomicilios.put("canton", domicilio.getCanton());
        mapDomicilios.put("parroquia", domicilio.getParroquia());
        mapDomicilios.put("barrio", domicilio.getBarrio());
        mapDomicilios.put("calles", domicilio.getCalles());

        mFirestore.collection("Domicilios").add(mapDomicilios).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                System.out.println("ID del domicilio: " + documentReference.getId());

//                documentReference.update("id", documentReference.getId());
                voluntario.setDomicilioUsuario(documentReference.getId());
                voluntario.setCuenta(idCuenta);

                Map<String, Object> mapUsuarios = new HashMap<>();
                mapUsuarios.put("estado", voluntario.getEstadoUsuario());
                mapUsuarios.put("nombre", voluntario.getNombre());
                mapUsuarios.put("cedula", voluntario.getCedula());
                mapUsuarios.put("edad", voluntario.getEdad());
                mapUsuarios.put("fechaNac", voluntario.getFechaNacimento());
                mapUsuarios.put("estadoCiv", voluntario.getEstadoCivil());
                mapUsuarios.put("ocupacion", voluntario.getOcupacion());
                mapUsuarios.put("telefono", voluntario.getTelefono());
                mapUsuarios.put("ig", voluntario.getIg());
                mapUsuarios.put("fb", voluntario.getFb());
                mapUsuarios.put("domicilioUsuario", voluntario.getDomicilioUsuario());
                mapUsuarios.put("cuentaUsuario", voluntario.getCuenta());

                mFirestore.collection("Usuarios").add(mapUsuarios).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
//                        documentReference.update("id", documentReference.getId());
                        Toast.makeText(CrearVoluntarioActivity.this, "Se creó el usuario en DB", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CrearVoluntarioActivity.this, "No se creó el usuario en DB", Toast.LENGTH_LONG).show();
                        System.out.println("Error crear usuarios en DB" + e);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                voluntario.setDomicilioUsuario("No se agregó el domicilio");
                Toast.makeText(CrearVoluntarioActivity.this, "No se pudo agregar el domicilio a la BD", Toast.LENGTH_SHORT).show();
            }
        });

        Intent i = new Intent(CrearVoluntarioActivity.this, VerUsuariosActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private boolean validarFormulario() {
        boolean valido = true;

        if (!controladorUtilidades.validarTextoVacio(nombreVoluntario, "Ingrese el nombre del voluntario")) valido = false;
        if (!controladorUtilidades.validarTextoVacio(fechaNacVoluntario, "Ingrese la fecha de nacimiento del voluntario")) valido = false;
        if (!controladorUtilidades.validarEstadoCiv(estadoCivOpsVoluntario, tVEstadoCiv)) valido = false;
        if (!controladorUtilidades.validarTextoVacio(ocupacionVoluntario, "Ingrese la profesión/ocupación del voluntario")) valido = false;


        return valido;
    }

    private String obtenerRol (String idCuenta) {
        final String[] rol = new String[1];
        mFirestore.collection("Cuentas").document(idCuenta).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> cuentas = documentSnapshot.getData();
                    String rolCuenta = documentSnapshot.getString("rol");
                    rol[0] = rolCuenta;
                    Toast.makeText(CrearVoluntarioActivity.this, "Este es el rol: " + rol[0], Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CrearVoluntarioActivity.this, "No se encontró el documento", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return rol[0];
    }

//    private static boolean validadorDeCedula(String cedula) {
//        boolean cedulaCorrecta = false;
//
//        try {
//            // Verificar que la cédula tenga 10 caracteres
//            if (cedula.length() == 10) {
//                int tercerDigito = Integer.parseInt(cedula.substring(2, 3));
//
//                // Verificar que el tercer dígito sea menor que 6
//                if (tercerDigito < 6) {
//                    // Coeficientes de validación de la cédula
//                    int[] coefValCedula = { 2, 1, 2, 1, 2, 1, 2, 1, 2 };
//                    int verificador = Integer.parseInt(cedula.substring(9, 10));
//                    int suma = 0;
//                    int digito = 0;
//
//                    // Calcular la suma de los productos de cada dígito por su coeficiente
//                    for (int i = 0; i < (cedula.length() - 1); i++) {
//                        digito = Integer.parseInt(cedula.substring(i, i + 1)) * coefValCedula[i];
//                        suma += ((digito % 10) + (digito / 10));
//                    }
//
//                    // Validar si el dígito verificador es correcto
//                    if ((suma % 10 == 0 && suma % 10 == verificador) || (10 - (suma % 10)) == verificador) {
//                        cedulaCorrecta = true;
//                    } else {
//                        cedulaCorrecta = false;
//                    }
//                } else {
//                    cedulaCorrecta = false;
//                }
//            } else {
//                cedulaCorrecta = false;
//            }
//        } catch (NumberFormatException nfe) {
//            cedulaCorrecta = false;
//        } catch (Exception err) {
//            System.out.println("Una excepción ocurrió en el proceso de validación");
//            cedulaCorrecta = false;
//        }
//
////        if (!cedulaCorrecta) {
////            System.out.println("La Cédula ingresada es Incorrecta");
////        }
//
//        return cedulaCorrecta;
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private static int calcularEdad(String fechaNacimiento) {
//        // Definir el formato de fecha
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//        try {
//            // Parsear la fecha de nacimiento
//            LocalDate fechaNac = LocalDate.parse(fechaNacimiento, formatter);
//
//            // Obtener la fecha actual
//            LocalDate fechaActual = LocalDate.now();
//
//            // Calcular el periodo entre la fecha de nacimiento y la fecha actual
//            Period periodo = Period.between(fechaNac, fechaActual);
//
//            // Devolver la cantidad de años como la edad
//            return periodo.getYears();
//
//        } catch (DateTimeParseException e) {
//            // Manejar excepción en caso de formato de fecha inválido
//
////            System.out.println("El formato de la fecha de nacimiento es inválido. Por favor, use el formato dd/MM/yyyy.");
//            return -1; // Devolver -1 para indicar un error en el formato
//        }
//    }
}