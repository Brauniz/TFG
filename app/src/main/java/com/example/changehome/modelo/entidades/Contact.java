package com.example.changehome.modelo.entidades;

import com.google.firebase.firestore.DocumentId;

public class Contact {
    @DocumentId
    private String id; // ID del documento en Firebase
    private String name;
    private String userName;
    private String imageUrl;

    public Contact() {
        // Constructor vacío necesario para Firebase
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", userName='" + userName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}