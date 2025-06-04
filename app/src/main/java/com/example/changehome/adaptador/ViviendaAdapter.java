package com.example.changehome.adaptador;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.changehome.R;
import com.example.changehome.modelo.entidades.Vivienda;

import java.util.List;

public class ViviendaAdapter extends RecyclerView.Adapter<ViviendaAdapter.ViviendaViewHolder> {

    private static final String TAG = "ViviendaAdapter";
    private Context context;
    private List<Vivienda> listaViviendas;
    private OnViviendaClickListener listener;

    // Interface para manejar clicks
    public interface OnViviendaClickListener {
        void onViviendaClick(Vivienda vivienda, int position);
        void onContactarClick(Vivienda vivienda, int position);
    }

    public ViviendaAdapter(Context context, List<Vivienda> listaViviendas) {
        this.context = context;
        this.listaViviendas = listaViviendas;
    }

    // Método para establecer el listener
    public void setOnViviendaClickListener(OnViviendaClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViviendaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viviendas_layout, parent, false);
        return new ViviendaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViviendaViewHolder holder, int position) {
        Vivienda vivienda = listaViviendas.get(position);

        Log.d(TAG, "Binding vivienda en posición " + position + ": " + vivienda.toString());

        // Verificar que los datos no sean null y establecer valores
        establecerTextos(holder, vivienda);
        cargarImagen(holder, vivienda);
        configurarListeners(holder, vivienda, position);
    }

    private void establecerTextos(ViviendaViewHolder holder, Vivienda vivienda) {
        // Título
        if (vivienda.getTitulo() != null && !vivienda.getTitulo().trim().isEmpty()) {
            holder.titulo.setText(vivienda.getTitulo());
        } else {
            holder.titulo.setText("Sin título");
            Log.w(TAG, "Vivienda sin título: " + vivienda.getDocumentId());
        }

        // Subtítulo
        if (vivienda.getSubtitulo() != null && !vivienda.getSubtitulo().trim().isEmpty()) {
            holder.subtitulo.setText(vivienda.getSubtitulo());
        } else {
            holder.subtitulo.setText("Sin información adicional");
            Log.w(TAG, "Vivienda sin subtítulo: " + vivienda.getDocumentId());
        }

        // Descripción
        if (vivienda.getDescripcion() != null && !vivienda.getDescripcion().trim().isEmpty()) {
            holder.descripcion.setText(vivienda.getDescripcion());
        } else {
            holder.descripcion.setText("Sin descripción disponible");
            Log.w(TAG, "Vivienda sin descripción: " + vivienda.getDocumentId());
        }
    }

    private void cargarImagen(ViviendaViewHolder holder, Vivienda vivienda) {
        // Cargar imagen desde Firestore (URL) o usar imagen por defecto
        if (vivienda.getImagen() != null && !vivienda.getImagen().trim().isEmpty()) {
            Log.d(TAG, "Cargando imagen desde URL: " + vivienda.getImagen());

            Glide.with(context)
                    .load(vivienda.getImagen())
                    .placeholder(R.drawable.prueba) // Imagen mientras carga
                    .error(R.drawable.prueba)       // Imagen si falla la carga
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache para mejor rendimiento
                    .centerCrop() // Ajustar imagen al ImageView
                    .into(holder.imagen);
        } else {
            Log.w(TAG, "Vivienda sin imagen, usando imagen por defecto: " + vivienda.getDocumentId());
            holder.imagen.setImageResource(R.drawable.prueba);
        }
    }

    private void configurarListeners(ViviendaViewHolder holder, Vivienda vivienda, int position) {
        // Click en toda la tarjeta
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViviendaClick(vivienda, position);
            } else {
                // Acción por defecto si no hay listener
                Toast.makeText(context, "Vivienda: " + vivienda.getTitulo(), Toast.LENGTH_SHORT).show();
            }
        });

        // Click en el botón contactar
        holder.btnContactar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onContactarClick(vivienda, position);
            } else {
                // Acción por defecto para contactar
                Toast.makeText(context, "Contactar por: " + vivienda.getTitulo(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = listaViviendas != null ? listaViviendas.size() : 0;
        Log.d(TAG, "getItemCount() retorna: " + count);
        return count;
    }

    // Método para actualizar la lista de viviendas
    public void actualizarLista(List<Vivienda> nuevaLista) {
        if (nuevaLista != null) {
            this.listaViviendas.clear();
            this.listaViviendas.addAll(nuevaLista);
            notifyDataSetChanged();
            Log.d(TAG, "Lista actualizada con " + nuevaLista.size() + " elementos");
        }
    }

    // Método para agregar una vivienda
    public void agregarVivienda(Vivienda vivienda) {
        if (vivienda != null && listaViviendas != null) {
            listaViviendas.add(vivienda);
            notifyItemInserted(listaViviendas.size() - 1);
            Log.d(TAG, "Vivienda agregada: " + vivienda.getTitulo());
        }
    }

    // Método para eliminar una vivienda
    public void eliminarVivienda(int position) {
        if (listaViviendas != null && position >= 0 && position < listaViviendas.size()) {
            Vivienda viviendaEliminada = listaViviendas.remove(position);
            notifyItemRemoved(position);
            Log.d(TAG, "Vivienda eliminada: " + viviendaEliminada.getTitulo());
        }
    }

    // Método para obtener una vivienda específica
    public Vivienda getVivienda(int position) {
        if (listaViviendas != null && position >= 0 && position < listaViviendas.size()) {
            return listaViviendas.get(position);
        }
        return null;
    }

    public static class ViviendaViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen;
        TextView titulo, subtitulo, descripcion;
        Button btnContactar;

        public ViviendaViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializar las vistas
            imagen = itemView.findViewById(R.id.img_vivienda);
            titulo = itemView.findViewById(R.id.txt_titulo);
            subtitulo = itemView.findViewById(R.id.txt_info);
            descripcion = itemView.findViewById(R.id.txt_descripcion);
            btnContactar = itemView.findViewById(R.id.btn_contactar);

            // Verificar que todas las vistas se encontraron
            if (imagen == null) Log.e("ViviendaViewHolder", "No se encontró img_vivienda");
            if (titulo == null) Log.e("ViviendaViewHolder", "No se encontró txt_titulo");
            if (subtitulo == null) Log.e("ViviendaViewHolder", "No se encontró txt_info");
            if (descripcion == null) Log.e("ViviendaViewHolder", "No se encontró txt_descripcion");
            if (btnContactar == null) Log.e("ViviendaViewHolder", "No se encontró btn_contactar");
        }
    }
}