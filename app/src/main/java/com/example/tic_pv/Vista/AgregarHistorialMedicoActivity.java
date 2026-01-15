package com.example.tic_pv.Vista;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tic_pv.Controlador.ControladorHistorialMedico;
import com.example.tic_pv.Controlador.ControladorMascota;
import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.Desparasitacion;
import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.Modelo.HistorialMedico;
import com.example.tic_pv.Modelo.Mascota;
import com.example.tic_pv.R;
import com.example.tic_pv.databinding.ActivityAgregarHistorialMedicoBinding;

public class AgregarHistorialMedicoActivity extends AppCompatActivity {

    private final ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private final ControladorMascota controladorMascota = new ControladorMascota();
    private final ControladorHistorialMedico controladorHistorialMedico = new ControladorHistorialMedico();
    private String idMascota;
    private boolean isVacuna, procesando;
    private ActivityAgregarHistorialMedicoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_historial_medico);
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
                    ScrollView scrollView = findViewById(R.id.scrollAgregarHistorial);
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

        binding = ActivityAgregarHistorialMedicoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        idMascota = intent.getStringExtra("idMascota");
        isVacuna = intent.getBooleanExtra("isVacuna", false);

        Dialog alerta = controladorUtilidades.crearAlertaPersonalizada("MENSAJE DE CONFIRMACIÓN", "¿Seguro/a que desea guardar la información ingresada?", this);
        LinearLayout btnAceptar = alerta.findViewById(R.id.lLbtnAceptar);
        LinearLayout btnCancelar = alerta.findViewById(R.id.lLbtnCancelar);
        btnAceptar.setVisibility(View.VISIBLE);
        btnCancelar.setVisibility(View.VISIBLE);

        binding.btnGuardarHistorial.setOnClickListener(v -> {
            // Configurar botones de la alerta
            btnAceptar.setOnClickListener(view -> {
                guardarHistorialMedico();
            });

            btnCancelar.setOnClickListener(view -> {
                alerta.dismiss();
                v.setEnabled(true);
                procesando = false;
            });

            binding.barraProgresoAgregarHistorial.setVisibility(View.VISIBLE);
            //Verificar si ya está en proceso
            if (procesando) {
                return; //Salir si ya está procesando
            }
            //Marcar como "Procesando"
            procesando = true;

            //Deshabilitar el botón inmediantamente
            v.setEnabled(false);
            boolean formularioValido = validarFormulario();

            if (formularioValido) {
                alerta.create();
                alerta.show();
            } else {
                ocultarTeclado();
                binding.barraProgresoAgregarHistorial.setVisibility(View.GONE);
                v.setEnabled(true);
                procesando = false;
                Toast.makeText(AgregarHistorialMedicoActivity.this, "Existen campos vacíos", Toast.LENGTH_SHORT).show();
            }

        });

        adaptarVista();

    }

    private boolean validarFormulario() {
        boolean valido = true;

        valido &= controladorUtilidades.validarTextoVacio(
                binding.eTNombreVacuna,
                "Ingrese el nombre"
        );

        valido &= controladorUtilidades.validarTextoVacio(
                binding.eTFechaColocacion,
                "Ingrese la fecha de colocación"
        );

        if (!isVacuna) {
            valido &= controladorUtilidades.validarTextoVacio(
                    binding.eTPesoMascotaDesp,
                    "Ingrese el peso de la mascota"
            );
        }

        return valido;
    }

    private void guardarHistorialMedico() {
        if (isVacuna) {

            HistorialMedico vacuna = new HistorialMedico();
            vacuna.setTipo(EstadosCuentas.VACUNA.toString());
            vacuna.setNombre(binding.eTNombreVacuna.getText().toString());
            vacuna.setFechaColocacion(binding.eTFechaColocacion.getText().toString());
            vacuna.setHoraRecordatorio(binding.eTHoraRecordatorio.getText().toString());
            vacuna.setFechaProxima(binding.eTFechaProximaColocacion.getText().toString());

            controladorMascota.obtenerMascota(idMascota, new ControladorMascota.CallbackMascota<Mascota>() {
                @Override
                public void onComplete(Mascota result) {
                    controladorHistorialMedico.guardarInformacionVacuna(vacuna,
                            result,
                            binding.lLBarraProgresoAgregarHistorial,
                            AgregarHistorialMedicoActivity.this);
                }

                @Override
                public void onError(Exception e) {
                    Log.e("ERROR", "Error al obtener la mascota: " + e.getMessage());
                }
            });

        } else {
            Desparasitacion desparasitacion = new Desparasitacion();
            desparasitacion.setTipo(EstadosCuentas.DESPARASITACION.toString());
            desparasitacion.setNombre(binding.eTNombreVacuna.getText().toString());
            desparasitacion.setFechaColocacion(binding.eTFechaColocacion.getText().toString());
            desparasitacion.setHoraRecordatorio(binding.eTHoraRecordatorio.getText().toString());
            desparasitacion.setFechaProxima(binding.eTFechaProximaColocacion.getText().toString());
            desparasitacion.setPesoMascota(Float.parseFloat(binding.eTPesoMascotaDesp.getText().toString()));
            desparasitacion.setCantidadDesparasitante(Float.parseFloat(binding.eTCantidadDesparasitante.getText().toString()));

            controladorMascota.obtenerMascota(idMascota, new ControladorMascota.CallbackMascota<Mascota>() {
                @Override
                public void onComplete(Mascota result) {
                    controladorHistorialMedico.guardarInformacionDesparasitacion(desparasitacion,
                            result,
                            binding.lLBarraProgresoAgregarHistorial,
                            AgregarHistorialMedicoActivity.this);
                }

                @Override
                public void onError(Exception e) {
                    Log.e("FIRESTORE", "Error al obtener la mascota");
                }
            });
        }
    }

    private void adaptarVista() {
        controladorUtilidades.colocarTimePicker(binding.eTHoraRecordatorio, this);
        if (isVacuna) {
            binding.rLCamposDesparasitacion.setVisibility(View.GONE);

            controladorMascota.obtenerMascota(idMascota, new ControladorMascota.CallbackMascota<Mascota>() {
                @Override
                public void onComplete(Mascota result) {
                    boolean esAdulto = result.getEdadMascota().equalsIgnoreCase(EstadosCuentas.ADULTO.toString());

                    controladorHistorialMedico.obtenerNumeroVacunas(idMascota, new ControladorHistorialMedico.CallbackCantidad() {
                        @Override
                        public void onResultado(int cantidad) {
                            boolean cantidadVacunas = cantidad >= 3;

                            controladorUtilidades.colocarDatePickerVacunas(binding.eTFechaColocacion, binding.eTFechaProximaColocacion,
                                    esAdulto, cantidadVacunas, AgregarHistorialMedicoActivity.this);
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("ERROR", "Error al obtener la cantidad de vacunas del callback");
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    Log.e("Error", "Error al obtener la mascota con el callback");
                }
            });

        } else {
            binding.rLCamposDesparasitacion.setVisibility(View.VISIBLE);

            controladorUtilidades.adaptarCamposPesoCantidad(binding.eTPesoMascotaDesp,
                    binding.eTCantidadDesparasitante);

            controladorUtilidades.colocarDatePickerDesparasitaciones(binding.eTFechaColocacion,
                    binding.eTFechaProximaColocacion,
                    AgregarHistorialMedicoActivity.this);

        }
    }

    private void ocultarTeclado() {
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