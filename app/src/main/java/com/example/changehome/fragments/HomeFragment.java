package com.example.changehome.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.changehome.activities.ViviendaActivity;
import com.example.changehome.adaptador.ViviendaAdapter;
import com.example.changehome.databinding.FragmentHomeBinding;
import com.example.changehome.modelo.entidades.Vivienda;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements ViviendaAdapter.OnViviendaClickListener {

    private static final String TAG = "HomeFragment";
    private static final String COLLECTION_NAME = "vivienda";

    private FragmentHomeBinding binding;
    private ViviendaAdapter adapter;
    private List<Vivienda> listaViviendas;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initializeComponents();
        setupRecyclerView();
        cargarViviendasFirebase();

        return root;
    }

    private void initializeComponents() {
        listaViviendas = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();
        adapter = new ViviendaAdapter(requireContext(), listaViviendas);

        Log.d(TAG, "Componentes inicializados correctamente");
    }

    private void setupRecyclerView() {
        binding.propertyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.propertyRecyclerView.setHasFixedSize(true);
        binding.propertyRecyclerView.setAdapter(adapter);

        // Establecer el listener para los clicks
        adapter.setOnViviendaClickListener(this);

        Log.d(TAG, "RecyclerView configurado");
    }

    private void cargarViviendasFirebase() {
        Log.d(TAG, "Iniciando carga de viviendas desde Firebase...");

        firestore.collection(COLLECTION_NAME)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d(TAG, "Consulta exitosa. Documentos encontrados: " + queryDocumentSnapshots.size());

                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.w(TAG, "No se encontraron documentos en la colección 'vivienda'");
                        mostrarMensaje("No hay viviendas disponibles");
                        return;
                    }

                    listaViviendas.clear();
                    int documentosExitosos = 0;

                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Log.d(TAG, "Procesando documento: " + document.getId());

                            // Verificar que el documento tenga datos
                            if (!document.exists()) {
                                Log.w(TAG, "Documento no existe: " + document.getId());
                                continue;
                            }

                            // Convertir a objeto Vivienda
                            Vivienda vivienda = document.toObject(Vivienda.class);

                            if (vivienda != null) {
                                // Asegurar que el documentId esté establecido
                                if (vivienda.getDocumentId() == null) {
                                    vivienda.setDocumentId(document.getId());
                                }

                                // Log para debug - ver qué datos se cargaron
                                Log.d(TAG, "Vivienda cargada: " + vivienda.toString());

                                listaViviendas.add(vivienda);
                                documentosExitosos++;

                            } else {
                                Log.e(TAG, "Error: documento se convirtió a null - " + document.getId());
                            }

                        } catch (Exception e) {
                            Log.e(TAG, "Error procesando documento " + document.getId() + ": " + e.getMessage(), e);
                        }
                    }

                    Log.d(TAG, "Carga completada. Viviendas procesadas exitosamente: " + documentosExitosos);

                    // Actualizar el adapter
                    adapter.notifyDataSetChanged();

                    // Mostrar resultado
                    if (documentosExitosos > 0) {
                        mostrarMensaje("Cargadas " + documentosExitosos + " viviendas");
                        Log.d(TAG, "Datos mostrados en RecyclerView");
                    } else {
                        mostrarMensaje("No se pudieron cargar las viviendas");
                        Log.w(TAG, "No se cargaron viviendas en la lista");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error cargando viviendas desde Firebase: " + e.getMessage(), e);
                    mostrarMensaje("Error al cargar viviendas: " + e.getMessage());
                });
    }

    private void mostrarMensaje(String mensaje) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
        }
    }

    // Método público para refrescar los datos
    public void refrescarDatos() {
        cargarViviendasFirebase();
    }

    // Implementación de los listeners del adapter
    @Override
    public void onViviendaClick(Vivienda vivienda, int position) {
        Log.d(TAG, "Click en vivienda: " + vivienda.getTitulo());

        // Crear Intent para abrir ViviendaActivity
        Intent intent = ViviendaActivity.createIntent((AppCompatActivity) requireActivity(), vivienda);
        startActivity(intent);
    }

    @Override
    public void onContactarClick(Vivienda vivienda, int position) {
        Log.d(TAG, "Click en contactar para: " + vivienda.getTitulo());
        // Aquí puedes implementar la funcionalidad de contacto
        // Por ejemplo: abrir WhatsApp, email, teléfono, etc.
        mostrarMensaje("Contactar para: " + vivienda.getTitulo());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Limpiar referencias
        if (listaViviendas != null) {
            listaViviendas.clear();
        }

        binding = null;
        Log.d(TAG, "Fragment destruido y recursos liberados");
    }
}