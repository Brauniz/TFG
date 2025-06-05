package com.example.changehome.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.changehome.R;
import com.example.changehome.adaptador.ImageCarouselAdapter;
import com.example.changehome.modelo.entidades.Vivienda;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViviendaActivity extends AppCompatActivity {

    private static final String TAG = "ViviendaActivity";
    public static final String EXTRA_VIVIENDA_ID = "vivienda_id";
    public static final String EXTRA_VIVIENDA_OBJECT = "vivienda_objeto"; // Cambiado para coincidir con el adapter

    // Vistas del layout
    private ViewPager2 viewPagerVivienda;
    private ImageButton btnPrev, btnNext, btnGoBack;
    private TextView tvUbicacion, tvDatos, tvEtiquetas, tvContEtiquetas, tvDescripcion, tvContTiempo;

    // Datos
    private Vivienda vivienda;
    private ImageCarouselAdapter carouselAdapter;
    private List<String> imagenesVivienda;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_vivienda);

        initViews();
        setupFirestore();
        obtenerDatosVivienda();
        setupCarousel();
        setupNavigation();
    }

    private void initViews() {
        viewPagerVivienda = findViewById(R.id.viewPagerVivienda);
        btnPrev = findViewById(R.id.btn_prev);
        btnNext = findViewById(R.id.btn_next);
        btnGoBack = findViewById(R.id.go_back);
        tvUbicacion = findViewById(R.id.Ubicacion);
        tvDatos = findViewById(R.id.Datos);
        tvEtiquetas = findViewById(R.id.tvetiquetas);
        tvContEtiquetas = findViewById(R.id.contEtiquetas);
        tvDescripcion = findViewById(R.id.Descripcion);
        tvContTiempo = findViewById(R.id.contTiempo);

        // Inicializar lista de imágenes
        imagenesVivienda = new ArrayList<>();
    }

    private void setupFirestore() {
        firestore = FirebaseFirestore.getInstance();
    }

    private void obtenerDatosVivienda() {
        // Intentar obtener la vivienda del Intent - probando múltiples formas
        vivienda = (Vivienda) getIntent().getSerializableExtra(EXTRA_VIVIENDA_OBJECT);

        // Si no funciona con la primera clave, probar las que usa el adapter
        if (vivienda == null) {
            vivienda = (Vivienda) getIntent().getSerializableExtra("vivienda_objeto");
        }

        // También obtener datos individuales del Intent como respaldo
        String viviendaId = getIntent().getStringExtra(EXTRA_VIVIENDA_ID);
        if (viviendaId == null) {
            viviendaId = getIntent().getStringExtra("vivienda_id");
        }

        if (vivienda != null) {
            // Si ya tenemos el objeto completo, mostrar datos
            Log.d(TAG, "Vivienda recibida por objeto: " + vivienda.getTitulo());
            mostrarDatosVivienda();
        } else if (viviendaId != null) {
            // Si solo tenemos el ID, buscar en Firebase
            Log.d(TAG, "Buscando vivienda por ID: " + viviendaId);
            buscarViviendaPorId(viviendaId);
        } else {
            // Como último recurso, crear vivienda con datos individuales del Intent
            crearViviendaDesdeIntent();
        }
    }

    private void crearViviendaDesdeIntent() {
        Intent intent = getIntent();
        String titulo = intent.getStringExtra("vivienda_titulo");
        String subtitulo = intent.getStringExtra("vivienda_subtitulo");
        String descripcion = intent.getStringExtra("vivienda_descripcion");
        String imagen = intent.getStringExtra("vivienda_imagen");
        String ciudad = intent.getStringExtra("vivienda_ciudad");
        String id = intent.getStringExtra("vivienda_id");

        if (titulo != null) {
            // Crear objeto vivienda con los datos recibidos
            vivienda = new Vivienda(imagen, titulo, subtitulo, descripcion, ciudad, vivienda.getCreadorId());
            vivienda.setDocumentId(id);
            Log.d(TAG, "Vivienda creada desde datos del Intent: " + titulo);
            mostrarDatosVivienda();
        } else {
            // Error: no hay datos
            Log.e(TAG, "No se recibieron datos de vivienda");
            Toast.makeText(this, "Error: No se encontraron datos de la vivienda", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void buscarViviendaPorId(String viviendaId) {
        // Corregir el nombre de la colección
        firestore.collection("viviendas") // Era "vivienda", debería ser "viviendas"
                .document(viviendaId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        vivienda = documentSnapshot.toObject(Vivienda.class);
                        if (vivienda != null) {
                            vivienda.setDocumentId(documentSnapshot.getId());
                            Log.d(TAG, "Vivienda encontrada: " + vivienda.getTitulo());
                            mostrarDatosVivienda();
                        } else {
                            mostrarError("Error al procesar los datos de la vivienda");
                        }
                    } else {
                        mostrarError("Vivienda no encontrada");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error buscando vivienda", e);
                    mostrarError("Error al cargar la vivienda: " + e.getMessage());
                });
    }

    private void mostrarDatosVivienda() {
        if (vivienda == null) return;

        Log.d(TAG, "Mostrando datos de: " + vivienda.getTitulo());

        // Establecer datos en las vistas
        establecerUbicacion();
        establecerDatos();
        establecerDescripcion();
        configurarImagenes();
    }

    private void establecerUbicacion() {
        if (vivienda.getCiudad() != null && !vivienda.getCiudad().trim().isEmpty()) {
            tvDatos.setText(vivienda.getCiudad());
        } else {
            tvDatos.setText("Ubicación no especificada");
        }
    }

    private void establecerDatos() {
        if (vivienda.getSubtitulo() != null && !vivienda.getSubtitulo().trim().isEmpty()) {
            tvContEtiquetas.setText(vivienda.getSubtitulo());
        } else {
            tvContEtiquetas.setText("Información no disponible");
        }
    }

    private void establecerDescripcion() {
        if (vivienda.getDescripcion() != null && !vivienda.getDescripcion().trim().isEmpty()) {
            tvContTiempo.setText(vivienda.getDescripcion());
        } else {
            tvContTiempo.setText("Descripción no disponible");
        }
    }

    private void configurarImagenes() {
        imagenesVivienda.clear();

        // Agregar imagen principal
        if (vivienda.getImagen() != null && !vivienda.getImagen().trim().isEmpty()) {
            imagenesVivienda.add(vivienda.getImagen());
        }

        // Agregar imágenes adicionales
        agregarImagenesAdicionales();

        // Si no hay imágenes, usar imagen por defecto
        if (imagenesVivienda.isEmpty()) {
            imagenesVivienda.add("android.resource://" + getPackageName() + "/" + R.drawable.prueba);
        }

        Log.d(TAG, "Configuradas " + imagenesVivienda.size() + " imágenes para el carrusel");
    }

    private void agregarImagenesAdicionales() {
        // Aquí puedes agregar lógica para cargar imágenes adicionales desde Firebase
        // Por ahora, agregamos algunas imágenes de ejemplo si solo hay una imagen
        if (imagenesVivienda.size() == 1) {
            // Agregar imágenes de ejemplo para demostración
            imagenesVivienda.addAll(Arrays.asList(
                    "https://images.unsplash.com/photo-1560448204-603b3fc33ddc?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80",
                    "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80"
            ));
        }
    }

    private void setupCarousel() {
        // Crear y configurar el adapter del carrusel
        carouselAdapter = new ImageCarouselAdapter(this, imagenesVivienda);
        viewPagerVivienda.setAdapter(carouselAdapter);

        // Listener para cambios de página
        viewPagerVivienda.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                actualizarBotonesNavegacion(position);
            }
        });

        // Configuración inicial
        actualizarBotonesNavegacion(0);
    }

    private void setupNavigation() {
        // Botón de retroceso
        btnGoBack.setOnClickListener(v -> {
            onBackPressed();
        });

        // Navegación del carrusel
        btnPrev.setOnClickListener(v -> {
            int currentItem = viewPagerVivienda.getCurrentItem();
            if (currentItem > 0) {
                viewPagerVivienda.setCurrentItem(currentItem - 1, true);
            }
        });

        btnNext.setOnClickListener(v -> {
            int currentItem = viewPagerVivienda.getCurrentItem();
            if (currentItem < imagenesVivienda.size() - 1) {
                viewPagerVivienda.setCurrentItem(currentItem + 1, true);
            }
        });
    }

    private void actualizarBotonesNavegacion(int position) {
        // Mostrar/ocultar botones según la posición
        btnPrev.setVisibility(position > 0 ? View.VISIBLE : View.INVISIBLE);
        btnNext.setVisibility(position < imagenesVivienda.size() - 1 ? View.VISIBLE : View.INVISIBLE);

        // Opcional: Mostrar indicador de posición
        Log.d(TAG, "Imagen " + (position + 1) + " de " + imagenesVivienda.size());
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        Log.e(TAG, mensaje);
        finish();
    }

    // Métodos estáticos para crear Intent (mejorados para compatibilidad)
    public static Intent createIntent(AppCompatActivity activity, Vivienda vivienda) {
        Intent intent = new Intent(activity, ViviendaActivity.class);
        intent.putExtra(EXTRA_VIVIENDA_OBJECT, vivienda);
        return intent;
    }

    public static Intent createIntent(AppCompatActivity activity, String viviendaId) {
        Intent intent = new Intent(activity, ViviendaActivity.class);
        intent.putExtra(EXTRA_VIVIENDA_ID, viviendaId);
        return intent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}