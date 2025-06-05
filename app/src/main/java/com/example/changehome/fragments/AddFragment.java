package com.example.changehome.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.changehome.R;

import com.example.changehome.activities.CreateViviendaActivity;
import com.example.changehome.adaptador.ViviendaAdapter;

import com.example.changehome.modelo.entidades.Vivienda;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class AddFragment extends Fragment {

    private RecyclerView propertyRecyclerView;
    private FloatingActionButton fab;
    private ViviendaAdapter viviendaAdapter;
    private List<Vivienda> viviendaList;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    // Request code para el resultado de la actividad
    private static final int CREATE_VIVIENDA_REQUEST = 1001;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Inicializar vistas
        initViews(view);

        // Configurar RecyclerView
        setupRecyclerView();

        // Configurar FloatingActionButton
        setupFab();

        // Cargar viviendas del usuario actual
        loadUserViviendas();

        return view;
    }

    private void initViews(View view) {
        propertyRecyclerView = view.findViewById(R.id.propertyRecyclerView);
        fab = view.findViewById(R.id.fab);
    }

    private void setupRecyclerView() {
        viviendaList = new ArrayList<>();
        viviendaAdapter = new ViviendaAdapter(getContext(), viviendaList);
        propertyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        propertyRecyclerView.setAdapter(viviendaAdapter);
    }

    private void setupFab() {
        fab.setOnClickListener(v -> {
            // Abrir CreateViviendaActivity
            Intent intent = new Intent(getContext(), CreateViviendaActivity.class);
            startActivityForResult(intent, CREATE_VIVIENDA_REQUEST);
        });
    }

    private void loadUserViviendas() {
        if (currentUser != null) {
            // Cargar solo las viviendas del usuario actual
            db.collection("vivienda")
                    .whereEqualTo("creadorId", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            viviendaList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Vivienda vivienda = document.toObject(Vivienda.class);
                                viviendaList.add(vivienda);
                            }
                            viviendaAdapter.actualizarLista(viviendaList);

                            // Mostrar mensaje si no hay viviendas
                            if (viviendaList.isEmpty()) {
                                Toast.makeText(getContext(),
                                        "No tienes viviendas creadas. ¡Crea tu primera vivienda!",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getContext(),
                                    "Error al cargar viviendas: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_VIVIENDA_REQUEST && resultCode == getActivity().RESULT_OK) {
            // Si se creó una nueva vivienda exitosamente, recargar la lista
            loadUserViviendas();
            Toast.makeText(getContext(), "¡Vivienda creada exitosamente!", Toast.LENGTH_SHORT).show();
        }
    }

    // Método público para actualizar la lista desde otra parte de la app
    public void refreshViviendas() {
        loadUserViviendas();
    }

    // Método para añadir una nueva vivienda
    public void addNewVivienda(Vivienda vivienda) {
        if (viviendaAdapter != null) {
            viviendaAdapter.agregarVivienda(vivienda);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Actualizar datos cuando el fragment vuelve a ser visible
        loadUserViviendas();
    }
}