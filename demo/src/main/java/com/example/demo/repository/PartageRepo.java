package com.example.demo.repository;

import com.example.demo.entity.Partage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PartageRepo extends JpaRepository<Partage, Long> {
    List<Partage> findByCible(String cible);
}