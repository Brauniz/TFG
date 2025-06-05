package com.example.changehome.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.changehome.R;
import com.example.changehome.modelo.entidades.Vivienda;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class CreateViviendaActivity extends AppCompatActivity {

    // Vistas
    private TextInputEditText etCiudad, etTitulo, etSubtitulo, etDescripcion;
    private Button btnCancelar, btnGuardar;
    private ImageButton btnSubirImagen;
    private ProgressBar progressBar;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseUser currentUser;

    // Variables para imagen
    private Uri imageUri = null;
    private String uploadedImageUrl = "";

    // Constantes
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 2;
    private static final int MEDIA_PERMISSION_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_vivienda_layout); // Usar el layout que proporcionaste

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Verificar que Storage esté configurado correctamente
        try {
            storageRef = storage.getReference();
        } catch (IllegalStateException e) {
            Toast.makeText(this, "Error: Firebase Storage no está configurado correctamente", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
            return;
        }

        currentUser = mAuth.getCurrentUser();

        // Verificar autenticación
        if (currentUser == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar vistas
        initViews();

        // Configurar listeners
        setupListeners();
    }

    private void initViews() {
        // TextInputEditText
        etCiudad = findViewById(R.id.et_titulo); // En tu layout el id es et_titulo pero el hint es "Ciudad"
        etTitulo = findViewById(R.id.et_descripcion); // En tu layout el id es et_descripcion pero el hint es "Titulo"
        etSubtitulo = findViewById(R.id.et_precio); // En tu layout el id es et_precio pero el hint es "Subtitulo"
        etDescripcion = findViewById(R.id.et_ubicacion); // En tu layout el id es et_ubicacion pero el hint es "Descripcion"

        // Botones
        btnCancelar = findViewById(R.id.btn_cancelar);
        btnGuardar = findViewById(R.id.btn_guardar);
        btnSubirImagen = findViewById(R.id.btn_subir_imagen);

        // Crear ProgressBar programáticamente ya que no está en el layout
        progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.GONE);
    }

    private void setupListeners() {
        // Botón cancelar
        btnCancelar.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        // Botón guardar
        btnGuardar.setOnClickListener(v -> {
            if (validateFields()) {
                saveVivienda();
            }
        });

        // Botón subir imagen
        btnSubirImagen.setOnClickListener(v -> {
            checkPermissionAndOpenGallery();
        });
    }

    private boolean validateFields() {
        String ciudad = etCiudad.getText().toString().trim();
        String titulo = etTitulo.getText().toString().trim();
        String subtitulo = etSubtitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (ciudad.isEmpty()) {
            etCiudad.setError("La ciudad es requerida");
            etCiudad.requestFocus();
            return false;
        }

        if (titulo.isEmpty()) {
            etTitulo.setError("El título es requerido");
            etTitulo.requestFocus();
            return false;
        }

        if (subtitulo.isEmpty()) {
            etSubtitulo.setError("El subtítulo es requerido");
            etSubtitulo.requestFocus();
            return false;
        }

        if (descripcion.isEmpty()) {
            etDescripcion.setError("La descripción es requerida");
            etDescripcion.requestFocus();
            return false;
        }

        if (imageUri == null && uploadedImageUrl.isEmpty()) {
            Toast.makeText(this, "Por favor, selecciona una imagen", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void checkPermissionAndOpenGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ usa READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        MEDIA_PERMISSION_CODE);
            } else {
                openGallery();
            }
        } else {
            // Android 12 y anteriores usan READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_CODE);
            } else {
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE || requestCode == MEDIA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permiso denegado para acceder a la galería",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Toast.makeText(this, "Imagen seleccionada", Toast.LENGTH_SHORT).show();
            // Cambiar el icono o mostrar preview si lo deseas
            btnSubirImagen.setImageURI(imageUri);
        }
    }

    private void saveVivienda() {
        // Mostrar progress
        showProgress(true);
        btnGuardar.setEnabled(false);

        if (imageUri != null) {
            // Subir imagen primero
            uploadImageAndSaveVivienda();
        } else {
            // Si ya hay una URL de imagen (no debería pasar en este caso)
            saveViviendaToFirestore();
        }
    }

    private void uploadImageAndSaveVivienda() {
        // Generar nombre único para la imagen
        String imageName = "viviendas/" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString() + ".jpg";

        // Obtener referencia correcta al bucket de Storage
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(imageName);

        // Verificar que imageUri no sea null
        if (imageUri == null) {
            Toast.makeText(this, "Error: No se ha seleccionado ninguna imagen", Toast.LENGTH_SHORT).show();
            showProgress(false);
            btnGuardar.setEnabled(true);
            return;
        }

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Obtener URL de descarga
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        uploadedImageUrl = uri.toString();
                        saveViviendaToFirestore();
                    }).addOnFailureListener(e -> {
                        showProgress(false);
                        btnGuardar.setEnabled(true);
                        Toast.makeText(CreateViviendaActivity.this,
                                "Error al obtener URL de imagen: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    });
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    btnGuardar.setEnabled(true);
                    Toast.makeText(CreateViviendaActivity.this,
                            "Error al subir imagen: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                })
                .addOnProgressListener(snapshot -> {
                    // Opcional: mostrar progreso de subida
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    // Puedes actualizar un ProgressBar aquí si lo deseas
                });
    }

    private void saveViviendaToFirestore() {
        // Obtener datos de los campos
        String ciudad = etCiudad.getText().toString().trim();
        String titulo = etTitulo.getText().toString().trim();
        String subtitulo = etSubtitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        // Crear objeto Vivienda
        Vivienda vivienda = new Vivienda(
                uploadedImageUrl,
                titulo,
                subtitulo,
                descripcion,
                ciudad,
                currentUser.getUid() // Agregar el ID del usuario creador
        );

        // Guardar en Firestore
        db.collection("vivienda")
                .add(vivienda)
                .addOnSuccessListener(documentReference -> {
                    showProgress(false);
                    Toast.makeText(CreateViviendaActivity.this,
                            "Vivienda creada exitosamente",
                            Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    btnGuardar.setEnabled(true);
                    Toast.makeText(CreateViviendaActivity.this,
                            "Error al guardar vivienda: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void showProgress(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        // También puedes mostrar un diálogo de progreso aquí
    }

    // Método adicional para debug
    private void checkStorageConfiguration() {
        try {
            String bucket = storage.getReference().getBucket();
            if (bucket == null || bucket.isEmpty()) {
                Toast.makeText(this, "Error: Storage bucket no configurado", Toast.LENGTH_LONG).show();
                Log.e("CreateVivienda", "Storage bucket is null or empty");
            } else {
                Log.d("CreateVivienda", "Storage bucket: " + bucket);
            }
        } catch (Exception e) {
            Log.e("CreateVivienda", "Error checking storage configuration", e);
        }
    }

    @Override
    public void onBackPressed() {
        // Comportamiento igual que el botón cancelar
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}