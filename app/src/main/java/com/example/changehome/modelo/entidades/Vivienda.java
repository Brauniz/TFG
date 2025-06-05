package com.example.changehome.modelo.entidades;

import com.google.firebase.firestore.DocumentId;
import java.io.Serializable;

public class Vivienda implements Serializable {
    @DocumentId
    private String documentId; // ID del documento de Firestore
    private String imagen;     // URL de la imagen
    private String titulo;
    private String subtitulo;
    private String descripcion;
    private String ciudad;     // Ciudad donde se ubica la vivienda
    private String creadorId;  // ID del usuario que creó la vivienda

    // Constructor vacío requerido por Firebase
    public Vivienda() {
    }

    // Constructor con parámetros
    public Vivienda(String imagen, String titulo, String subtitulo, String descripcion, String ciudad, String creadorId) {
        this.imagen = imagen;
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.descripcion = descripcion;
        this.ciudad = ciudad;
        this.creadorId = creadorId;
    }

    // Getters y Setters
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCreadorId() {
        return creadorId;
    }

    public void setCreadorId(String creadorId) {
        this.creadorId = creadorId;
    }

    @Override
    public String toString() {
        return "Vivienda{" +
                "documentId='" + documentId + '\'' +
                ", imagen='" + imagen + '\'' +
                ", titulo='" + titulo + '\'' +
                ", subtitulo='" + subtitulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", creadorId='" + creadorId + '\'' +
                '}';
    }
}