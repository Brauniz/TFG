package com.example.changehome.modelo.entidades;

import com.google.firebase.firestore.DocumentId;

public class Setting {

    @DocumentId
    private String documentId;
    private String name;
    private String imageUrl;
    private long createdAt;
    private long updatedAt;

    // Constructor vacío requerido para Firebase
    public Setting() {}

    // Constructor con parámetros principales
    public Setting(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Constructor completo
    public Setting(String name, String imageUrl, long createdAt, long updatedAt) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters y Setters
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis(); // Actualizar timestamp
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        this.updatedAt = System.currentTimeMillis(); // Actualizar timestamp
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Setting{" +
                "documentId='" + documentId + '\'' +
                ", name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Setting setting = (Setting) obj;
        return documentId != null ? documentId.equals(setting.documentId) : setting.documentId == null;
    }

    @Override
    public int hashCode() {
        return documentId != null ? documentId.hashCode() : 0;
    }
}
