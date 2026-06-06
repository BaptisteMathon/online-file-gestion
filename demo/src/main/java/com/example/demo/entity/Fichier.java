package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tab_fichiers")
public class Fichier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameFich;
    private String pathFich;
    private String typeFich;
    private String idUtil;

    public Fichier() {
    }

    public Fichier(String nameFich, String pathFich, String typeFich, String idUtil) {
        this.nameFich = nameFich;
        this.pathFich = pathFich;
        this.typeFich = typeFich;
        this.idUtil = idUtil;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameFich() {
        return nameFich;
    }

    public void setNameFich(String nameFich) {
        this.nameFich = nameFich;
    }

    public String getPathFich() {
        return pathFich;
    }

    public void setPathFich(String pathFich) {
        this.pathFich = pathFich;
    }

    public String getTypeFich() {
        return typeFich;
    }

    public void setTypeFich(String typeFich) {
        this.typeFich = typeFich;
    }

    public String getIdUtil() {
        return idUtil;
    }

    public void setIdUtil(String idUtil) {
        this.idUtil = idUtil;
    }
}