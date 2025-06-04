package com.example.changehome.ui.home;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.changehome.adaptador.ViviendaAdapter;
import com.example.changehome.databinding.FragmentHomeBinding;
import com.example.changehome.modelo.entidades.Vivienda;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ViviendaAdapter adapter;
    private List<Vivienda> listaViviendas;
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listaViviendas = new ArrayList<>();
        adapter = new ViviendaAdapter(requireContext(), listaViviendas);
        firestore = FirebaseFirestore.getInstance();

        binding.propertyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.propertyRecyclerView.setAdapter(adapter);

        cargarViviendasFirebase();

        return root;
    }

    private void cargarViviendasFirebase() {
        firestore.collection("vivienda") // Asegúrate que el nombre coincide con tu colección
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaViviendas.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Vivienda v = doc.toObject(Vivienda.class);
                        listaViviendas.add(v);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Manejo de error
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}