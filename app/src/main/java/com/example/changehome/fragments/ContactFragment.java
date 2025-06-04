package com.example.changehome.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.changehome.R;
import com.example.changehome.adaptador.ContactAdapter;
import com.example.changehome.modelo.entidades.Contact;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ContactFragment extends Fragment implements ContactAdapter.OnContactClickListener {

    private static final String TAG = "ContactFragment";
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private EditText searchEditText;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        initViews(view);
        setupFirestore();
        setupRecyclerView();
        setupSearchFunctionality();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.propertyRecyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);
    }

    private void setupFirestore() {
        db = FirebaseFirestore.getInstance();
    }

    private void setupRecyclerView() {
        // CORREGIDO: Consulta a la colección "users" ordenada por nombre
        Query baseQuery = db.collection("users").orderBy("name");

        // Configurar opciones para el adaptador Firestore
        FirestoreRecyclerOptions<Contact> options = new FirestoreRecyclerOptions.Builder<Contact>()
                .setQuery(baseQuery, Contact.class)
                .build();

        // Crear el adaptador
        contactAdapter = new ContactAdapter(options, requireContext());
        contactAdapter.setOnContactClickListener(this);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(contactAdapter);
        recyclerView.setHasFixedSize(true);
    }

    private void setupSearchFunctionality() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().trim().toLowerCase();
                applySearchFilter(searchText);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void applySearchFilter(String searchText) {
        Query searchQuery;

        if (searchText.isEmpty()) {
            // CORREGIDO: Buscar en "users" en lugar de "contacts"
            searchQuery = db.collection("users").orderBy("name");
        } else {
            // CORREGIDO: Buscar en "users" con filtro de texto
            searchQuery = db.collection("users")
                    .orderBy("name")
                    .startAt(searchText)
                    .endAt(searchText + "\uf8ff");
        }

        FirestoreRecyclerOptions<Contact> newOptions = new FirestoreRecyclerOptions.Builder<Contact>()
                .setQuery(searchQuery, Contact.class)
                .build();

        contactAdapter.updateOptions(newOptions);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (contactAdapter != null) {
            contactAdapter.startListening();
            Log.d(TAG, "Adaptador iniciado - escuchando cambios en users");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (contactAdapter != null) {
            contactAdapter.stopListening();
            Log.d(TAG, "Adaptador detenido");
        }
    }

    // Implementación de los métodos del listener para DocumentSnapshot
    @Override
    public void onContactClick(DocumentSnapshot documentSnapshot, int position) {
        Contact contact = documentSnapshot.toObject(Contact.class);
        if (contact != null) {
            Toast.makeText(getContext(), "Usuario: " + contact.getName(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Click en usuario: " + contact.getName() + " - Email: " + contact.getEmail());
        }
    }

    @Override
    public void onContactLongClick(DocumentSnapshot documentSnapshot, int position) {
        Contact contact = documentSnapshot.toObject(Contact.class);
        if (contact != null) {
            Toast.makeText(getContext(), "Email: " + contact.getEmail(), Toast.LENGTH_LONG).show();
            Log.d(TAG, "Click largo en usuario: " + contact.getName());
        }
    }

    // Implementación de los métodos del listener para Contact
    @Override
    public void onContactClick(Contact contact, int position) {
        Toast.makeText(getContext(), "Usuario: " + contact.getName(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Usuario seleccionado: " + contact.getName() + " - " + contact.getEmail());
    }

    @Override
    public void onContactLongClick(Contact contact, int position) {
        Toast.makeText(getContext(), "Email: " + contact.getEmail(), Toast.LENGTH_LONG).show();
        Log.d(TAG, "Click largo en: " + contact.getName());
    }

    // ELIMINADO: Métodos para agregar/eliminar contactos ya que trabajamos con users registrados
    // Los usuarios se crean cuando se registran en Firebase Auth, no manualmente

    // Método opcional para refrescar la lista
    public void refreshUsers() {
        if (contactAdapter != null) {
            contactAdapter.notifyDataSetChanged();
            Log.d(TAG, "Lista de usuarios actualizada");
        }
    }

    // Método para obtener información de un usuario específico
    public void getUserInfo(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Contact user = documentSnapshot.toObject(Contact.class);
                        if (user != null) {
                            String info = "Nombre: " + user.getName() + "\nEmail: " + user.getEmail();
                            Toast.makeText(getContext(), info, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al obtener usuario", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error obteniendo usuario: " + userId, e);
                });
    }
}