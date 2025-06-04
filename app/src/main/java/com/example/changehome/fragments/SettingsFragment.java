package com.example.changehome.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.changehome.R;
import com.example.changehome.adaptador.SettingsAdapter;
import com.example.changehome.modelo.entidades.Setting;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment implements SettingsAdapter.OnSettingClickListener {

    private ImageView imgProfile;
    private TextView txtName, txtId;
    private RecyclerView propertyRecyclerView;
    private SettingsAdapter settingsAdapter;
    private List<Setting> settingsList;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Inicializar vistas
        initViews(view);

        // Configurar RecyclerView
        setupRecyclerView();

        // Cargar datos del usuario actual
        loadUserProfile();

        // Cargar settings desde Firebase
        loadSettingsFromFirebase();

        return view;
    }

    private void initViews(View view) {
        imgProfile = view.findViewById(R.id.imgProfile);
        txtName = view.findViewById(R.id.txtName);
        txtId = view.findViewById(R.id.txtid);
        propertyRecyclerView = view.findViewById(R.id.propertyRecyclerView);
    }

    private void setupRecyclerView() {
        settingsList = new ArrayList<>();
        settingsAdapter = new SettingsAdapter(getContext(), settingsList);
        settingsAdapter.setOnSettingClickListener(this); // AGREGADO: Configurar listener
        propertyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        propertyRecyclerView.setAdapter(settingsAdapter);
    }

    private void loadUserProfile() {
        if (currentUser != null) {
            // Cargar datos del usuario actual desde Firestore
            db.collection("users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");
                            String profileImagePath = documentSnapshot.getString("profileImage");

                            // Mostrar nombre y email
                            txtName.setText(name != null ? name : "Usuario");
                            txtId.setText(email != null ? email : currentUser.getEmail());

                            // Cargar imagen de perfil
                            loadProfileImage(profileImagePath);
                        } else {
                            // Si no existe el documento, usar datos básicos
                            txtName.setText("Usuario");
                            txtId.setText(currentUser.getEmail());
                            loadProfileImage(null);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error al cargar perfil: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        // Usar datos básicos en caso de error
                        txtName.setText("Usuario");
                        txtId.setText(currentUser.getEmail());
                        loadProfileImage(null);
                    });
        }
    }

    private void loadProfileImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            StorageReference imageRef = storage.getReference().child(imagePath);
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(this)
                        .load(uri)
                        .placeholder(R.drawable.hunter)
                        .error(R.drawable.error_icon)
                        .circleCrop()
                        .into(imgProfile);
            }).addOnFailureListener(exception -> {
                Glide.with(this)
                        .load(R.drawable.error_icon)
                        .circleCrop()
                        .into(imgProfile);
            });
        } else {
            Glide.with(this)
                    .load(R.drawable.error_icon)
                    .circleCrop()
                    .into(imgProfile);
        }
    }

    private void loadSettingsFromFirebase() {
        // CORREGIDO: Cargar todos los settings sin filtro de usuario
        db.collection("settings")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        settingsList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Setting setting = document.toObject(Setting.class);
                            if (setting != null) {
                                setting.setDocumentId(document.getId()); // AGREGADO: Asignar ID del documento
                                settingsList.add(setting);
                            }
                        }
                        settingsAdapter.updateList(settingsList); // CORREGIDO: Usar updateList del adapter

                        // Debug: Mostrar cuántos settings se cargaron
                        Toast.makeText(getContext(), "Settings cargados: " + settingsList.size(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error al cargar configuraciones: " +
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // AGREGADO: Implementar métodos del listener del adapter
    @Override
    public void onSettingClick(Setting setting, int position) {
        Toast.makeText(getContext(), "Setting: " + setting.getName(), Toast.LENGTH_SHORT).show();
        // Aquí puedes implementar la navegación según el setting seleccionado
    }

    @Override
    public void onSettingLongClick(Setting setting, int position) {
        Toast.makeText(getContext(), "Long click en: " + setting.getName(), Toast.LENGTH_SHORT).show();
        // Aquí puedes implementar opciones como editar o eliminar
    }

    // Método público para actualizar la lista desde otra parte de la app
    public void refreshSettings() {
        loadSettingsFromFirebase();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Actualizar datos cuando el fragment vuelve a ser visible
        loadUserProfile();
        loadSettingsFromFirebase();
    }
}