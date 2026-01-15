package com.example.tic_pv.Vista;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.service.controls.Control;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tic_pv.Controlador.ControladorUsuario;
import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class CambiarClaveActivity extends AppCompatActivity {

    private FirebaseAuth authPerfil;
    private EditText etClave, etNuevaClave, etNuevaClaveConfir;
    private ImageView iVVerClaveActual, iVVerNuevaClave, iVVerConfirNuevaClave;
    private TextView tVMaximoCaracteres, tVMinusculas, tVMayusculas, tVNumero, tVCaracterEspecial
            , tVErrorConfirClave;
    private RelativeLayout rLCambiarClave;
    private TextView tvAutenticado;
    private Button btnAutenticar, btnCambiarClave;
    private String claveUsuario;
    private boolean todasValidas;
    private ControladorUsuario controladorUsuario = new ControladorUsuario();
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cambiar_clave);
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
                    ScrollView scrollView = findViewById(R.id.scrollCambiarClave);
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


//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollCambiarClave), (v, insets) -> {
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
//                    findViewById(R.id.scrollCambiarClave).post(() -> {
//                        ((ScrollView) findViewById(R.id.scrollCambiarClave)).smoothScrollBy(0, scrollDistance + 20); // Agrega un margen opcional
//                    });
//                }
//            }
//
//            return insets;
//        });


        etClave = findViewById(R.id.eTClaveCC);
        etNuevaClave = findViewById(R.id.etNuevaClaveCC);
        etNuevaClaveConfir = findViewById(R.id.etNuevaClaveConfirCC);
        tvAutenticado = findViewById(R.id.encabezadoActualizarClave);
        rLCambiarClave = findViewById(R.id.rLNuevaClave);

        //TextViews para la validacion de contraseña
        tVMaximoCaracteres = findViewById(R.id.tVMaxCaracteresCC);
        tVMinusculas = findViewById(R.id.tVMinusculasCC);
        tVMayusculas = findViewById(R.id.tVMayusculasCC);
        tVNumero = findViewById(R.id.tVNumeroCC);
        tVCaracterEspecial = findViewById(R.id.tVCaracterEspecialCC);
        tVErrorConfirClave = findViewById(R.id.tVErrorConfirClaveCC);

        //Definir las ImageView para ver contraseñas
        iVVerClaveActual = findViewById(R.id.iVVerClaveActual);
        iVVerNuevaClave = findViewById(R.id.iVVerNuevaClave);
        iVVerConfirNuevaClave = findViewById(R.id.iVVerConfirmNuevaClave);

        //Configurar los botones para ver las contraseñas
        controladorUtilidades.configurarBotonesVerClave(iVVerClaveActual, etClave);
        controladorUtilidades.configurarBotonesVerClave(iVVerNuevaClave, etNuevaClave);
        controladorUtilidades.configurarBotonesVerClave(iVVerConfirNuevaClave, etNuevaClaveConfir);

        //Definir los botones de la vista
        btnAutenticar = findViewById(R.id.btnConfirmarAutenticacion);
        btnCambiarClave = findViewById(R.id.btnGuardarCambioClave);

        //Deshabilitar campos para nueva contraseña y botón para guardar
//        etNuevaClave.setEnabled(false);
//        etNuevaClaveConfir.setEnabled(false);
//        btnCambiarClave.setEnabled(false);

        //Validaciones en tiempo real para los campos de contraseñas
//        controladorUtilidades.validarClave(this, etNuevaClave, etNuevaClaveConfir, tVMaximoCaracteres, tVMinusculas, tVMayusculas, tVNumero, tVCaracterEspecial, tVErrorConfirClave, "clave");
//        controladorUtilidades.validarClaveConfirmacionTiempoReal(etNuevaClave, etNuevaClaveConfir, tVErrorConfirClave, "confirmacion");
        controladorUtilidades.validarClaveYConfirmacionConjuntamente(this, etNuevaClave, etNuevaClaveConfir,
                tVMaximoCaracteres, tVMinusculas, tVMayusculas, tVNumero, tVCaracterEspecial,
                tVErrorConfirClave, "clave", "confirmacion");
//        controladorUtilidades.agregarValidacionEnTiempoReal(etNuevaClaveConfir, text -> controladorUtilidades.validarConfirmacionClave(etNuevaClave, etNuevaClaveConfir), tVErrorConfirClave, "confirmacion");

        authPerfil = FirebaseAuth.getInstance();
        FirebaseUser usuarioFirebase = authPerfil.getCurrentUser();

        if (usuarioFirebase == null) {
            Toast.makeText(this, "Error al obtener el inicio de sesión", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CambiarClaveActivity.this, VerInformacionPerfilActivity.class);
            startActivity(intent);
            finish();
        } else {
            reautenticarUsuario(usuarioFirebase);
        }

    }

    private void reautenticarUsuario(FirebaseUser usuarioFirebase) {

        //Definir el Alert Dialog para confirmar el cambio de clave
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar cambio de contraseña");
        builder.setMessage("¿Está seguro/a de que desea cambiar su contraseña?");

        btnAutenticar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                claveUsuario = etClave.getText().toString();

                if (TextUtils.isEmpty(claveUsuario)) {
                    Toast.makeText(CambiarClaveActivity.this, "Ingrese su contraseña actual", Toast.LENGTH_SHORT).show();
                } else {
                    AuthCredential credencial = EmailAuthProvider.getCredential(usuarioFirebase.getEmail(), claveUsuario);
                    usuarioFirebase.reauthenticate(credencial).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                etClave.setEnabled(false);
                                etNuevaClave.setEnabled(true);
                                etNuevaClaveConfir.setEnabled(true);

                                btnAutenticar.setEnabled(false);
                                iVVerClaveActual.setEnabled(false);
                                btnCambiarClave.setEnabled(false);

//                                tvAutenticado.setText("Autenticado");

                                rLCambiarClave.setVisibility(View.VISIBLE);

                                controladorUtilidades.setActualizarBotonCallback(() -> {
                                    todasValidas = !controladorUtilidades.getEstadoValidaciones().containsValue(false);
                                    btnCambiarClave.setEnabled(todasValidas);
                                });

//                                btnCambiarClave.setBackgroundTintList(ContextCompat.getColorStateList(
//                                        CambiarClaveActivity.this, com.google.android.gms.base.R.color.common_google_signin_btn_text_dark_disabled));

                                btnCambiarClave.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

//                                        String claveNueva, claveNuevaConfir;
//
//                                        claveNueva = etNuevaClave.getText().toString();
//                                        claveNuevaConfir = etNuevaClaveConfir.getText().toString();
                                        String claveNueva;
                                        claveNueva = etNuevaClave.getText().toString();

                                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                controladorUsuario.cambiarClaveUsuario(usuarioFirebase, claveNueva, CambiarClaveActivity.this);
                                                Toast.makeText(CambiarClaveActivity.this, "Formulario valido", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {dialog.dismiss();}
                                        });

                                        if (validarFormulario() && todasValidas) {
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        } else {
                                            Toast.makeText(CambiarClaveActivity.this, "Ingrese correctamente la información", Toast.LENGTH_SHORT).show();
                                        }

//                                        if (TextUtils.isEmpty(claveNueva)) {
//                                            Toast.makeText(CambiarClaveActivity.this, "Campo incompleto", Toast.LENGTH_LONG).show();
//                                        }
////                                        else if (!controladorUsuario.validarClave(claveNueva)) {
////                                            Toast.makeText(CambiarClaveActivity.this, "Campo no válido", Toast.LENGTH_LONG).show();
////                                        }
//                                        else if (TextUtils.isEmpty(claveNuevaConfir)) {
//                                            Toast.makeText(CambiarClaveActivity.this, "Campo incompleto", Toast.LENGTH_LONG).show();
//                                        } else if (!claveNueva.equals(claveNuevaConfir)) {
//                                            Toast.makeText(CambiarClaveActivity.this, "Campo no válido", Toast.LENGTH_LONG).show();
//                                            //Se limpian los campos de las contraseñas
//                        //                    claveUsuario.clearComposingText();
//                        //                    claveUsuarioConfir.clearComposingText();
//                                        } else if (claveNueva.equals(claveUsuario)) {
//                                            Toast.makeText(CambiarClaveActivity.this, "Campo no válido", Toast.LENGTH_SHORT).show();
//                                        } else {
//
//                                        }
                                    }
                                });
                            } else {
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (Exception e) {
                                    Toast.makeText(CambiarClaveActivity.this, "Contraseña incorrecta", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean validarFormulario () {
        boolean valido =  true;

        if (!controladorUtilidades.validarTextoVacio(etNuevaClave, "Ingrese su contraseña actual")) valido = false;
        if (!controladorUtilidades.validarTextoVacio(etNuevaClaveConfir, "Ingrese la confirmación de contraseña")) valido = false;

        return valido;
    }
}