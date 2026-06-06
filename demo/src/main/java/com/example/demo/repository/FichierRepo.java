package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Fichier;

import java.util.List;

public interface FichierRepo extends JpaRepository<Fichier, Long> {
    List<Fichier> findByIdUtil(String idUtil);
}