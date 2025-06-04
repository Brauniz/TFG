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

    // Interface simplificada para manejar clicks en los items
    public interface OnContactClickListener {
        void onContactClick(DocumentSnapshot documentSnapshot, int position);
        void onContactLongClick(DocumentSnapshot documentSnapshot, int position);
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
        private TextView txtPhone; // Mantenemos txtPhone como en tu layout

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            txtName = itemView.findViewById(R.id.txtName);
            txtPhone = itemView.findViewById(R.id.txtPhone);
        }

        public void bind(Contact contact, DocumentSnapshot documentSnapshot) {
            // Mostrar nombre del usuario
            if (contact.getName() != null && !contact.getName().isEmpty()) {
                txtName.setText(contact.getName());
            } else {
                txtName.setText("Usuario sin nombre");
            }

            // Mostrar email en el campo txtPhone (según tu layout)
            if (contact.getEmail() != null && !contact.getEmail().isEmpty()) {
                txtPhone.setText(contact.getEmail());
            } else {
                txtPhone.setText("Sin email");
            }

            // Cargar imagen de perfil con Glide - SIN circleCrop porque tu layout ya tiene clipToOutline
            if (contact.getProfileImageUrl() != null && !contact.getProfileImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(contact.getProfileImageUrl())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.hunter)
                                .error(R.drawable.hunter))
                        .into(imgProfile);
            } else {
                // Usar imagen por defecto si no hay imagen de perfil
                imgProfile.setImageResource(R.drawable.hunter);
            }

            // Configurar click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onContactClick(documentSnapshot, getAdapterPosition());
                    listener.onContactClick(contact, getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onContactLongClick(documentSnapshot, getAdapterPosition());
                    listener.onContactLongClick(contact, getAdapterPosition());
                    return true;
                }
                return false;
            });
        }
    }

    // Método para obtener un contacto en una posición específica
    public Contact getContact(int position) {
        return getItem(position);
    }

    // Método para obtener el DocumentSnapshot en una posición específica
    public DocumentSnapshot getSnapshot(int position) {
        return getSnapshots().getSnapshot(position);
    }

    // Método para obtener el ID del documento en una posición específica
    public String getDocumentId(int position) {
        return getSnapshots().getSnapshot(position).getId();
    }
}