package com.example.tic_pv.Controlador;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.utils.ObjectUtils;
import com.example.tic_pv.Modelo.VideoCache;
import com.example.tic_pv.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ControladorUtilidades {
    private final Map<String, Boolean> estadoValidaciones = new HashMap<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Runnable actualizarBotonCallback;
    private boolean videoCargado = false;
    private boolean reproduciendo = false;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public Map<String, Boolean> getEstadoValidaciones() {
        return estadoValidaciones;
    }

    public void setActualizarBotonCallback(Runnable callback) {
        this.actualizarBotonCallback = callback;
    }

    public void reemplazarFragments (int id, FragmentManager fragmentManager, Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(id, fragment);
        fragmentTransaction.commit();
    }

    public void reemplazarEntreFragments(int id, FragmentManager fragmentManager, Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(id, fragment)
                .addToBackStack(null) // Permite volver al fragmento anterior con el botón de retroceso
                .commit();
    }

    public void cerrarFragment(FragmentManager fragmentManager, String nombreFragment) {
        Fragment fragment = fragmentManager.findFragmentByTag(nombreFragment);
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }

    public void limpiarErroresRadioGroup (RadioGroup rg, TextView tv) {
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                tv.setError(null);
            }
        });
    }

    public void limpiarErroresSpinners (Spinner sp, TextView tv) {
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tv.setError(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void colocarDatePicker(EditText editText, Context context) {
        // Variable para almacenar la última fecha seleccionada
        final Calendar fechaSeleccionada = Calendar.getInstance();

        editText.setOnClickListener(v -> {
            // Establecer Locale en español
            Locale locale = new Locale("es", "ES");
            Locale.setDefault(locale);
            Configuration config = context.getResources().getConfiguration();
            config.setLocale(locale);
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

            // Si ya hay una fecha seleccionada, usarla; de lo contrario, usar la fecha actual
            int dia, mes, anio;
            if (!editText.getText().toString().isEmpty()) {
                String[] partesFecha = editText.getText().toString().split("/");
                dia = Integer.parseInt(partesFecha[0]);
                mes = Integer.parseInt(partesFecha[1]) - 1; // Meses en Calendar comienzan en 0
                anio = Integer.parseInt(partesFecha[2]);
            } else {
                dia = fechaSeleccionada.get(Calendar.DAY_OF_MONTH);
                mes = fechaSeleccionada.get(Calendar.MONTH);
                anio = fechaSeleccionada.get(Calendar.YEAR);
            }

            // Crear el objeto Calendar para la fecha actual (límite máximo)
            Calendar fechaLimite = Calendar.getInstance();

            // Crear y mostrar el DatePickerDialog
            DatePickerDialog picker = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                fechaSeleccionada.set(year, month, dayOfMonth);
                @SuppressLint("DefaultLocale")
                String fechaFormateada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                editText.setText(fechaFormateada);
            }, anio, mes, dia);

            // Establecer el límite máximo en la fecha actual (no se puede seleccionar una fecha futura)
            picker.getDatePicker().setMaxDate(fechaLimite.getTimeInMillis());

            picker.show();
            editText.setError(null);
        });
    }

    // Permite colocar el date picker para seleccionar la fecha, y tiene la lógica para definir la
    // fecha de la próxima vacuna, cada mes o anualmente dependiendo de la condición
    public void colocarDatePickerVacunas(EditText etColocacion,
                                         EditText etProxima,
                                         boolean esAdulto,
                                         boolean cantidadVacunas,
                                         Context context) {

        final Calendar fechaSeleccionada = Calendar.getInstance();

        etColocacion.setOnClickListener(v -> {

            // Configurar locale en español
            Locale locale = new Locale("es", "ES");
            Locale.setDefault(locale);
            Configuration config = context.getResources().getConfiguration();
            config.setLocale(locale);
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

            int dia, mes, anio;

            if (!etColocacion.getText().toString().isEmpty()) {
                String[] partes = etColocacion.getText().toString().split("/");
                dia = Integer.parseInt(partes[0]);
                mes = Integer.parseInt(partes[1]) - 1;
                anio = Integer.parseInt(partes[2]);
            } else {
                dia = fechaSeleccionada.get(Calendar.DAY_OF_MONTH);
                mes = fechaSeleccionada.get(Calendar.MONTH);
                anio = fechaSeleccionada.get(Calendar.YEAR);
            }

            DatePickerDialog picker = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {

                // Asignar fecha en el primer EditText
                fechaSeleccionada.set(year, month, dayOfMonth);
                @SuppressLint("DefaultLocale") String fechaFormateada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                etColocacion.setText(fechaFormateada);

                // Calcular fecha próxima según condición
                Calendar fechaProx = Calendar.getInstance();
                fechaProx.set(year, month, dayOfMonth);

                if (esAdulto || cantidadVacunas) {
                    fechaProx.add(Calendar.YEAR, 1);   // adulto → +1 año
                } else {
                    fechaProx.add(Calendar.MONTH, 1);  // cachorro → +1 mes
                }

                // Formateo
                @SuppressLint("DefaultLocale") String fechaProximaFormateada = String.format("%02d/%02d/%04d",
                        fechaProx.get(Calendar.DAY_OF_MONTH),
                        fechaProx.get(Calendar.MONTH) + 1,
                        fechaProx.get(Calendar.YEAR));

                // Asignar fecha próxima
                etProxima.setText(fechaProximaFormateada);

            }, anio, mes, dia);

            picker.show();

            etColocacion.setError(null);
        });
    }

    public void colocarDatePickerDesparasitaciones(EditText etColocacion,
                                         EditText etProxima,
                                         Context context) {

        final Calendar fechaSeleccionada = Calendar.getInstance();

        etColocacion.setOnClickListener(v -> {

            // Configurar locale en español
            Locale locale = new Locale("es", "ES");
            Locale.setDefault(locale);
            Configuration config = context.getResources().getConfiguration();
            config.setLocale(locale);
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

            int dia, mes, anio;

            if (!etColocacion.getText().toString().isEmpty()) {
                String[] partes = etColocacion.getText().toString().split("/");
                dia = Integer.parseInt(partes[0]);
                mes = Integer.parseInt(partes[1]) - 1;
                anio = Integer.parseInt(partes[2]);
            } else {
                dia = fechaSeleccionada.get(Calendar.DAY_OF_MONTH);
                mes = fechaSeleccionada.get(Calendar.MONTH);
                anio = fechaSeleccionada.get(Calendar.YEAR);
            }

            DatePickerDialog picker = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {

                // Asignar fecha en el primer EditText
                fechaSeleccionada.set(year, month, dayOfMonth);
                @SuppressLint("DefaultLocale") String fechaFormateada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                etColocacion.setText(fechaFormateada);

                // Calcular fecha próxima según condición
                Calendar fechaProx = Calendar.getInstance();
                fechaProx.set(year, month, dayOfMonth);

                fechaProx.add(Calendar.MONTH, 6);  // cada 6 meses (semestral)

                // Formateo
                @SuppressLint("DefaultLocale") String fechaProximaFormateada = String.format("%02d/%02d/%04d",
                        fechaProx.get(Calendar.DAY_OF_MONTH),
                        fechaProx.get(Calendar.MONTH) + 1,
                        fechaProx.get(Calendar.YEAR));

                // Asignar fecha próxima
                etProxima.setText(fechaProximaFormateada);

            }, anio, mes, dia);

            picker.show();

            etColocacion.setError(null);
        });
    }

    public void adaptarCamposPesoCantidad(EditText pesoMascota, EditText cantidadDesparasitante) {
        pesoMascota.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se utiliza
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Validar si el texto no está vacío para evitar errores
                if (!s.toString().isEmpty()) {
                    try {
                        // Convertir el texto ingresado en el editTextOrigen a un entero
                        double libras = Double.parseDouble(s.toString());

                        // Calcular los centímetros redondeando
                        int centimetros = (int) Math.round(libras / 10.0);

                        // Mostrar el valor en editTextDestino
                        cantidadDesparasitante.setText(String.valueOf(centimetros));
                    } catch (NumberFormatException e) {
                        Log.e("ERROR", "Número mal ingresado");
                    }
                } else {
                    // Si no hay texto, limpiar el editTextDestino
                    cantidadDesparasitante.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No se utiliza
            }
        });
    }

    public void colocarFechaEnDatePicker (EditText editText, String fechaDB, Context context) {
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Establecer el locale en español
                Locale locale = new Locale("es", "ES");
                Locale.setDefault(locale);
                Configuration config = context.getResources().getConfiguration();
                config.setLocale(locale);
                context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

                // Parsear la fecha del String proporcionado
                final Calendar calendario = Calendar.getInstance();
                try {
                    SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy", locale);
                    Date fecha = formatoFecha.parse(fechaDB);
                    if (fecha != null) {
                        calendario.setTime(fecha);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    // Si hay un error en el parseo, usar la fecha actual
                    calendario.setTimeInMillis(System.currentTimeMillis());
                }

                int dia = calendario.get(Calendar.DAY_OF_MONTH);
                int mes = calendario.get(Calendar.MONTH);
                int anio = calendario.get(Calendar.YEAR);

                // Ventana con DatePicker
                DatePickerDialog pickerDialog = null;
                pickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        @SuppressLint("DefaultLocale") String fechaFormateada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                        editText.setText(fechaFormateada);
                    }
                }, anio, mes, dia);

                pickerDialog.show();
            }
        });
    }

    public boolean validadorDeCedula(String cedula) {
        boolean cedulaCorrecta = false;

        try {
            // Verificar que la cédula tenga 10 caracteres
            if (cedula.length() == 10) {
                int tercerDigito = Integer.parseInt(cedula.substring(2, 3));

                // Verificar que el tercer dígito sea menor que 6
                if (tercerDigito < 6) {
                    // Coeficientes de validación de la cédula
                    int[] coefValCedula = { 2, 1, 2, 1, 2, 1, 2, 1, 2 };
                    int verificador = Integer.parseInt(cedula.substring(9, 10));
                    int suma = 0;
                    int digito = 0;

                    // Calcular la suma de los productos de cada dígito por su coeficiente
                    for (int i = 0; i < (cedula.length() - 1); i++) {
                        digito = Integer.parseInt(cedula.substring(i, i + 1)) * coefValCedula[i];
                        suma += ((digito % 10) + (digito / 10));
                    }

                    // Validar si el dígito verificador es correcto
                    if ((suma % 10 == 0 && suma % 10 == verificador) || (10 - (suma % 10)) == verificador) {
                        cedulaCorrecta = true;
                    } else {
                        cedulaCorrecta = false;
                    }
                } else {
                    cedulaCorrecta = false;
                }
            } else {
                cedulaCorrecta = false;
            }
        } catch (NumberFormatException nfe) {
            cedulaCorrecta = false;
        } catch (Exception err) {
            System.out.println("Una excepción ocurrió en el proceso de validación");
            cedulaCorrecta = false;
        }

//        if (!cedulaCorrecta) {
//            System.out.println("La Cédula ingresada es Incorrecta");
//        }

        return cedulaCorrecta;
    }

    public void validarClaveYConfirmacionConjuntamente(Context context, EditText nuevaClave, EditText confirmacionClave,
                                                       TextView tVMinimoCaracteres, TextView tVMinusculas,
                                                       TextView tVMayusculas, TextView tVNumero,
                                                       TextView tVCaracterEspecial, TextView tVError, String keyClave, String keyConfirmacion) {
        // Inicializar estados de validación como no válidos
        estadoValidaciones.put(keyClave, false);
        estadoValidaciones.put(keyConfirmacion, false);

        // TextWatcher compartido para ambos campos
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String clave = nuevaClave.getText().toString();
                String confirmacion = confirmacionClave.getText().toString();

                // Validaciones de la nueva clave
                boolean longitud = validarClaveTiempoReal(clave, tVMinimoCaracteres, ".*.{8,}", context);
                boolean minusculas = validarClaveTiempoReal(clave, tVMinusculas, ".*[a-z].*", context);
                boolean mayusculas = validarClaveTiempoReal(clave, tVMayusculas, ".*[A-Z].*", context);
                boolean numeros = validarClaveTiempoReal(clave, tVNumero, ".*[0-9].*", context);
                boolean caracterEspecial = validarClaveTiempoReal(clave, tVCaracterEspecial, ".*[^a-zA-Z0-9].*", context);

                // Validación conjunta de clave y confirmación
                boolean clavesCoinciden = clave.equals(confirmacion);

                // Actualizar los estados de validación
                estadoValidaciones.put(keyClave, longitud && minusculas && mayusculas && numeros && caracterEspecial);
                estadoValidaciones.put(keyConfirmacion, clavesCoinciden);

                // Mostrar error si las claves no coinciden
                if (!clavesCoinciden && !confirmacion.isEmpty()) {
                    tVError.setVisibility(View.VISIBLE);
                } else {
                    tVError.setVisibility(View.GONE);
                }

                // Actualizar el botón si las validaciones son correctas
                if (actualizarBotonCallback != null) {
                    actualizarBotonCallback.run();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        // Asignar el TextWatcher a ambos campos
        nuevaClave.addTextChangedListener(watcher);
        confirmacionClave.addTextChangedListener(watcher);
    }

    private boolean validarClaveTiempoReal(String clave, TextView textView, String regex, Context context) {
        if (clave.matches(regex)) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.light_green)); // Criterio cumplido
            return true;
        } else {
            textView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light)); // Criterio no cumplido
            return false;
        }
    }


    public boolean validarNumeroCelular(String numeroCelular) {
        // Expresión regular para validar números de celular en Ecuador
        String regex = "^((\\+593|0)([2-7]|9[1-9]))\\d{7,8}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(numeroCelular);

        return matcher.matches();
    }

    public boolean validarTextoVacio(EditText editText, String mensajeError) {
        String texto = editText.getText().toString().trim();
        if (TextUtils.isEmpty(texto)) {
            editText.setError(mensajeError);
            return false;
        }
        return true;
    }

    public boolean validarCorreo(EditText editText) {
        String correo = editText.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
//            editText.setError("Por favor, ingresa un correo válido");
            return false;
        }
        return true;
    }

    public boolean validarEstadoCiv(RadioGroup radioGroup, TextView textView) {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            textView.setError("El estado civil es obligatorio");
            return false;
        }
        return true;
    }

    public boolean validarCedula(EditText editText, ControladorUtilidades controladorUtilidades) {
        String cedula = editText.getText().toString().trim();
        if (!controladorUtilidades.validadorDeCedula(cedula)) {
//            editText.setError("Cédula no válida");
            return false;
        }
        return true;
    }

    public boolean validarTelefono(EditText editText, ControladorUtilidades controladorUtilidades) {
        String telefono = editText.getText().toString().trim();
        if (!controladorUtilidades.validarNumeroCelular(telefono)) {
//            editText.setError("El número debe tener 10 dígitos");
            return false;
        }
        return true;
    }

    public boolean validarSeleccionRadioGroups(int seleccion, String mensaje, TextView tVError) {
        if (seleccion == -1) {
            tVError.setError(mensaje);
            return true;
        }
        return false;
    }

    public void agregarValidacionEnTiempoReal(EditText editText, Predicate<String> validador, TextView textView, String key) {

        estadoValidaciones.put(key, false);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                boolean esValido = validador.test(text);
                estadoValidaciones.put(key, esValido);
                if (!esValido) {
                    textView.setVisibility(View.VISIBLE);
                } else {
                    textView.setVisibility(View.GONE);
                }
                if (actualizarBotonCallback != null) {
                    actualizarBotonCallback.run();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
//                if (!validador.apply(editable.toString())) {
//                    textView.setVisibility(View.VISIBLE);
////                    editText.setError(mensajeError);
//                } else {
//                    textView.setVisibility(View.GONE);
//                }
            }
        });
    }


    public void configurarBotonesVerClave (ImageView imageView, EditText editText) {
        imageView.setImageResource(R.drawable.ic_ocultar_clave);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //Si la contraseña es visible, se oculta
                    editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Se cambia el ícono
                    imageView.setImageResource(R.drawable.ic_ocultar_clave);
                } else {
                    editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageView.setImageResource(R.drawable.ic_ver_clave);
                }

                editText.setSelection(editText.getText().length());
            }
        });
    }

    public String obtenerFechaActual() {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

        return formato.format(Calendar.getInstance().getTime());
    }

    public void insertarImagenDesdeBDD(String url, ImageView imageView, Context context) {
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.ic_cargando)
                .fitCenter()
                .into(imageView);
    }

    public void insertarVideoDesdeBDD(String url, VideoView videoView, ProgressBar barraProgreso, ImageView imageView) {

        if (!videoCargado) {
            videoView.setVisibility(View.VISIBLE);
            barraProgreso.setVisibility(View.VISIBLE);

            VideoCache.getVideo(videoView.getContext(), url, uri -> {
                videoView.setVideoURI(uri);
                videoCargado = true;

                // Configurar el reproductor cuando el video está listo
                videoView.setOnPreparedListener(mp -> {
                    barraProgreso.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    // Iniciar la reproducción
//                    videoView.start();
                });

                videoView.setOnClickListener(v -> {
                    if (reproduciendo) {
                        videoView.pause();
                        imageView.setVisibility(View.VISIBLE);
                    } else {
                        videoView.start();
                        imageView.setVisibility(View.INVISIBLE);
                    }
                    reproduciendo = !reproduciendo;
                });

                // Reiniciar el video cuando termina, sin recargar
                videoView.setOnCompletionListener(mp -> {
                    reproduciendo = !reproduciendo;
                    imageView.setVisibility(View.VISIBLE);
                    videoView.seekTo(0); // Regresar al inicio sin recargar
//                    videoView.start(); // Volver a reproducir inmediatamente
                });
            });
        }

    }

    public String convertirFecha(String fechaString) {
        try {
            // Formato de entrada (dd/MM/yyyy)
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            // Convertir String a Date
            Date fecha = formatoEntrada.parse(fechaString);

            // Formato de salida (día, dd de mes de yyyy)
            SimpleDateFormat formatoSalida = new SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));

            // Retornar la fecha formateada con la primera letra en mayúscula
            String fechaFormateada = formatoSalida.format(fecha);
            return fechaFormateada.substring(0, 1).toUpperCase() + fechaFormateada.substring(1);

        } catch (ParseException e) {
            e.printStackTrace();
            return "Fecha inválida"; // En caso de error
        }
    }

    public void configurarColoresBotones(LinearLayout btnSeleccionado, LinearLayout btnNoSeleccionado) {
        btnSeleccionado.setBackgroundResource(R.color.dashboard_blue);
        btnNoSeleccionado.setBackgroundResource(R.color.blue_2);
    }

    public String reemplazarPalabraString (String original, String palabra, String reemplazo) {
        return original.replace(palabra, reemplazo);
    }

    public Uri obtenerUriDeBitmap(Bitmap bitmap, Context context) {
        try {
            File archivoTemp = new File(context.getCacheDir(), "imagen_recortada_" + System.currentTimeMillis() + ".jpg");

            FileOutputStream out = new FileOutputStream(archivoTemp);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            return FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".fileprovider",
                    archivoTemp
            );
        } catch (IOException e) {
            Log.e("ERROR", "Error al obtener la URI del Bitmap: " + e.getMessage());
            return null;
        }
    }

    public SpannableStringBuilder ponerTextoNegrita(String titulo, String contenido) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(titulo + contenido);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, titulo.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    public void colocarTextoEnTextView(SpannableStringBuilder texto, TextView textView) {
        textView.setText(texto);
    }

    public Dialog crearAlertaPersonalizada(String titulo, String mensaje, Context context) {
        Dialog alerta = new Dialog(context);

        alerta.setContentView(R.layout.dialog_alerta_general);
        Objects.requireNonNull(alerta.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tVTitulo, tVMensaje;

        tVTitulo = alerta.findViewById(R.id.tVTituloAlerta);
        tVMensaje = alerta.findViewById(R.id.tVMensajeAlerta);

        tVTitulo.setText(titulo);
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tVTitulo, 12, 18, 2, TypedValue.COMPLEX_UNIT_SP);
        tVMensaje.setText(mensaje);
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tVMensaje, 12, 18, 2, TypedValue.COMPLEX_UNIT_SP);


        return alerta;
    }

    public void eliminarVideoCloudinary (String url) {
        String idPublico = extraerPublicIdDesdeUrl(url);
        new Thread(() -> {
            try {
                Map resultado = MediaManager.get().getCloudinary().uploader().destroy(
                        idPublico,
                        ObjectUtils.asMap("resource_type", "video")
                );

                // Verificar si el eliminado fue exitoso
                if ("ok".equals(resultado.get("result"))) {
                    Log.d("CLOUDINARY", "Eliminado exitoso");
                } else {
                    Log.e("CLOUDINARY", "Falló el eliminado");
                }
            } catch (IOException e) {
                Log.e("CLOUDINARY", "Error al eliminar el vídeo: " + e.getMessage());

            }
        });
    }

    public void eliminarFirmaContrato (String idContrato) {

        db.collection("ContratosAdopciones")
                .document(idContrato).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        eliminarImagenFirebase(Objects.requireNonNull(documentSnapshot.get("firmaAdoptante")).toString());

                        db.collection("ContratosAdopciones")
                                .document(idContrato).update("firmaAdoptante", "")
                                .addOnSuccessListener(command -> {
                                    Log.d("FIRESTORE", "URL eliminada correctamente");
                                }).addOnFailureListener(e -> {
                                    Log.e("FIRESTORE", "No se pudo borrar la URL");
                                });
                    }
                }).addOnFailureListener(e -> {
                    Log.e("FIRESTORE", "Error al eliminar imagen");
                });

    }

    public void eliminarImagenFirebase (String url) {
        StorageReference fotoRef = storage.getReferenceFromUrl(url);

        fotoRef.delete().addOnSuccessListener(command -> {
            Log.d("FIREBASE", "Imagen eliminada correctamente desde URL");
        }).addOnFailureListener(e -> {
            Log.e("FIREBASE", "Error al eliminar: " + e.getMessage());
        });
    }

    public String extraerPublicIdDesdeUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        try {
            // Obtener la última parte después del último "/"
            String fileName = url.substring(url.lastIndexOf("/") + 1);

            // Quitar la extensión (ejemplo: ".mp4")
            int dotIndex = fileName.lastIndexOf(".");
            if (dotIndex > 0) {
                return fileName.substring(0, dotIndex);
            } else {
                return fileName; // Por si no tiene extensión
            }
        } catch (Exception e) {
            Log.e("ERROR", "Error al extraer el public ID: " + e.getMessage());
            return null;
        }
    }

    public String convertirFechaAFormatoServidor(String fechaNotificacion, String horaNotificacion) {
        try {

            // 1. Parsear la fecha de entrada (dd/MM/yyyy)
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date fecha = formatoEntrada.parse(fechaNotificacion);

            // 2. Crear calendario en zona horaria -05:00 (Ecuador)
            TimeZone tz = TimeZone.getTimeZone("GMT-05:00");
            Calendar calendar = Calendar.getInstance(tz);
            calendar.setTime(fecha);

            // 3. Procesar hora "HH:mm"
            if (horaNotificacion != null && horaNotificacion.contains(":")) {
                String[] partes = horaNotificacion.split(":");
                int hora = Integer.parseInt(partes[0]);
                int minuto = Integer.parseInt(partes[1]);

                calendar.set(Calendar.HOUR_OF_DAY, hora);
                calendar.set(Calendar.MINUTE, minuto);
            } else {
                // Si no envían hora → aplica tu hora por defecto
                calendar.set(Calendar.HOUR_OF_DAY, 8);
                calendar.set(Calendar.MINUTE, 0);
            }

            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // 4. Formato ISO final
            SimpleDateFormat formatoISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault());
            formatoISO.setTimeZone(tz);

            return formatoISO.format(calendar.getTime());

        } catch (Exception e) {
            Log.e("ERROR", "Error al convertir fecha");
            return null;
        }
    }

    public String convertirFechaServidorAFormatoNormal(String fechaISO) {
        try {
            // Formato ISO 8601 de entrada
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault());
            Date fecha = formatoEntrada.parse(fechaISO);

            // Extraer día, mes y año manualmente
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fecha);

            int dia = calendar.get(Calendar.DAY_OF_MONTH);
            int mes = calendar.get(Calendar.MONTH) + 1; // Se suma 1 porque enero = 0
            int anio = calendar.get(Calendar.YEAR);

            // Formatear manualmente con "%02d/%02d/%04d"
            return String.format(Locale.getDefault(), "%02d/%02d/%04d", dia, mes, anio);

        } catch (Exception e) {
            Log.e("ERROR", "Error al convertir fecha desde servidor: " + e.getMessage());
            return null;
        }
    }


    public ArrayList<String> obtenerTresFechasConsecutivas(String fechaIsoBase) {
        try {
            // Zona horaria fija (-05:00 Ecuador)
            TimeZone tz = TimeZone.getTimeZone("GMT-05:00");

            // Formato ISO compatible con tu método actual
            SimpleDateFormat formatoISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault());
            formatoISO.setTimeZone(tz);

            // Parsear fecha base
            Date fecha = formatoISO.parse(fechaIsoBase);

            // Crear calendario
            Calendar calendar = Calendar.getInstance(tz);
            calendar.setTime(fecha);

            ArrayList<String> fechas = new ArrayList<>();

            // Hoy, ayer y anteayer
            for (int i = 0; i < 3; i++) {
                Calendar aux = (Calendar) calendar.clone();
                aux.add(Calendar.DAY_OF_MONTH, -i); // ← fechas hacia atrás
                fechas.add(formatoISO.format(aux.getTime()));
            }

            return fechas;

        } catch (Exception e) {
            Log.e("ERROR", "Error al generar fechas consecutivas");
            return null;
        }
    }

    public void colocarTimePicker (EditText editText, Context context) {
        editText.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int hora = c.get(Calendar.HOUR_OF_DAY);
            int minuto = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    context,
                    (view, hourOfDay, minute) -> {
                        String h = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                        editText.setText(h);
                    },
                    hora,
                    minuto,
                    true
            );
            timePickerDialog.show();

        });
    }


}
