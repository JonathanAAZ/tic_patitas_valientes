package com.example.tic_pv.Vista;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.CuentaUsuario;
import com.example.tic_pv.Modelo.Domicilio;
import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.Modelo.Usuario;
import com.example.tic_pv.R;

public class AgregarDomicilioActivity extends AppCompatActivity {
    private EditText paisDomic, parroquiaDomic, barrioDomic, callesDomic;
    private TextView tvProvincia, tvCanton;
    private Spinner provinciaDomic, cantonDomic;
    private boolean procesando;
    String provinciaSeleccionada, cantonSeleccionado;
    private ArrayAdapter <CharSequence> provinciaAdaptador, cantonAdaptador;
    private Usuario usuario;
    private CuentaUsuario cuenta;
    private Domicilio domicilio;
    private ProgressBar barraProgreso;
    private EstadosCuentas estadoObj;
    private Button btnSiguienteDom;
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_domicilio);
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
                    ScrollView scrollView = findViewById(R.id.scrollAgregarDomicilio);
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

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollAgregarDomicilio), (v, insets) -> {
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
//                    findViewById(R.id.scrollAgregarDomicilio).post(() -> {
//                        ((ScrollView) findViewById(R.id.scrollAgregarDomicilio)).smoothScrollBy(0, scrollDistance + 20); // Agrega un margen opcional
//                    });
//                }
//            }
//
//            return insets;
//        });

        paisDomic = findViewById(R.id.paisDomicilio);
        provinciaDomic = findViewById(R.id.spProvinciaDomicilio);
        cantonDomic = findViewById(R.id.spCantonDomicilio);
        parroquiaDomic = findViewById(R.id.parroquiaDomicilio);
        barrioDomic = findViewById(R.id.barrioDomicilio);
        callesDomic = findViewById(R.id.callesDomicilio);
        barraProgreso = findViewById(R.id.barraProgresoAgregarDomicilio);
        btnSiguienteDom = findViewById(R.id.btnSiguienteDom);

        //Definir los TextView para mostrar errores
        tvProvincia = findViewById(R.id.labelProvincia);
        tvCanton = findViewById(R.id.labelCanton);

        Intent i = getIntent();
        usuario = i.getParcelableExtra("usuario");
        cuenta = i.getParcelableExtra("cuenta");


        //Llenar los Spinner con los datos
        provinciaAdaptador = ArrayAdapter.createFromResource(this, R.array.array_provincias_ecuador, R.layout.spinner_layout);

        //Especificar el texto que aparece en el spinner antes de ser seleccionado
        provinciaAdaptador.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

        //Colocar el adaptador dentro del spinner para rellenarlo
        provinciaDomic.setAdapter(provinciaAdaptador);

        //Cuando un item del spinner es seleccionado
        provinciaDomic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvProvincia.setError(null);
                //Se define el spinner de Canton, pero se rellena dependiendo de la provincia seleccionada
                cantonDomic = findViewById(R.id.spCantonDomicilio);

                //Obtener la provincia seleccionada
                provinciaSeleccionada = provinciaDomic.getSelectedItem().toString();
                int idSeleccion = parent.getId();

                if (idSeleccion == R.id.spProvinciaDomicilio) {
                    switch (provinciaSeleccionada) {
                        case "--- Selecciona tu provincia ---": cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_provincias_defecto, R.layout.spinner_layout);
                        break;

                        case "Azuay":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_azuay_cantones, R.layout.spinner_layout);
                        break;

                        case "Bolívar":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_bolivar_cantones, R.layout.spinner_layout);
                            break;

                        case "Cañar":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_canar_cantones, R.layout.spinner_layout);
                            break;

                        case "Carchi":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_carchi_cantones, R.layout.spinner_layout);
                            break;

                        case "Chimborazo":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_chimborazo_cantones, R.layout.spinner_layout);
                            break;

                        case "Cotopaxi":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_cotopaxi_cantones, R.layout.spinner_layout);
                            break;

                        case "El Oro":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_el_oro_cantones, R.layout.spinner_layout);
                            break;

                        case "Esmeraldas":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_esmeraldas_cantones, R.layout.spinner_layout);
                            break;

                        case "Galápagos":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_galapagos_cantones, R.layout.spinner_layout);
                            break;

                        case "Guayas":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_guayas_cantones, R.layout.spinner_layout);
                            break;

                        case "Imbabura":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_imbabura_cantones, R.layout.spinner_layout);
                            break;

                        case "Loja":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_loja_cantones, R.layout.spinner_layout);
                            break;

                        case "Los Ríos":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_los_rios_cantones, R.layout.spinner_layout);
                            break;

                        case "Manabí":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_manabi_cantones, R.layout.spinner_layout);
                            break;

                        case "Morona Santiago":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_morona_santiago_cantones, R.layout.spinner_layout);
                            break;

                        case "Napo":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_napo_cantones, R.layout.spinner_layout);
                            break;

                        case "Orellana":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_orellana_cantones, R.layout.spinner_layout);
                            break;

                        case "Pastaza":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_pastaza_cantones, R.layout.spinner_layout);
                            break;

                        case "Pichincha":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_pichincha_cantones, R.layout.spinner_layout);
                            break;

                        case "Santa Elena":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_santa_elena_cantones, R.layout.spinner_layout);
                            break;

                        case "Santo Domingo de los Tsáchilas":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_santo_domingo_cantones, R.layout.spinner_layout);
                            break;

                        case "Sucumbíos":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_sucumbios_cantones, R.layout.spinner_layout);
                            break;

                        case "Tungurahua":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_tungurahua_cantones, R.layout.spinner_layout);
                            break;

                        case "Zamora Chinchipe":
                            cantonAdaptador = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_zamora_chinchipe_cantones, R.layout.spinner_layout);
                            break;
                        default: break;
                    }

                    cantonAdaptador.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

                    //Llenar los cantones acorde a la provincia seleccionada
                    cantonDomic.setAdapter(cantonAdaptador);

                    //Obtener el canton seleccionado
                    cantonDomic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            tvCanton.setError(null);
                            cantonSeleccionado = cantonDomic.getSelectedItem().toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnSiguienteDom.setOnClickListener(new View.OnClickListener() {
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

                //Obtener todos los datos
                String pais = paisDomic.getText().toString();
                String parroquia = parroquiaDomic.getText().toString();
                String barrio = barrioDomic.getText().toString();
                String calles = callesDomic.getText().toString();

                boolean formularioValido = validarFormulario();

                if (formularioValido) {
                    ocultarTeclado();
                    tvProvincia.setError(null);
                    tvCanton.setError(null);

//                    Toast.makeText(AgregarDomicilioActivity.this, "SELECCION REALIZADA: " + provinciaSeleccionada + cantonSeleccionado, Toast.LENGTH_LONG).show();

                    domicilio = new Domicilio(pais, provinciaSeleccionada, cantonSeleccionado, parroquia, barrio, calles, estadoObj.ACTIVO.toString());

                    barraProgreso.setVisibility(View.VISIBLE);
                    enviarObjs(usuario, cuenta, domicilio);
                } else {
                    ocultarTeclado();
                    barraProgreso.setVisibility(View.GONE);
                    v.setEnabled(true);
                    procesando = false;
                    Toast.makeText(AgregarDomicilioActivity.this, "Existen campos vacíos", Toast.LENGTH_SHORT).show();
                }

//                if (TextUtils.isEmpty(pais)) {
//                    Toast.makeText(AgregarDomicilioActivity.this, "Ingrese un país", Toast.LENGTH_LONG).show();
//                    paisDomic.setError("El país es obligatorio");
//                    paisDomic.requestFocus();
//                } else if (contieneNumeros(pais)) {
//                    Toast.makeText(AgregarDomicilioActivity.this, "No se permiten números", Toast.LENGTH_LONG).show();
//                } else if(provinciaSeleccionada.equals("--- Selecciona tu provincia ---")) {
//                    Toast.makeText(AgregarDomicilioActivity.this, "Seleccione una provincia", Toast.LENGTH_LONG).show();
//                    tvProvincia.setError("La provincia es obligatoria");
//                    tvProvincia.requestFocus();
//                } else if (contieneNumeros(provinciaSeleccionada)) {
//                    Toast.makeText(AgregarDomicilioActivity.this, "No se permiten números", Toast.LENGTH_LONG).show();
//                    tvProvincia.setError("No se pueden ingresar números");
//                    tvProvincia.requestFocus();
//                } else if (cantonSeleccionado.equals("--- Selecciona tu cantón ---")) {
//                    Toast.makeText(AgregarDomicilioActivity.this, "Seleccione un cantón", Toast.LENGTH_LONG).show();
//                    tvCanton.setError("El cantón es obligatorio");
//                    tvCanton.requestFocus();
//                } else if (TextUtils.isEmpty(parroquia)) {
//                    Toast.makeText(AgregarDomicilioActivity.this, "Ingrese una parroquia", Toast.LENGTH_LONG).show();
//                    parroquiaDomic.setError("La parroquita es obligatoria");
//                    parroquiaDomic.requestFocus();
//                } else if (TextUtils.isEmpty(barrio)) {
//                    Toast.makeText(AgregarDomicilioActivity.this, "Ingrese un barrio", Toast.LENGTH_LONG).show();
//                    barrioDomic.setError("El barrio es obligatorio");
//                    barrioDomic.requestFocus();
//                } else if (TextUtils.isEmpty(calles)) {
//                    Toast.makeText(AgregarDomicilioActivity.this, "Ingrese las calles", Toast.LENGTH_LONG).show();
//                    callesDomic.setError("Las calles son obligatorias");
//                    callesDomic.requestFocus();
//                } else {
//
//                }

            }
        });

    }

    private boolean validarFormulario() {
        boolean valido = true;

        //Valores por defecto de los spinners
        String [] arrayProvincias = getResources().getStringArray(R.array.array_provincias_ecuador);
        String provinciaDefecto = arrayProvincias[0];

        String [] arrayCantones = getResources().getStringArray(R.array.array_provincias_defecto);
        String cantonDefecto = arrayCantones[0];

        if(provinciaSeleccionada.equals(provinciaDefecto)) {
            tvProvincia.setError("Seleccione una provincia");
            valido = false;
        }
        if (cantonSeleccionado.equals(cantonDefecto)) {
            tvCanton.setError("Seleccione un cantón");
            valido = false;
        }
        if (!controladorUtilidades.validarTextoVacio(parroquiaDomic, "Ingrese una parroquia")) valido = false;
        if (!controladorUtilidades.validarTextoVacio(barrioDomic, "Ingrese un barrio")) valido = false;
        if (!controladorUtilidades.validarTextoVacio(callesDomic, "Ingrese las calles del domicilio")) valido = false;

        return valido;
    }

    private void enviarObjs (Usuario usuario, CuentaUsuario cuenta, Domicilio domicilio){

        runOnUiThread(()->{

            // Restablecer el estado de procesamiento
            procesando = false;

            Intent i = new Intent(AgregarDomicilioActivity.this, SubirFotoPerfil.class);
            i.putExtra("usuario", usuario);
            i.putExtra("cuenta", cuenta);
            i.putExtra("domicilio", domicilio);
            startActivity(i);

            barraProgreso.setVisibility(View.GONE);
            btnSiguienteDom.setEnabled(true);
//        finish();
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

    public boolean contieneNumeros(String texto) {
        return texto.matches(".*\\d.*");
    }

}