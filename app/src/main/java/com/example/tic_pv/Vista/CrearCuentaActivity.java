package com.example.tic_pv.Vista;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tic_pv.Controlador.ControladorUsuario;
import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.CuentaUsuario;
import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.Modelo.Usuario;
import com.example.tic_pv.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public class CrearCuentaActivity extends AppCompatActivity {

    private EditText nombreUsuario, correoUsuario, cedulaUsuario, fechaNacUsuario, ocupacionUsuario,
            telefonoUsuario, instaUsuario,
            fbUsuario, claveUsuario, claveUsuarioConfir;
    private TextView tVEstadoCiv, tVMaximoCaracteres, tVMinusculas, tVMayusculas,
            tVNumero, tVCaracterEspecial, tVErrorCorreo, tVErrorCedula, tVErrorTelefono,
            tVErrorConfirClave;
    private ImageView iVVerClave, iVVerConfirmacionClave;
    private ProgressBar barraProgreso;
    private boolean procesando;
    private Button btnSiguienteReg;
    private RadioGroup estadoCivOps;
    private DatePickerDialog pickerDialog;
    private RadioButton estadoCivSeleccionado;
    private Usuario nuevoUsuario;
    private CuentaUsuario nuevaCuenta;
    private ControladorUsuario controladorUsuario = new ControladorUsuario();
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private FirebaseAuth auth;
//    private DatabaseReference dbRef;
    private FirebaseFirestore mFirestore;
    private EstadosCuentas estadoObj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_cuenta);
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
                    ScrollView scrollView = findViewById(R.id.scrollCrearCuenta);
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


//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//
//            // Ajustar el padding del contenedor principal
//            int bottomPadding = imeInsets.bottom > 0 ? imeInsets.bottom : systemBars.bottom;
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding);
//
//            // Verificar si hay un EditText enfocado
//            View focusedView = getCurrentFocus();
//            if (focusedView instanceof EditText && imeInsets.bottom > 0) {
//                int[] location = new int[2];
//                focusedView.getLocationOnScreen(location);
//
//                // Altura disponible para el contenido visible (pantalla - teclado)
//                int availableHeight = getResources().getDisplayMetrics().heightPixels - imeInsets.bottom;
//
//                // Asegurar que el campo no sea cubierto por el teclado
//                int focusedViewBottom = location[1] + focusedView.getHeight();
//                int scrollDistance = focusedViewBottom - availableHeight;
//
//                if (scrollDistance > 0) {
//                    // Desplaza solo la distancia necesaria para mostrar completamente el campo
//                    findViewById(R.id.scrollCrearCuenta).post(() -> {
//                        ((ScrollView) findViewById(R.id.scrollCrearCuenta)).smoothScrollBy(0, scrollDistance + 20); // Agrega un margen opcional
//                    });
//                }
//            }
//
//            return insets;
//        });

//        getSupportActionBar().setTitle("Registrar");
        auth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
//        dbRef = FirebaseDatabase.getInstance().getReference();

//        Toast.makeText(this, "Ingresa los datos personales", Toast.LENGTH_LONG).show();

        nombreUsuario = findViewById(R.id.nombreUsuario);
        correoUsuario = findViewById(R.id.correoUsuario);
        cedulaUsuario = findViewById(R.id.cedulaUsuario);
        fechaNacUsuario = findViewById(R.id.fechaNacUsuario);
        ocupacionUsuario = findViewById(R.id.ocupacionUsuario);
        telefonoUsuario = findViewById(R.id.telefonoUsuario);
        instaUsuario = findViewById(R.id.instaUsuario);
        fbUsuario = findViewById(R.id.fbUsuario);
        barraProgreso = findViewById(R.id.barraProgresoCrearCuentaUsuario);

        //Bloquear boton "Siguiente" hasta que se hayan validado los datos
        btnSiguienteReg = findViewById(R.id.btnSiguienteReg);
        btnSiguienteReg.setEnabled(false);

        //Obtener los TextView para mostrar errores
//        tVFechaNac = findViewById(R.id.textFechaNac);
        tVEstadoCiv = findViewById(R.id.textEstadoCiv);
        tVErrorCorreo = findViewById(R.id.tVErrorCorreoUsuario);
        tVErrorCedula = findViewById(R.id.tVErrorCedulaUsuario);
        tVErrorTelefono = findViewById(R.id.tVErrorTelefonoUsuario);
        tVErrorConfirClave = findViewById(R.id.tVErrorConfirClave);


        //Radio Group Estado Civil
        estadoCivOps = findViewById(R.id.estadoCivOps);
        estadoCivOps.clearCheck();

        //Limpiar errores del Estado Civil
        controladorUtilidades.limpiarErroresRadioGroup(estadoCivOps, tVEstadoCiv);

        //Colocar un DatePicker en el EditText de la fecha de nacimiento
        controladorUtilidades.colocarDatePicker(fechaNacUsuario, this);

        //Obtener lo relacionado con la contraseña y su validación
        claveUsuario = findViewById(R.id.claveUsuario);
        claveUsuarioConfir = findViewById(R.id.claveUsuarioConfir);
        tVMaximoCaracteres = findViewById(R.id.tVMaxCaracteres);
        tVMinusculas = findViewById(R.id.tVMinusculas);
        tVMayusculas = findViewById(R.id.tVMayusculas);
        tVNumero = findViewById(R.id.tVNumero);
        tVCaracterEspecial = findViewById(R.id.tVCaracterEspecial);

        //Configuracion de botones para ver contraseñas
        iVVerClave = findViewById(R.id.iVVerClaveCrearCuenta);
        iVVerConfirmacionClave = findViewById(R.id.iVVerConfirCrearCuenta);
        controladorUtilidades.configurarBotonesVerClave(iVVerClave, claveUsuario);
        controladorUtilidades.configurarBotonesVerClave(iVVerConfirmacionClave, claveUsuarioConfir);

        //Verificar que se hayan realizado las validaciones correspondientes
        controladorUtilidades.setActualizarBotonCallback(() -> {
            boolean todasValidas = !controladorUtilidades.getEstadoValidaciones().containsValue(false);
            btnSiguienteReg.setEnabled(todasValidas);
        });

        controladorUtilidades.agregarValidacionEnTiempoReal(correoUsuario, text -> controladorUtilidades.validarCorreo(correoUsuario), tVErrorCorreo, "correo");
        controladorUtilidades.agregarValidacionEnTiempoReal(cedulaUsuario, controladorUtilidades::validadorDeCedula, tVErrorCedula, "cedula");
        controladorUtilidades.agregarValidacionEnTiempoReal(telefonoUsuario, controladorUtilidades::validarNumeroCelular, tVErrorTelefono, "telefono");
//        controladorUtilidades.validarClave(this, claveUsuario, claveUsuarioConfir, tVMaximoCaracteres, tVMinusculas, tVMayusculas, tVNumero, tVCaracterEspecial, tVErrorConfirClave, "clave");
//        controladorUtilidades.agregarValidacionEnTiempoReal(claveUsuarioConfir, text -> controladorUtilidades.validarConfirmacionClave(claveUsuario, claveUsuarioConfir), tVErrorConfirClave, "confirmacion");
//        controladorUtilidades.validarClaveConfirmacionTiempoReal(claveUsuario, claveUsuarioConfir, tVErrorConfirClave, "confirmacion");
        controladorUtilidades.validarClaveYConfirmacionConjuntamente(this, claveUsuario, claveUsuarioConfir,
                tVMaximoCaracteres, tVMinusculas, tVMayusculas, tVNumero, tVCaracterEspecial,
                tVErrorConfirClave, "clave", "confirmacion");

        btnSiguienteReg.setOnClickListener(new View.OnClickListener() {
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

                //Mostrar la barra de progreso
                barraProgreso.setVisibility(View.VISIBLE);

                int IDestadoCivSeleccionado = estadoCivOps.getCheckedRadioButtonId();
                estadoCivSeleccionado = findViewById(IDestadoCivSeleccionado);

                //Obtener todos los datos
                String textNombre = nombreUsuario.getText().toString();
                String textCorreo = correoUsuario.getText().toString();
                String textCedula = cedulaUsuario.getText().toString();
                String textFechaNac = fechaNacUsuario.getText().toString();
                String textOcupacion = ocupacionUsuario.getText().toString();
                String textTelefono = telefonoUsuario.getText().toString();
                String textInsta = instaUsuario.getText().toString();
                String textFB = fbUsuario.getText().toString();
                String textClave = claveUsuario.getText().toString();
                String textEstadoUsuario = estadoObj.ACTIVO.toString();
                final String[] idDispositivo = {""};

                int edad;
                String textEstadoCiv;   //Se debe confirmar que se ha seleccionado una opción
                boolean formularioValido = validarFormulario();

                if (formularioValido) {
                    ocultarTeclado();
                    edad = ControladorUsuario.calcularEdad(textFechaNac);
                    textEstadoCiv = estadoCivSeleccionado.getText().toString();
                    if (TextUtils.isEmpty(textInsta)) {
                        textInsta = "Sin usuario de Instagram";
                    }
                    if (TextUtils.isEmpty(textFB)) {
                        textFB = "Sin usuario de Facebook";
                    }

                    nuevoUsuario = new Usuario(textNombre, textCedula, edad, textFechaNac, textEstadoCiv, textOcupacion, textTelefono, textEstadoUsuario, textInsta, textFB);

                    controladorUsuario.obtenerTokenRegistro(new ControladorUsuario.TokenCallBack() {
                        @Override
                        public void tokenRecibido(String token) {
                            idDispositivo[0] = token;
                            nuevaCuenta = new CuentaUsuario("Por seleccionar", textCorreo, textClave, "Adoptante", EstadosCuentas.ACTIVO.toString(), idDispositivo[0]);

                            barraProgreso.setVisibility(View.VISIBLE);
                            Toast.makeText(CrearCuentaActivity.this, "ID DISPOSITIVO" + nuevaCuenta.getIdDispositivo(), Toast.LENGTH_SHORT).show();
                            enviarObjetos(nuevoUsuario, nuevaCuenta);
                        }
                    });

                } else {
                    ocultarTeclado();
                    barraProgreso.setVisibility(View.GONE);
                    v.setEnabled(true);
                    procesando = false;
//                    barraProgreso.setVisibility(View.GONE);
                    Toast.makeText(CrearCuentaActivity.this, "Existen campos vacíos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Método para validar campos vacíos en el formulario
    private boolean validarFormulario() {
        boolean valido = true;

        if (!controladorUtilidades.validarTextoVacio(nombreUsuario, "Ingrese el nombre")) valido = false;
        if (!controladorUtilidades.validarTextoVacio(fechaNacUsuario, "Ingrese la fecha de nacimiento")) valido = false;
        if (!controladorUtilidades.validarEstadoCiv(estadoCivOps, tVEstadoCiv)) valido = false;
        if (!controladorUtilidades.validarTextoVacio(ocupacionUsuario, "Ingrese la profesión/ocupación")) valido = false;
//        if (controladorUtilidades.validarConfirmacionClave(claveUsuario, claveUsuarioConfir));
        return valido;
    }

    private void enviarObjetos (Usuario usuario, CuentaUsuario cuenta) {

        System.out.println("CORREO UTILIZADO ---------" + cuenta.getCorreo().toString());

        controladorUsuario.verificarCorreoUsado(cuenta.getCorreo(), new ControladorUsuario.OnCorreoVerificadoListener() {
            @Override
            public void onCorreoVerificado(boolean enUso) {
                runOnUiThread(() -> {
                    // Restablecer el estado de procesamiento
                    procesando = false;

                    if (enUso) {
                        Toast.makeText(CrearCuentaActivity.this, "El correo ingresado ya se encuentra registrado en la aplicación", Toast.LENGTH_SHORT).show();
                        barraProgreso.setVisibility(View.GONE);
                        btnSiguienteReg.setEnabled(true);
                    } else {
                        Intent i = new Intent(CrearCuentaActivity.this, AgregarDomicilioActivity.class);
                        i.putExtra("usuario", usuario);
                        i.putExtra("cuenta", cuenta);
                        startActivity(i);

                        // Opcional: En caso de que la activity no se cierre
                        barraProgreso.setVisibility(View.GONE);
                        btnSiguienteReg.setEnabled(true);
                    }
                });
//                if (enUso) {
//                    Toast.makeText(CrearCuentaActivity.this, "El correo ingresado ya se encuentra registrado en el sistema", Toast.LENGTH_SHORT).show();
//                } else {
//                    Intent i = new Intent(CrearCuentaActivity.this, AgregarDomicilioActivity.class);
//                    i.putExtra("usuario", usuario);
//                    i.putExtra("cuenta", cuenta);
//                    startActivity(i);
//                    barraProgreso.setVisibility(View.GONE);
//                }
            }
        });
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