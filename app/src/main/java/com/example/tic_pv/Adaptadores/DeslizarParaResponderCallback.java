package com.example.tic_pv.Adaptadores;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tic_pv.R;

// Gesto de deslizar un mensaje hacia la derecha para responderlo, como en WhatsApp.
// El mensaje no se elimina: acompaña al dedo hasta un tope y vuelve a su lugar.
public class DeslizarParaResponderCallback extends ItemTouchHelper.SimpleCallback {

    private final ListaMensajesAdaptador adaptador;
    private final Drawable iconoResponder;

    // Hasta dónde puede acompañar el mensaje al dedo, en píxeles
    private static final float DESPLAZAMIENTO_MAXIMO = 150f;

    // Parte del ancho que hay que recorrer para que cuente como respuesta
    private static final float UMBRAL_RESPUESTA = 0.25f;

    public DeslizarParaResponderCallback(RecyclerView recyclerView, ListaMensajesAdaptador adaptador) {
        super(0, ItemTouchHelper.RIGHT);
        this.adaptador = adaptador;
        this.iconoResponder = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.ic_responder);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return UMBRAL_RESPUESTA;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int posicion = viewHolder.getAbsoluteAdapterPosition();
        if (posicion == RecyclerView.NO_POSITION) {
            return;
        }

        adaptador.responderMensaje(posicion);

        // El mensaje debe volver a su sitio, no desaparecer de la lista
        adaptador.notifyItemChanged(posicion);
    }

    @Override
    public void onChildDraw(@NonNull Canvas canvas,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {

        // Se limita el arrastre para que el mensaje no cruce toda la pantalla
        float desplazamiento = Math.min(dX, DESPLAZAMIENTO_MAXIMO);

        dibujarIconoResponder(canvas, viewHolder, desplazamiento);

        super.onChildDraw(canvas, recyclerView, viewHolder, desplazamiento, dY, actionState, isCurrentlyActive);
    }

    // El icono aparece detrás del mensaje y se va haciendo visible al arrastrar
    private void dibujarIconoResponder(Canvas canvas, RecyclerView.ViewHolder viewHolder, float desplazamiento) {
        if (iconoResponder == null || desplazamiento <= 0) {
            return;
        }

        int alto = iconoResponder.getIntrinsicHeight();
        int ancho = iconoResponder.getIntrinsicWidth();

        int centroVertical = viewHolder.itemView.getTop()
                + (viewHolder.itemView.getHeight() - alto) / 2;
        int margenIzquierdo = (int) Math.max(0, desplazamiento - ancho - 16);

        iconoResponder.setBounds(margenIzquierdo,
                centroVertical,
                margenIzquierdo + ancho,
                centroVertical + alto);

        // La opacidad acompaña al arrastre para dar sensación de progreso
        int opacidad = (int) Math.min(255, (desplazamiento / DESPLAZAMIENTO_MAXIMO) * 255);
        iconoResponder.setAlpha(opacidad);
        iconoResponder.draw(canvas);
    }
}
