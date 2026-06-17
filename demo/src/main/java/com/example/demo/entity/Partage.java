package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tab_partages")
public class Partage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long fichId;
    private String cible;

    public Partage() {
    }

    public Partage(Long fichId, String cible) {
        this.fichId = fichId;
        this.cible = cible;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFichId() {
        return fichId;
    }

    public void setFichId(Long fichId) {
        this.fichId = fichId;
    }

    public String getCible() {
        return cible;
    }

    public void setCible(String cible) {
        this.cible = cible;
    }
}