package com.example.changehome.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.changehome.R;
import com.example.changehome.activities.ViviendaActivity;
import com.example.changehome.adaptador.ViviendaAdapter;
import com.example.changehome.modelo.entidades.Vivienda;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchFragment extends Fragment implements ViviendaAdapter.OnViviendaClickListener {

    private static final String TAG = "SearchFragment";

    private EditText searchEditText;
    private RecyclerView recyclerViewResultados;
    private TextView textViewResultados;
    private TextView textViewSugerencias;

    private ViviendaAdapter adapter;
    private List<Vivienda> listaResultados;
    private FirebaseFirestore firestore;

    // Lista de ciudades disponibles (se carga dinámicamente)
    private Set<String> ciudadesDisponibles;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initViews(view);
        setupFirestore();
        setupRecyclerView();
        setupSearchFunctionality();
        cargarCiudadesDisponibles();

        return view;
    }

    private void initViews(View view) {
        searchEditText = view.findViewById(R.id.searchEditText);

        // Crear vistas programáticamente o asegúrate de tenerlas en tu layout
        recyclerViewResultados = createRecyclerView();
        textViewResultados = createTextViewResultados();
        textViewSugerencias = createTextViewSugerencias();

        // Agregar las vistas al layout principal
        if (view instanceof ViewGroup) {
            ViewGroup container = (ViewGroup) view;
            container.addView(textViewSugerencias);
            container.addView(textViewResultados);
            container.addView(recyclerViewResultados);
        }
    }

    private RecyclerView createRecyclerView() {
        RecyclerView recyclerView = new RecyclerView(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(24, 16, 24, 16);
        recyclerView.setLayoutParams(params);
        recyclerView.setVisibility(View.GONE);
        return recyclerView;
    }

    private TextView createTextViewResultados() {
        TextView textView = new TextView(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(24, 16, 24, 8);
        textView.setLayoutParams(params);
        textView.setTextSize(16);
        textView.setTextColor(getResources().getColor(android.R.color.black));
        textView.setVisibility(View.GONE);
        return textView;
    }

    private TextView createTextViewSugerencias() {
        TextView textView = new TextView(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(24, 24, 24, 16);
        textView.setLayoutParams(params);
        textView.setText("Busca viviendas por ciudad...\n\nCiudades disponibles: Cargando...");
        textView.setTextSize(14);
        textView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        textView.setLineSpacing(4, 1.2f);
        return textView;
    }

    private void setupFirestore() {
        firestore = FirebaseFirestore.getInstance();
        ciudadesDisponibles = new HashSet<>();
    }

    private void setupRecyclerView() {
        listaResultados = new ArrayList<>();
        adapter = new ViviendaAdapter(requireContext(), listaResultados);
        adapter.setOnViviendaClickListener(this);

        recyclerViewResultados.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewResultados.setHasFixedSize(true);
        recyclerViewResultados.setAdapter(adapter);
    }

    private void setupSearchFunctionality() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String busqueda = s.toString().trim();
                if (busqueda.length() >= 2) { // Buscar a partir de 2 caracteres
                    buscarViviendasPorCiudad(busqueda);
                } else if (busqueda.isEmpty()) {
                    limpiarResultados();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Cambiar el hint dinámicamente
        searchEditText.setHint("Buscar por ciudad...");
    }

    private void cargarCiudadesDisponibles() {
        Log.d(TAG, "Cargando ciudades disponibles...");

        firestore.collection("vivienda")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ciudadesDisponibles.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String ciudad = doc.getString("ciudad");
                        if (ciudad != null && !ciudad.trim().isEmpty()) {
                            ciudadesDisponibles.add(ciudad.toLowerCase());
                        }
                    }

                    Log.d(TAG, "Ciudades cargadas: " + ciudadesDisponibles.size());
                    actualizarSugerencias();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error cargando ciudades", e);
                    textViewSugerencias.setText("Error al cargar ciudades disponibles");
                });
    }

    private void actualizarSugerencias() {
        if (ciudadesDisponibles.isEmpty()) {
            textViewSugerencias.setText("No hay ciudades disponibles");
            return;
        }

        StringBuilder sugerencias = new StringBuilder();
        sugerencias.append("Busca viviendas por ciudad...\n\n");
        sugerencias.append("Ciudades disponibles:\n");

        for (String ciudad : ciudadesDisponibles) {
            sugerencias.append("• ").append(capitalizeFirstLetter(ciudad)).append("\n");
        }

        textViewSugerencias.setText(sugerencias.toString());
    }

    private void buscarViviendasPorCiudad(String ciudadBusqueda) {
        Log.d(TAG, "Buscando viviendas en: " + ciudadBusqueda);

        String ciudadLower = ciudadBusqueda.toLowerCase();

        // Buscar por coincidencia exacta o que contenga el texto
        firestore.collection("vivienda")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Vivienda> resultados = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            Vivienda vivienda = doc.toObject(Vivienda.class);
                            if (vivienda != null && vivienda.getCiudad() != null) {
                                String ciudadVivienda = vivienda.getCiudad().toLowerCase();

                                // Buscar coincidencias (exacta o que contenga)
                                if (ciudadVivienda.contains(ciudadLower) ||
                                        ciudadLower.contains(ciudadVivienda)) {

                                    if (vivienda.getDocumentId() == null) {
                                        vivienda.setDocumentId(doc.getId());
                                    }
                                    resultados.add(vivienda);
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error procesando documento: " + doc.getId(), e);
                        }
                    }

                    mostrarResultados(resultados, ciudadBusqueda);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error en búsqueda", e);
                    Toast.makeText(getContext(), "Error al buscar", Toast.LENGTH_SHORT).show();
                });
    }

    private void mostrarResultados(List<Vivienda> resultados, String busqueda) {
        Log.d(TAG, "Mostrando " + resultados.size() + " resultados para: " + busqueda);

        listaResultados.clear();
        listaResultados.addAll(resultados);
        adapter.notifyDataSetChanged();

        // Mostrar/ocultar vistas según si hay resultados
        if (resultados.isEmpty()) {
            recyclerViewResultados.setVisibility(View.GONE);
            textViewResultados.setVisibility(View.VISIBLE);
            textViewResultados.setText("No se encontraron viviendas en \"" + busqueda + "\"");
            textViewSugerencias.setVisibility(View.VISIBLE);
        } else {
            recyclerViewResultados.setVisibility(View.VISIBLE);
            textViewResultados.setVisibility(View.VISIBLE);
            textViewResultados.setText("Encontradas " + resultados.size() +
                    " vivienda" + (resultados.size() != 1 ? "s" : "") +
                    " en \"" + capitalizeFirstLetter(busqueda) + "\"");
            textViewSugerencias.setVisibility(View.GONE);
        }
    }

    private void limpiarResultados() {
        listaResultados.clear();
        adapter.notifyDataSetChanged();

        recyclerViewResultados.setVisibility(View.GONE);
        textViewResultados.setVisibility(View.GONE);
        textViewSugerencias.setVisibility(View.VISIBLE);
    }

    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    // Implementación de los listeners del adapter
    @Override
    public void onViviendaClick(Vivienda vivienda, int position) {
        Log.d(TAG, "Click en vivienda: " + vivienda.getTitulo() + " (Ciudad: " + vivienda.getCiudad() + ")");

        // Crear Intent para abrir ViviendaActivity
        Intent intent = ViviendaActivity.createIntent((AppCompatActivity) requireActivity(), vivienda);
        startActivity(intent);
    }

    @Override
    public void onContactarClick(Vivienda vivienda, int position) {
        Log.d(TAG, "Contactar para: " + vivienda.getTitulo());
        Toast.makeText(getContext(), "Contactar: " + vivienda.getTitulo(), Toast.LENGTH_SHORT).show();
        // Aquí puedes implementar la funcionalidad de contacto
    }
}