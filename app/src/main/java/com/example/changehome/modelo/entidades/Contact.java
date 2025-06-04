package com.example.changehome.modelo.entidades;

import com.google.firebase.firestore.DocumentId;

public class Contact {

    @DocumentId
    private String documentId;
    private String name;
    private String email;
    private String uid; // UID de Firebase Auth
    private String profileImageUrl;
    private long createdAt;

    // Constructor vacío requerido para Firebase
    public Contact() {}

    // Constructor con parámetros
    public Contact(String name, String email, String uid) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.createdAt = System.currentTimeMillis();
    }

    // Constructor completo
    public Contact(String name, String email, String uid, String profileImageUrl) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = System.currentTimeMillis();
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
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "documentId='" + documentId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", uid='" + uid + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}