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
import com.example.tic_pv.databinding.ActivityEditarHistorialMedicoBinding;
import com.example.tic_pv.databinding.ActivityEditarMascotaBinding;

public class EditarHistorialMedicoActivity extends AppCompatActivity {
    private String idVacuna, idMascota;
    private final ControladorHistorialMedico controladorHistorialMedico = new ControladorHistorialMedico();
    private final ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private final ControladorMascota controladorMascota = new ControladorMascota();
    private boolean procesando, isVacuna;
    private String fechaOriginal, horaOriginal;
    private ActivityEditarHistorialMedicoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_historial_medico);
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
                    ScrollView scrollView = findViewById(R.id.scrollEditarHistorial);
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

        Intent intent = getIntent();
        idVacuna = intent.getStringExtra("idVacuna");
        idMascota = intent.getStringExtra("idMascota");
        isVacuna = intent.getBooleanExtra("isVacuna", true);


        binding = ActivityEditarHistorialMedicoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        if (isVacuna) {
            adaptarVistaVacunas();
        } else {
            adaptarVistaDesparasitaciones();
        }
    }

    private void adaptarVistaDesparasitaciones () {
        binding.rLEditarCamposDesparasitacion.setVisibility(View.VISIBLE);
        controladorUtilidades.adaptarCamposPesoCantidad(binding.eTEditarPesoMascotaDesp,
                binding.eTEditarCantidadDesparasitante);

        controladorUtilidades.colocarDatePickerDesparasitaciones(binding.eTEditarFechaColocacion,
                binding.eTEditarFechaProximaColocacion,
                EditarHistorialMedicoActivity.this);
        // Configurar el alert dialog
        Dialog alerta = controladorUtilidades.crearAlertaPersonalizada("MENSAJE DE CONFIRMACIÓN", "¿Seguro/a que desea guardar la información editada?", this);
        LinearLayout btnAceptar = alerta.findViewById(R.id.lLbtnAceptar);
        LinearLayout btnCancelar = alerta.findViewById(R.id.lLbtnCancelar);
        btnAceptar.setVisibility(View.VISIBLE);
        btnCancelar.setVisibility(View.VISIBLE);

        controladorUtilidades.colocarTimePicker(binding.eTEditarHoraRecordatorio, this);

        controladorHistorialMedico.obtenerDesparasitacion(idVacuna, idMascota, new ControladorHistorialMedico.Callback<Desparasitacion>() {
            @Override
            public void onComplete(Desparasitacion result) {

                binding.eTEditarNombreVacuna.setText(result.getNombre());
                binding.eTEditarFechaColocacion.setText(result.getFechaColocacion());
                binding.eTEditarHoraRecordatorio.setText(result.getHoraRecordatorio());
                binding.eTEditarFechaProximaColocacion.setText(result.getFechaProxima());
                binding.eTEditarPesoMascotaDesp.setText(String.valueOf(result.getPesoMascota()));
                binding.eTEditarCantidadDesparasitante.setText(String.valueOf(result.getCantidadDesparasitante()));

                binding.btnGuardarEditarHistorial.setOnClickListener(v -> {

                    // Configurar los botones de la alerta
                    btnAceptar.setOnClickListener(view -> {
                        editarInformacionHistorial(isVacuna, result.getFechaColocacion(), result.getHoraRecordatorio());
                    });

                    btnCancelar.setOnClickListener(view -> {
                        alerta.dismiss();
                        v.setEnabled(true);
                        procesando = false;
                    });

                    binding.barraProgresoEditarHistorial.setVisibility(View.VISIBLE);
                    //Verificar si ya está en proceso
                    if (procesando) {
                        return; //Salir si ya está procesando
                    }
                    //Marcar como "Procesando"
                    procesando = true;

                    //Deshabilitar el botón inmediantamente
                    v.setEnabled(false);
                    boolean formularioValido = validarFormulario(isVacuna);

                    if (formularioValido) {
                        alerta.create();
                        alerta.show();
                    } else {
                        ocultarTeclado();
                        binding.barraProgresoEditarHistorial.setVisibility(View.GONE);
                        v.setEnabled(true);
                        procesando = false;
                        Toast.makeText(EditarHistorialMedicoActivity.this, "Existen campos vacíos", Toast.LENGTH_SHORT).show();
                    }

                });
            }

            @Override
            public void onError(Exception e) {
                Log.e("FIREBASE", "Error al obtener la vacuna con callback");
            }
        });
    }

    private void adaptarVistaVacunas() {
        binding.rLEditarCamposDesparasitacion.setVisibility(View.GONE);
        // Configurar el alert dialog
        Dialog alerta = controladorUtilidades.crearAlertaPersonalizada("MENSAJE DE CONFIRMACIÓN", "¿Seguro/a que desea guardar la información editada?", this);
        LinearLayout btnAceptar = alerta.findViewById(R.id.lLbtnAceptar);
        LinearLayout btnCancelar = alerta.findViewById(R.id.lLbtnCancelar);
        btnAceptar.setVisibility(View.VISIBLE);
        btnCancelar.setVisibility(View.VISIBLE);

        controladorUtilidades.colocarTimePicker(binding.eTEditarHoraRecordatorio, this);

        controladorHistorialMedico.obtenerVacuna(idVacuna, idMascota, new ControladorHistorialMedico.Callback<HistorialMedico>() {
            @Override
            public void onComplete(HistorialMedico result) {

                binding.eTEditarNombreVacuna.setText(result.getNombre());
                binding.eTEditarFechaColocacion.setText(result.getFechaColocacion());
                binding.eTEditarHoraRecordatorio.setText(result.getHoraRecordatorio());
                binding.eTEditarFechaProximaColocacion.setText(result.getFechaProxima());

                binding.btnGuardarEditarHistorial.setOnClickListener(v -> {

                    // Configurar los botones de la alerta
                    btnAceptar.setOnClickListener(view -> {
                        editarInformacionHistorial(isVacuna, result.getFechaColocacion(), result.getHoraRecordatorio());
                    });

                    btnCancelar.setOnClickListener(view -> {
                        alerta.dismiss();
                        v.setEnabled(true);
                        procesando = false;
                    });

                    binding.barraProgresoEditarHistorial.setVisibility(View.VISIBLE);
                    //Verificar si ya está en proceso
                    if (procesando) {
                        return; //Salir si ya está procesando
                    }
                    //Marcar como "Procesando"
                    procesando = true;

                    //Deshabilitar el botón inmediantamente
                    v.setEnabled(false);
                    boolean formularioValido = validarFormulario(isVacuna);

                    if (formularioValido) {
                        alerta.create();
                        alerta.show();
                    } else {
                        ocultarTeclado();
                        binding.barraProgresoEditarHistorial.setVisibility(View.GONE);
                        v.setEnabled(true);
                        procesando = false;
                        Toast.makeText(EditarHistorialMedicoActivity.this, "Existen campos vacíos", Toast.LENGTH_SHORT).show();
                    }

                });

                controladorMascota.obtenerMascota(idMascota, new ControladorMascota.CallbackMascota<Mascota>() {
                    @Override
                    public void onComplete(Mascota result) {
                        boolean esAdulto = result.getEdadMascota().equalsIgnoreCase(EstadosCuentas.ADULTO.toString());

                        controladorHistorialMedico.obtenerNumeroVacunas(idMascota, new ControladorHistorialMedico.CallbackCantidad() {
                            @Override
                            public void onResultado(int cantidad) {
                                boolean cantidadVacunas = cantidad >= 3;

                                controladorUtilidades.colocarDatePickerVacunas(binding.eTEditarFechaColocacion, binding.eTEditarFechaProximaColocacion, esAdulto, cantidadVacunas, EditarHistorialMedicoActivity.this);
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("FIREBASE", "Error al obtener la cantidad de vacunas disponibles.");
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("FIREBASE", "Error al obtener la mascota.");
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e("FIREBASE", "Error al obtener la vacuna con callback");
            }
        });
    }

    private boolean validarFormulario(boolean esVacuna) {
        boolean valido = true;

        valido &= controladorUtilidades.validarTextoVacio(binding.eTEditarNombreVacuna, "Ingrese el nombre");

        valido &= controladorUtilidades.validarTextoVacio(binding.eTEditarFechaColocacion, "Ingrese la fecha de colocación");

        if (!esVacuna) {
            valido &= controladorUtilidades.validarTextoVacio(binding.eTEditarPesoMascotaDesp, "Ingrese el peso de la mascota");
        }

        return valido;
    }

    private void editarInformacionHistorial(boolean isVacuna, String fechaOriginal, String horaOriginal) {
        if (isVacuna) {
            HistorialMedico vacuna = new HistorialMedico();
            vacuna.setId(idVacuna);
            vacuna.setTipo(EstadosCuentas.VACUNA.toString());
            vacuna.setNombre(binding.eTEditarNombreVacuna.getText().toString());
            vacuna.setFechaColocacion(binding.eTEditarFechaColocacion.getText().toString());
            vacuna.setHoraRecordatorio(binding.eTEditarHoraRecordatorio.getText().toString());
            vacuna.setFechaProxima(binding.eTEditarFechaProximaColocacion.getText().toString());

            controladorMascota.obtenerMascota(idMascota, new ControladorMascota.CallbackMascota<Mascota>() {
                @Override
                public void onComplete(Mascota result) {
                    controladorHistorialMedico.editarVacuna(vacuna, result, fechaOriginal, horaOriginal,EditarHistorialMedicoActivity.this, binding.lLBarraProgresoEditarHistorial);
                }

                @Override
                public void onError(Exception e) {
                    Log.e("FIREBASE", "Error al obtener la mascota.");
                }
            });

        } else {
            Desparasitacion desparasitacion = new Desparasitacion();
            desparasitacion.setId(idVacuna);
            desparasitacion.setTipo(EstadosCuentas.DESPARASITACION.toString());
            desparasitacion.setNombre(binding.eTEditarNombreVacuna.getText().toString());
            desparasitacion.setFechaColocacion(binding.eTEditarFechaColocacion.getText().toString());
            desparasitacion.setHoraRecordatorio(binding.eTEditarHoraRecordatorio.getText().toString());
            desparasitacion.setFechaProxima(binding.eTEditarFechaProximaColocacion.getText().toString());
            desparasitacion.setPesoMascota(Float.parseFloat(binding.eTEditarPesoMascotaDesp.getText().toString()));
            desparasitacion.setCantidadDesparasitante(Float.parseFloat(binding.eTEditarCantidadDesparasitante.getText().toString()));

            controladorMascota.obtenerMascota(idMascota, new ControladorMascota.CallbackMascota<Mascota>() {
                @Override
                public void onComplete(Mascota result) {
                    controladorHistorialMedico.editarDesparasitacion(desparasitacion, result, fechaOriginal, horaOriginal, EditarHistorialMedicoActivity.this, binding.lLBarraProgresoEditarHistorial);
                }

                @Override
                public void onError(Exception e) {
                    Log.e("FIREBASE", "Error al obtener la mascota.");
                }
            });

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