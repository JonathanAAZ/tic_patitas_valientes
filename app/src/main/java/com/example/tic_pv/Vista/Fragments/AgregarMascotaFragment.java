package com.example.tic_pv.Vista.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tic_pv.Controlador.ControladorDomicilio;
import com.example.tic_pv.Controlador.ControladorMascota;
import com.example.tic_pv.Controlador.ControladorUtilidades;
import com.example.tic_pv.Modelo.EstadosCuentas;
import com.example.tic_pv.Modelo.Mascota;
import com.example.tic_pv.R;
import com.example.tic_pv.databinding.FragmentAgregarMascotaBinding;

import java.util.Calendar;
import java.util.Locale;

public class AgregarMascotaFragment extends Fragment {
    private FragmentAgregarMascotaBinding binding;
    private ArrayAdapter <CharSequence> edadAdaptador, generoAdaptador, especieAdaptador;
    private ControladorDomicilio controladorDomicilio = new ControladorDomicilio();
    private ControladorMascota controladorMascota = new ControladorMascota();
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private String edadMascota, especieMascota, sexoMascota;
    private int idVacunacionSeleccionada, idEsterilizacionSeleccionada, idDomicilioMascotaSeleccionado,idDesparasitacionSeleccionada;
    private DatePickerDialog pickerDialog;
    private EstadosCuentas estadoObj;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAgregarMascotaBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //Se llama a este método para llenar los spinners, limpiar errores, colocar date picker, entre otros.
        adaptarVista();

        //Limpiar los Radio Group
        limpiarRadioGroups();

        configurarRadioButtons();

//        configurarInsets(view);

        //Click Listener del botón para continuar la creación de la mascota
        binding.btnContinuarAgregarMascota.setOnClickListener(v ->  {

            ocultarTeclado();

            idVacunacionSeleccionada = binding.rGVacunacion.getCheckedRadioButtonId();
            idEsterilizacionSeleccionada = binding.rGEsterilizacion.getCheckedRadioButtonId();
            idDesparasitacionSeleccionada = binding.rGDesparasitacion.getCheckedRadioButtonId();
            idDomicilioMascotaSeleccionado = binding.rGDomicilioMascota.getCheckedRadioButtonId();

            //Obtener los Radio Button con las opciones seleccionadas
            RadioButton opcionVacunacionSeleccionada = view.findViewById(idVacunacionSeleccionada);
            RadioButton opcionEsterilizacionSeleccionada = view.findViewById(idEsterilizacionSeleccionada);
            RadioButton opcionDesparasitacionSeleccionada = view.findViewById(idDesparasitacionSeleccionada);
            RadioButton opcionDomicilioSeleccionado = view.findViewById(idDomicilioMascotaSeleccionado);

            String nombreMascota = binding.eTNombreMascota.getText().toString();
            String fotoMascota = "Temporal";
            especieMascota = binding.spEspecieMascota.getSelectedItem().toString();
            String razaMascota = binding.eTRazaMascota.getText().toString();
            edadMascota = binding.spEdadMascota.getSelectedItem().toString();
            sexoMascota = binding.spSexoMascota.getSelectedItem().toString();
            String colorMascota = binding.eTColorMascota.getText().toString();
            String caracterMascota = binding.eTCaracterMascota.getText().toString();
            String domicilioMascota = "";
            String fechaEsterilizacion = "00/00/0000";
            Boolean mascotaAdoptada = false;
            Boolean vacunacionMascota = false;
            Boolean esterilizacionMascota = false;
            Boolean desparasitacionMascota = false;
            String estadoMascota = estadoObj.ACTIVO.toString();

            //Valores por defecto de los spinners
            String [] arrayEdad = getResources().getStringArray(R.array.array_edad_mascota);
            String edadMascotaDefecto = arrayEdad[0];

            String [] arrayGenero = getResources().getStringArray(R.array.array_genero_mascota);
            String generoMascotaDefecto = arrayGenero[0];

            String [] arrayEspecie = getResources().getStringArray(R.array.array_especie_mascota);
            String especieMascotaDefecto = arrayEspecie[0];

            boolean formularioValido = validarFormulario();

            if (formularioValido) {
                // Determinar los estados de vacunación, desparasitación y esterilización
                vacunacionMascota = opcionVacunacionSeleccionada.getText().equals(binding.rBOpcionVacunado.getText());
                desparasitacionMascota = opcionDesparasitacionSeleccionada.getText().equals(binding.rBOpcionDesparasitado.getText());
//                esterilizacionMascota = opcionEsterilizacionSeleccionada.equals(binding.rBOpcionEsterilizado.getText());

                if (opcionEsterilizacionSeleccionada.getText().equals(binding.rBOpcionEsterilizado.getText())) {
                    esterilizacionMascota = true;
                    if (!controladorUtilidades.validarTextoVacio(binding.eTFechaEsterilizacionMascota, "Seleccione una fecha de esterilización")) {
                        return;
                    } else {
                        fechaEsterilizacion = binding.eTFechaEsterilizacionMascota.getText().toString();
                    }
                } else {
                    esterilizacionMascota = false;
                }

                if (opcionDomicilioSeleccionado.getText().equals(binding.rBOpcionRefugio.getText())) {
                    domicilioMascota = "Rm7OZnZt5tdUK8kGyisr";
                } else {
                    String nombreVoluntarioDomicilio = binding.spHogarTemporal.getSelectedItem().toString();
                    if (nombreVoluntarioDomicilio.equals("--- Seleccione un voluntario ---")){
                        binding.tVSeleccionarHogar.setError("Por favor seleccione un voluntario");
                        return;
                    } else {
                        domicilioMascota = controladorDomicilio.obtenerDomicilioSeleccionado();
                    }
                }

                // Determinar el domicilio de la mascota
//                domicilioMascota = opcionDomicilioSeleccionado.getText().equals(binding.rBOpcionRefugio.getText())
//                        ? "I4KmbuJjjVyy4mrJqzb3"
//                        : controladorDomicilio.obtenerDomicilioSeleccionado();


                Toast.makeText(requireActivity(), "ESTE ES EL DOMICILIO: " + domicilioMascota, Toast.LENGTH_SHORT).show();

                Mascota mascota = new Mascota(estadoObj.ACTIVO.toString(), fotoMascota, nombreMascota, especieMascota, razaMascota, edadMascota, sexoMascota, colorMascota, caracterMascota, domicilioMascota, fechaEsterilizacion, mascotaAdoptada, vacunacionMascota, esterilizacionMascota, desparasitacionMascota);

                Bundle bundle = new Bundle();
                bundle.putParcelable("mascotaTemporal", mascota);
//                controladorMascota.crearMascota(this.getContext(), mascota, this.getParentFragmentManager());

                AgregarFotoMascotaFragment fragment = new AgregarFotoMascotaFragment();
                fragment.setArguments(bundle);
                controladorUtilidades.reemplazarEntreFragments(R.id.fLFragmentGestionarMascota, requireActivity().getSupportFragmentManager(), fragment);

            } else {
                Toast.makeText(requireContext(), "Existen campos vacíos o sin seleccionar", Toast.LENGTH_SHORT).show();
            }

//            if (TextUtils.isEmpty(nombreMascota)) {
//                Toast.makeText(view.getContext(), "Por favor ingrese el nombre de la mascota", Toast.LENGTH_LONG).show();
//                binding.eTNombreMascota.setError("El nombre de la mascota es obligatorio");
//                binding.eTNombreMascota.requestFocus();
//            } else if (especieMascota.equals(especieMascotaDefecto)) {
//                Toast.makeText(view.getContext(), "Por favor seleccione la especie de la mascota", Toast.LENGTH_LONG).show();
//                binding.tVEspecieMascota.setError("La especie de la mascota es obligatoria");
//                binding.tVEspecieMascota.requestFocus();
//            } else if (TextUtils.isEmpty(razaMascota)) {
//                Toast.makeText(view.getContext(), "Por favor seleccione la raza de la mascota", Toast.LENGTH_LONG).show();
//                binding.tVRazaMascota.setError("La raza de la mascota es obligatoria");
//                binding.tVRazaMascota.requestFocus();
//            }else if (edadMascota.equals(edadMascotaDefecto)) {
//                Toast.makeText(view.getContext(), "Por favor seleccione la edad de la mascota", Toast.LENGTH_LONG).show();
//                binding.tVEdadMascota.setError("La edad de la mascota es obligatoria");
//                binding.tVEdadMascota.requestFocus();
//            } else if (sexoMascota.equals(generoMascotaDefecto)) {
//                Toast.makeText(view.getContext(), "Por favor seleccione el género de la mascota", Toast.LENGTH_SHORT).show();
//                binding.tVSexoMascota.setError("El género de la mascota es obligatorio");
//                binding.tVSexoMascota.requestFocus();
//            } else if (TextUtils.isEmpty(colorMascota)) {
//                Toast.makeText(view.getContext(), "Por favor ingrese el color de la mascota", Toast.LENGTH_SHORT).show();
//                binding.tvColorMascota.setError("El color de la mascota es obligatorio");
//                binding.tvColorMascota.requestFocus();
//            } else if (TextUtils.isEmpty(caracterMascota)) {
//                Toast.makeText(view.getContext(), "Por favor ingrese el caracter que posee la mascota", Toast.LENGTH_SHORT).show();
//                binding.tvCaracterMascota.setError("El carácter de la mascota es obligatorio");
//                binding.tvCaracterMascota.requestFocus();
//            } else if (idVacunacionSeleccionada == -1) {
//                Toast.makeText(view.getContext(), "Por favor seleccione una respuesta", Toast.LENGTH_SHORT).show();
//                binding.tVVacunacionMascota.setError("Debe seleccionar una respuesta");
//                binding.tVVacunacionMascota.requestFocus();
//            }  else if (idDesparasitacionSeleccionada == -1) {
//                Toast.makeText(view.getContext(), "Por favor seleccione una respuesta", Toast.LENGTH_SHORT).show();
//                binding.tVDesparasitacionMascota.setError("Debe seleccionar una respuesta");
//                binding.tVDesparasitacionMascota.requestFocus();
//            } else if (idEsterilizacionSeleccionada == -1) {
//                Toast.makeText(view.getContext(), "Por favor seleccione una respuesta", Toast.LENGTH_SHORT).show();
//                binding.tVEsterilizacionMascota.setError("Debe seleccionar una respuesta");
//                binding.tVEsterilizacionMascota.requestFocus();
//            } else if (idDomicilioMascotaSeleccionado == -1){
//                Toast.makeText(view.getContext(), "Por favor seleccione el domicilio de la mascota", Toast.LENGTH_SHORT).show();
//                binding.tVDomicilioMascota.setError("Debe seleccionar un domicilio");
//                binding.tVDomicilioMascota.requestFocus();
//            } else {
//
////                if (opcionVacunacionSeleccionada.getText().equals(binding.rBOpcionVacunado.getText())) {
////                    vacunacionMascota = true;
////                }
////                if (opcionDesparasitacionSeleccionada.getText().equals(binding.rBOpcionDesparasitado)) {
////                    desparasitacionMascota = true;
////                }
////                if (opcionEsterilizacionSeleccionada.getText().equals(binding.rBOpcionEsterilizado.getText())) {
////                    esterilizacionMascota = true;
////                    fechaEsterilizacion = binding.eTFechaEsterilizacionMascota.getText().toString();
////                }
////                if (opcionDomicilioSeleccionado.getText().equals(binding.rBOpcionRefugio.getText())) {
////                    domicilioMascota = "I4KmbuJjjVyy4mrJqzb3";
////                } else {
////                    domicilioMascota = controladorDomicilio.obtenerDomicilioSeleccionado();
////                }
//
//            }

        });
        return view;
    }

//    private void configurarInsets(View rootView) {
//        // Usa ViewCompat para ajustar el comportamiento de los insets
//        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
//            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//
//            // Ajustar el padding del contenedor principal
//            int bottomPadding = imeInsets.bottom > 0 ? imeInsets.bottom : systemBars.bottom;
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding);
//
//            // Verificar si hay un EditText enfocado
//            View focusedView = getActivity().getCurrentFocus();
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
//                    rootView.post(() -> {
//                        // Si usas un ScrollView en el fragmento, usa la vista raíz
//                        ScrollView scrollView = rootView.findViewById(R.id.main);
//                        if (scrollView != null) {
//                            scrollView.smoothScrollBy(0, scrollDistance + 20); // Agrega un margen opcional
//                        }
//                    });
//                }
//            }
//
//            return insets;
//        });
//    }

    private boolean validarFormulario() {
        boolean valido = true;

        //Valores por defecto de los spinners
        String [] arrayEdad = getResources().getStringArray(R.array.array_edad_mascota);
        String edadMascotaDefecto = arrayEdad[0];

        String [] arrayGenero = getResources().getStringArray(R.array.array_genero_mascota);
        String sexoMascotaDefecto = arrayGenero[0];

        String [] arrayEspecie = getResources().getStringArray(R.array.array_especie_mascota);
        String especieMascotaDefecto = arrayEspecie[0];

        String domicilioDefecto = "--- Selecciona un voluntario ---";

        if (!controladorUtilidades.validarTextoVacio(binding.eTNombreMascota, "Ingrese el nombre de la mascota")) valido = false;
        if (edadMascota.equals(edadMascotaDefecto)) {
            binding.tVEdadMascota.setError("Seleccione la edad de la mascota");
            valido = false;
        }
        if (especieMascota.equals(especieMascotaDefecto)) {
            binding.tVEspecieMascota.setError("Seleccione la especie de la mascota");
            valido = false;
        }
        if (sexoMascota.equals(sexoMascotaDefecto)) {
            binding.tVSexoMascota.setError("Seleccione el sexo de la mascota");
            valido = false;
        }
        if (!controladorUtilidades.validarTextoVacio(binding.eTColorMascota, "Ingrese el color de la mascota")) valido = false;
        if (!controladorUtilidades.validarTextoVacio(binding.eTRazaMascota, "Ingrese la raza de la mascota")) valido = false;
        if (!controladorUtilidades.validarTextoVacio(binding.eTCaracterMascota, "Ingrese el caracter de la mascota")) valido = false;
        if (controladorUtilidades.validarSeleccionRadioGroups(idVacunacionSeleccionada, "Debe seleccionar una respuesta", binding.tVVacunacionMascota)) valido = false;
        if (controladorUtilidades.validarSeleccionRadioGroups(idDesparasitacionSeleccionada, "Debe seleccionar una respuesta", binding.tVDesparasitacionMascota)) valido = false;
        if (controladorUtilidades.validarSeleccionRadioGroups(idEsterilizacionSeleccionada, "Debe seleccionar una respuesta", binding.tVEsterilizacionMascota))valido = false;
        if (controladorUtilidades.validarSeleccionRadioGroups(idDomicilioMascotaSeleccionado, "Debe seleccionar una opción de domicilio", binding.tVDomicilioMascota)) valido = false;
        return valido;
    }

    private void adaptarVista () {

        //Deshabilitar el spinner de hogar por defecto
        binding.tVSeleccionarHogar.setTextColor(Color.GRAY);
        binding.spHogarTemporal.setEnabled(false);
        binding.spHogarTemporal.setAlpha(0.5f);

        //Colocar el DatePicker en el Edit Text
        controladorUtilidades.colocarDatePicker(binding.eTFechaEsterilizacionMascota, getContext());

        //Deshabilitar el Date Picker por defecto
        binding.tVFechaEsterilizacionMascota.setTextColor(Color.GRAY);
        binding.eTFechaEsterilizacionMascota.setEnabled(false);
        binding.eTFechaEsterilizacionMascota.setAlpha(0.5f);

        //Llenar los spinner con los datos
        edadAdaptador = ArrayAdapter.createFromResource(requireContext(), R.array.array_edad_mascota, R.layout.spinner_layout);
        generoAdaptador = ArrayAdapter.createFromResource(requireContext(), R.array.array_genero_mascota, R.layout.spinner_layout);
        especieAdaptador = ArrayAdapter.createFromResource(requireContext(), R.array.array_especie_mascota, R.layout.spinner_layout);

        //Especificar el texto que aparece en el spinner antes de ser seleccionado
        edadAdaptador.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item);
        generoAdaptador.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item);
        especieAdaptador.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        controladorDomicilio.cargarVoluntariosSpinner(binding.spHogarTemporal, binding.tVSeleccionarHogar);

        //Colocar el adaptador dentro del spinner para rellenarlo
        binding.spEdadMascota.setAdapter(edadAdaptador);
        binding.spSexoMascota.setAdapter(generoAdaptador);
        binding.spEspecieMascota.setAdapter(especieAdaptador);

        //Limpiar los errores de los TextViews relacionados con los spinners
        controladorUtilidades.limpiarErroresSpinners(binding.spEdadMascota, binding.tVEdadMascota);
        controladorUtilidades.limpiarErroresSpinners(binding.spSexoMascota, binding.tVSexoMascota);
        controladorUtilidades.limpiarErroresSpinners(binding.spEspecieMascota, binding.tVEspecieMascota);
//        controladorUtilidades.limpiarErroresSpinners(binding.spHogarTemporal, binding.tVSeleccionarHogar);

        //Limpiar los errores de los TextView relacionados con los Radio Groups
        controladorUtilidades.limpiarErroresRadioGroup(binding.rGVacunacion, binding.tVVacunacionMascota);
        controladorUtilidades.limpiarErroresRadioGroup(binding.rGEsterilizacion, binding.tVEsterilizacionMascota);
        controladorUtilidades.limpiarErroresRadioGroup(binding.rGDesparasitacion, binding.tVDesparasitacionMascota);
        controladorUtilidades.limpiarErroresRadioGroup(binding.rGDomicilioMascota, binding.tVDomicilioMascota);
    }

    private void limpiarRadioGroups () {
        binding.rGVacunacion.clearCheck();
        binding.rGEsterilizacion.clearCheck();
        binding.rGDesparasitacion.clearCheck();
        binding.rGDomicilioMascota.clearCheck();
    }

    private void configurarRadioButtons() {
        //Detectar los cambios en el estado del Radio Button
        RadioButton rBHogarTemporal = binding.rBOpcionHogarTemporal;
        rBHogarTemporal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.tVSeleccionarHogar.setTextColor(Color.BLACK);
                binding.spHogarTemporal.setEnabled(true); // Habilitar Spinner
                binding.spHogarTemporal.setAlpha(1f); // Restaurar opacidad
            } else {
                binding.tVSeleccionarHogar.setTextColor(Color.GRAY);
                binding.spHogarTemporal.setEnabled(false); // Deshabilitar Spinner
                binding.spHogarTemporal.setAlpha(0.5f); // Reducir opacidad
            }
        });

        //Detectar los cambios en el estado del Radio Button de Esterilizacion
        binding.rBOpcionEsterilizado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.tVFechaEsterilizacionMascota.setTextColor(Color.BLACK);
                binding.eTFechaEsterilizacionMascota.setEnabled(true); // Habilitar Spinner
                binding.eTFechaEsterilizacionMascota.setAlpha(1f); // Restaurar opacidad
            } else {
                binding.tVFechaEsterilizacionMascota.setTextColor(Color.BLACK);
                binding.eTFechaEsterilizacionMascota.setEnabled(false); // Deshabilitar Spinner
                binding.eTFechaEsterilizacionMascota.setAlpha(0.5f); // Reducir opacidad
            }
        });
    }

    private void ocultarTeclado() {
        // Obtener el servicio InputMethodManager
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        // Ocultar el teclado
        if (imm != null) {
            View currentFocus = requireActivity().getCurrentFocus();
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
        }
    }

}