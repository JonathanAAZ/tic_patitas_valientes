package com.example.tic_pv.Vista;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tic_pv.Controlador.ControladorUsuario;
import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Controlador.SessionManager;
import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Map;
import java.util.Objects;

public class IniciarSesionActivity extends AppCompatActivity {

    private EditText textCorreoIS, textClaveIS;
    private TextView tvRegistrarse;
    private LinearLayout barraProgresoIS;
    private ImageView verClave;
    private FirebaseAuth authIS;
    private EstadosCuentas estadosCuentas;
    private FirebaseFirestore dbCuentas = FirebaseFirestore.getInstance();
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private ControladorUsuario controladorUsuario = new ControladorUsuario();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_iniciar_sesion);
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
                focusedView.getLocationOnScreen(location);

                // Altura disponible para el contenido visible (pantalla - teclado)
                int availableHeight = getResources().getDisplayMetrics().heightPixels - imeInsets.bottom;

                // Asegurar que el campo no sea cubierto por el teclado
                int focusedViewBottom = location[1] + focusedView.getHeight();
                int scrollDistance = focusedViewBottom - availableHeight;

                if (scrollDistance > 0) {
                    // Desplaza solo la distancia necesaria para mostrar completamente el campo
                    findViewById(R.id.main).post(() -> {
                        ((ScrollView) findViewById(R.id.main)).smoothScrollBy(0, scrollDistance + 20); // Agrega un margen opcional
                    });
                }
            }

            return insets;
        });

//        pedirPermisosNotificaciones();

        textCorreoIS = findViewById(R.id.correoIS);
        textClaveIS = findViewById(R.id.claveIS);
        barraProgresoIS = findViewById(R.id.lLBarraProgresoIS);
        tvRegistrarse = findViewById(R.id.tvRegistrarse);

        //Configurar Edit Text del correo
        textCorreoIS.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(" ")) {
                    textCorreoIS.setText(s.toString().replace(" ", ""));
                    textCorreoIS.setSelection(textCorreoIS.getText().length()); // Mantiene el cursor al final
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Configurar botón para ver clave
        verClave = findViewById(R.id.iVVerClaveIS);
        controladorUtilidades.configurarBotonesVerClave(verClave, textClaveIS);
//        verClave.setImageResource(R.drawable.ic_ocultar_clave);
//        verClave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (textClaveIS.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
//                    //Si la contraseña es visible, se oculta
//                    textClaveIS.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    //Se cambia el ícono
//                    verClave.setImageResource(R.drawable.ic_ocultar_clave);
//                } else {
//                    textClaveIS.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                    verClave.setImageResource(R.drawable.ic_ver_clave);
//                }
//
//                textClaveIS.setSelection(textClaveIS.getText().length());
//            }
//        });

        authIS = FirebaseAuth.getInstance();

        //Botón para iniciar sesión
        Button btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correo = textCorreoIS.getText().toString();
                String clave = textClaveIS.getText().toString();

                if (TextUtils.isEmpty(correo)) {
                    ocultarTeclado(findViewById(android.R.id.content));
                    Toast.makeText(IniciarSesionActivity.this, "No se ha ingresado una dirección de correo electrónico", Toast.LENGTH_SHORT).show();
                    textCorreoIS.setError("Ingrese una direccion de correo electrónico");
                    textCorreoIS.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                    ocultarTeclado(findViewById(android.R.id.content));
                    Toast.makeText(IniciarSesionActivity.this, "El correo electrónico no es válido", Toast.LENGTH_SHORT).show();
                    textCorreoIS.setError("Ingrese una dirección de correo electrónico válida");
                    textCorreoIS.requestFocus();
                } else if (TextUtils.isEmpty(clave)) {
                    ocultarTeclado(findViewById(android.R.id.content));
                    Toast.makeText(IniciarSesionActivity.this, "No se ha ingresado una contraseña", Toast.LENGTH_SHORT).show();
                    textClaveIS.setError("Ingrese su contraseña");
                    textClaveIS.requestFocus();
                } else {
                    ocultarTeclado(findViewById(android.R.id.content));
                    barraProgresoIS.setVisibility(View.VISIBLE);
                    iniciarSesion(correo, clave);
                }
            }
        });

        tvRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IniciarSesionActivity.this, CrearCuentaActivity.class);
                startActivity(intent);
            }
        });

    }

    private void iniciarSesion (String correoUsuario, String claveUsuario) {
        authIS.signInWithEmailAndPassword(correoUsuario, claveUsuario).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    verificarInicioSesion(Objects.requireNonNull(task.getResult().getUser()).getUid());
//                    barraProgresoIS.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthException) {
                    ocultarTeclado(findViewById(android.R.id.content));
                    Toast.makeText(IniciarSesionActivity.this, "El correo o contraseña son incorrectos", Toast.LENGTH_SHORT).show();
                } else {
                    // Manejo general de errores
                    ocultarTeclado(findViewById(android.R.id.content));
                    Toast.makeText(IniciarSesionActivity.this, "Algo salió mal. Por favor, inténtalo de nuevo más tarde.", Toast.LENGTH_SHORT).show();
                }
                barraProgresoIS.setVisibility(View.GONE);
            }
        });
    }

    private void verificarInicioSesion (String idCuenta) {
        SessionManager sessionManager = new SessionManager(IniciarSesionActivity.this);
        barraProgresoIS.setVisibility(View.VISIBLE);
//        final String[] rol = new String[1];
        dbCuentas.collection("Cuentas").document(idCuenta).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String rolCuenta = documentSnapshot.getString("rol");
                    String estadoCuenta = documentSnapshot.getString("estado");

                    assert estadoCuenta != null;
                    if (estadoCuenta.equals(EstadosCuentas.ACTIVO.toString())) {

                        assert rolCuenta != null;
                        if (rolCuenta.equals("Adoptante")) {
                            Intent i = new Intent(IniciarSesionActivity.this, BottomNavigationMenu.class);
                            i.putExtra("id", R.id.bMInicio);
                            i.putExtra("rol", rolCuenta);
                            startActivity(i);
                            sessionManager.saveUserRole(rolCuenta);
//                            finish();
                            barraProgresoIS.setVisibility(View.GONE);
//                            pedirPermisosNotificaciones();
                            controladorUsuario.actualizarTokenInicioSesion(idCuenta);
                        } else if (rolCuenta.equals("Voluntario")) {
                            Intent i = new Intent(IniciarSesionActivity.this, BottomNavigationMenu.class);
                            i.putExtra("id", R.id.bMInicio);
                            i.putExtra("rol", rolCuenta);
                            startActivity(i);
                            sessionManager.saveUserRole(rolCuenta);
//                            finish();
                            barraProgresoIS.setVisibility(View.GONE);
//                            pedirPermisosNotificaciones();
                            controladorUsuario.actualizarTokenInicioSesion(idCuenta);
                        } else if (rolCuenta.equals("Administrador")) {
                            Intent i = new Intent(IniciarSesionActivity.this, BottomNavigationMenu.class);
                            i.putExtra("id", R.id.bMInicio);
                            i.putExtra("rol", rolCuenta);
                            startActivity(i);
                            sessionManager.saveUserRole(rolCuenta);
//                            finish();
                            barraProgresoIS.setVisibility(View.GONE);
//                            pedirPermisosNotificaciones();
                            controladorUsuario.actualizarTokenInicioSesion(idCuenta);
                        }
//                        Toast.makeText(IniciarSesionActivity.this, "Inicio de sesión correcto", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(IniciarSesionActivity.this, "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show();
                        barraProgresoIS.setVisibility(View.GONE);
                    }

                } else {
                    Toast.makeText(IniciarSesionActivity.this, "No se encontró el documento", Toast.LENGTH_SHORT).show();
                    barraProgresoIS.setVisibility(View.GONE);
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISOS", "Permiso de notificaciones concedido.");
            } else {
                Log.e("PERMISOS", "Permiso de notificaciones denegado.");
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.POST_NOTIFICATIONS)) {
                    Log.e("PERMISOS", "El usuario seleccionó 'No volver a preguntar'. Redirigir a Configuración.");
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onResume() {
        super.onResume();

        // Establecer un listener global en toda la vista raíz del Activity
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            rootView.setOnTouchListener((v, event) -> {
                // Ocultar teclado al tocar cualquier parte de la pantalla
                View currentFocus = getCurrentFocus();
                if (currentFocus != null) {
                    ocultarTeclado(currentFocus);
                    currentFocus.clearFocus(); // Limpia el foco para evitar volver a mostrar el teclado
                }
                return false; // Permitir que otros eventos táctiles sigan ocurriendo
            });
        }
    }

    // Método para ocultar el teclado
    private void ocultarTeclado(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

//    private void actualizar



//    @Override
//    protected void onResume() {
//        super.onResume();
//        View rootView = getView();
//        if (rootView != null) {
//            rootView.setOnTouchListener((v, event) -> {
//                View currentFocus = requireActivity().getCurrentFocus();
//                if (currentFocus != null) {
//                    ocultarTeclado(currentFocus);
//                }
//                return false;
//            });
//        }
//    }
}