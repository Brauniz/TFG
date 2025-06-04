package com.example.changehome.adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.changehome.R;
import com.example.changehome.modelo.entidades.Contact;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class ContactAdapter extends FirestoreRecyclerAdapter<Contact, ContactAdapter.ContactViewHolder> {

    private Context context;
    private OnContactClickListener listener;

    // Interface para manejar clicks en los items
    public interface OnContactClickListener {
        void onContactClick(DocumentSnapshot documentSnapshot, int position);
        void onContactLongClick(DocumentSnapshot documentSnapshot, int position);

        // Implementación de los métodos del listener
        void onContactClick(Contact contact, int position);

        void onContactLongClick(Contact contact, int position);
    }

    public ContactAdapter(@NonNull FirestoreRecyclerOptions<Contact> options, Context context) {
        super(options);
        this.context = context;
    }

    public void setOnContactClickListener(OnContactClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_layout, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ContactViewHolder holder, int position, @NonNull Contact contact) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
        holder.bind(contact, documentSnapshot);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgProfile;
        private TextView txtName;
        private TextView txtPhone;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            txtName = itemView.findViewById(R.id.txtName);
            txtPhone = itemView.findViewById(R.id.txtPhone);
        }

        public void bind(Contact contact, DocumentSnapshot documentSnapshot) {
            txtName.setText(contact.getName());
            txtPhone.setText(contact.getUserName());

            // Cargar imagen con Glide
            if (contact.getImageUrl() != null && !contact.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(contact.getImageUrl())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.hunter)
                                .error(R.drawable.hunter)
                                .circleCrop())
                        .into(imgProfile);
            } else {
                imgProfile.setImageResource(R.drawable.hunter);
            }

            // Configurar click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onContactClick(documentSnapshot, getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onContactLongClick(documentSnapshot, getAdapterPosition());
                    return true;
                }
                return false;
            });
        }
    }

    // Método para filtrar contactos (opcional - implementación alternativa)
    public void filter(String query) {
        // Esta implementación requiere que la Activity/Fragment maneje una nueva consulta
        // y actualice las opciones del adaptador usando updateOptions()
        throw new UnsupportedOperationException("Use updateOptions() with a new query instead");
    }
}