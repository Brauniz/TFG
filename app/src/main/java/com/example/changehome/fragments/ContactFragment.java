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
        // Consulta base para todos los contactos ordenados por nombre
        Query baseQuery = db.collection("contacts").orderBy("name");

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
            searchQuery = db.collection("contacts").orderBy("name");
        } else {
            searchQuery = db.collection("contacts")
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
            Log.d(TAG, "Adaptador iniciado - escuchando cambios");
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

    @Override
    public void onContactClick(DocumentSnapshot documentSnapshot, int position) {

    }

    @Override
    public void onContactLongClick(DocumentSnapshot documentSnapshot, int position) {

    }

    // Implementación de los métodos del listener
    @Override
    public void onContactClick(Contact contact, int position) {
        Toast.makeText(getContext(), "Contacto: " + contact.getName(), Toast.LENGTH_SHORT).show();
        // Aquí puedes implementar acciones al hacer click
    }

    @Override
    public void onContactLongClick(Contact contact, int position) {
        Toast.makeText(getContext(), "Click largo en: " + contact.getName(), Toast.LENGTH_SHORT).show();
        // Aquí puedes implementar acciones al hacer click largo
    }

    // Método para agregar nuevo contacto
    public void addNewContact(Contact contact) {
        db.collection("contacts")
                .add(contact)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Contacto agregado", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Contacto agregado con ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al agregar", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error al agregar contacto", e);
                });
    }

    // Método para eliminar contacto
    public void deleteContact(String documentId) {
        db.collection("contacts").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Contacto eliminado", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Documento eliminado: " + documentId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error al eliminar documento", e);
                });
    }
}