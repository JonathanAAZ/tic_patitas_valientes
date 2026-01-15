package com.example.tic_pv.Controlador;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.tic_pv.Modelo.Adopcion;
import com.example.tic_pv.Modelo.ContratoAdopcion;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ControladorContrato {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ControladorNotificaciones controladorNotificaciones = new ControladorNotificaciones();
    private ControladorAdopcion controladorAdopcion = new ControladorAdopcion();
    private ControladorUtilidades controladorUtilidades = new ControladorUtilidades();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    public ControladorContrato() {

    }

    public void crearContratoAdopcionDB(ContratoAdopcion contrato, Adopcion adopcion, LinearLayout barra, Context context) {
        barra.setVisibility(View.VISIBLE);

        Map<String, Object> mapContrato = new HashMap<>();
        mapContrato.put("estado", contrato.getEstado());
        mapContrato.put("idAdoptante", contrato.getIdAdoptante());
        mapContrato.put("idMascota", contrato.getIdMascota());
        mapContrato.put("fechaContratoAdopcion", "");
        mapContrato.put("horaContratoAdopcion", "");
        mapContrato.put("firmaAdministrador", contrato.getFirmaAdministrador());
        mapContrato.put("firmaAdoptante", "");


        db.collection("ContratosAdopciones").add(mapContrato).addOnSuccessListener(documentReference -> {
            Log.d("CONTRATO", "Contrato creado correctamente");
            Map<String, Object> mapAdopcion = new HashMap<>();
            mapAdopcion.put("estado", adopcion.getEstadoAdopcion());
            mapAdopcion.put("contratoAdopcion", documentReference.getId());

            controladorAdopcion.actualizarEstadoAdopcion(adopcion, mapAdopcion, barra, context);
        }).addOnFailureListener(e -> {
            Log.e("ERROR_CONTRATO", "No se ha creado el contrato correctamente");
        });
    }

    public void configurarClausulasContrato() {
        CollectionReference contratoRef = db.collection("ContratoGeneral");

        Map<String, Object> contrato = new HashMap<>();
        contrato.put("fechaCreacion", System.currentTimeMillis());

        contratoRef.add(contrato).addOnSuccessListener(documentReference -> {
           String idContrato = documentReference.getId();
           agregarClausulas(idContrato);
        }).addOnFailureListener(e -> {
            Log.e("ERROR", "Error al crear el contrato " + e.getMessage());
        });
    }

    private void agregarClausulas (String idContrato) {
        CollectionReference clausulasRef = db.collection("ContratoGeneral").document(idContrato).collection("Clausulas");

        Map<String, Map<String, Object>> clausulas = new HashMap<>();
        clausulas.put("1", crearClausula("PRIMERO: COMPARECIENTES: ", "Comparecen a la celebración del presente contrato de adopción, las siguientes partes:"));
        clausulas.put("1.1", crearClausula("PRIMERO: COMPARECIENTES: ", "Ambas partes, en forma libre y voluntaria concuerdan en celebrar el presente contrato de adopción, bajo los parámetros que las leyes ecuatorianas (normas civiles y penales) regulan los presentes actos y contratos."));
        clausulas.put("2", crearClausula("SEGUNDO: ADOPCIÓN DE MASCOTAS: ", "El adoptante [nombre], se compromete a  adoptar a la mascota que se encuentra bajo cuidado y custodia del Hogar Temporal Patitas Valientes, cuyas características son las siguientes:"));
        clausulas.put("3", crearClausula("TERCERO: DECLARACIÓN: ", "El adoptante declara adoptar a la mascota única y exclusivamente para compañía."));
        clausulas.put("4", crearClausula("CUARTA: PROHIBICIÓN: ", "La mascota entregada en adopción no podrá ser utilizada para:\n" +
                "•\tExperimentación de cualquier tipo\n" +
                "•\tLa práctica de peleas o enfrentamientos con otros animales\n" +
                "•\tLa cría\n" +
                "•\tLa caza\n" +
                "•\tParticipación y espectáculos de cualquier tipo"));
        clausulas.put("5", crearClausula("QUINTA: MALTRATO ANIMAL: ", "Tampoco se lo podrá someter a cualquier tipo de maltrato o lesión, de conformidad con lo previsto en el Código Orgánico Integral Penal. Caso contrario se iniciarán las acciones legales correspondientes."));
        clausulas.put("6", crearClausula("SEXTA: CONDICIONES DE HABITACIÓN: ", "El adoptante se compromete a proporcionar a la mascota alimentación y bebida suficiente y adecuada, prestarle los cuidados de higiene necesarios, la debida asistencia veterinaria, cuidarlo y respetarlo. Asimismo, se compromete a no regalar, vender o ceder por cualquier título a la mascota; en caso de no poder hacerse cargo de este por cualquier causa, se pondrá en contacto con el Hogar Temporal Patitas Valientes quienes recuperará la custodia y propiedad del mismo, el adoptante al devolverlo debe cubrir su alimentación y atención veterinaria hasta que encuentre un nuevo hogar."));
        clausulas.put("7", crearClausula("SÉPTIMO: EUTANASIA DE LA MASCOTA: ", "Para proceder a la eutanasia del animal (por motivos diferentes a enfermedad terminal), se requerirá el consentimiento previo del Hogar Temporal Patitas Valientes En caso de discrepancia, esta última se hará cargo de aquél."));
        clausulas.put("8", crearClausula("OCTAVO: PÉRDIDA DE LA MASCOTA: ", "La desaparición de la mascota, por robo, perdida, extravío o por cualquier otra causa, debe ser notificada al Hogar Temporal Patitas Valientes, a fin de que colabore en su búsqueda. El Adoptante debe siempre mantener dentro de casa, si sale será con su correa y debe portar collar y placa con datos."));
        clausulas.put("9", crearClausula("NOVENA: ESTERILIZACIÓN DE LA MASCOTA: ", "El adoptante se compromete a esterilizar al animal en caso de no estar esterilizado por motivos de salud o por no alcanzar la edad adecuada (cachorros) en el plazo máximo de 3 meses. En caso de ignorar la norma y preñar a su mascota o usarlo para cría, el Hogar Temporal Patitas Valientes podría reclamar la devolución del mismo para pasar a ser custodiado por la asociación y el adoptante deberá cubrir su alimentación y cuidado veterinario hasta que encuentre un nuevo hogar."));
        clausulas.put("10", crearClausula("DÉCIMA: RÉGIMEN DE VISITAS Y SEGUIMIENTO DE LA ADOPCIÓN: ", "Asimismo se compromete a que el Hogar Temporal Patitas Valientes, pueda realizar el seguimiento periódico del estado de la mascota adoptada. Solicitar información respectiva sobre su cuidado. Verificar en forma personal o por medio de un representante, el estado actual de salud de la mascota. \n\n" +
                        "Acotando que el adoptante, autoriza al Hogar Temporal Patitas Valientes, que en caso de incumplimiento de lo antes indicado en el presente contrato, se puedan iniciar todas las acciones legales con la finalidad de obtener la devolución o retorno de la mascota. En caso de presentarse lesiones o maltratos, se iniciará los trámites legales pertinentes.\n"));

        // Agregar cada cláusula a Firestore con su ID definido
        for (Map.Entry<String, Map<String, Object>> entry : clausulas.entrySet()) {
            clausulasRef.document(entry.getKey()).set(entry.getValue());
        }
    }

    private Map<String, Object> crearClausula(String titulo, String contenido) {
        Map<String, Object> clausula = new HashMap<>();
        clausula.put("titulo", titulo);
        clausula.put("contenido", contenido);
        return clausula;
    }

    public static Bitmap obtenerBitmapDesdeURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public interface ImageCallback {
        void onImageLoaded(Bitmap bitmap);
    }

    public static void obtenerBitmapAsincrono(String imageUrl, ImageCallback callback) {
        executor.execute(() -> {
            Bitmap bitmap = obtenerBitmapDesdeURL(imageUrl);
            mainHandler.post(() -> callback.onImageLoaded(bitmap));
        });
    }

    public void abrirPDFGenerado(Uri uriDescargas, File archivoPDF, Context context) {
        Intent intent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && uriDescargas != null) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uriDescargas, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else if (archivoPDF != null && archivoPDF.exists()) {
            Uri uri = FileProvider.getUriForFile(context,
                    context.getPackageName() + ".fileprovider", archivoPDF);

            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            Toast.makeText(context, "No se pudo abrir el PDF", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No se encontró una aplicación para abrir PDF", Toast.LENGTH_SHORT).show();
        }
    }
}
