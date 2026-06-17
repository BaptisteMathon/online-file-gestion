package com.example.demo.controller;

import com.example.demo.entity.Fichier;
import com.example.demo.entity.Partage;
import com.example.demo.repository.FichierRepo;
import com.example.demo.repository.PartageRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/my-files")
public class FichierCtrl {

    @Autowired
    private FichierRepo repoFich;

    private final String doss = System.getProperty("user.dir") + "/uploads/";

    @PostMapping
    public Fichier ajoutFich(@RequestParam("fich") MultipartFile fich, @AuthenticationPrincipal Jwt jwt)
            throws IOException {
        String idU = jwt.getSubject();
        String nomF = fich.getOriginalFilename();

        List<String> typsOk = List.of("pdf", "xlsx", "xls", "doc", "docx", "mp3", "mp4");
        String typ = nomF.substring(nomF.lastIndexOf(".") + 1).toLowerCase();

        if (!typsOk.contains(typ)) {
            throw new RuntimeException("ErrTyp");
        }

        String chmF = doss + nomF;
        File d = new File(doss);
        if (!d.exists()) {
            d.mkdirs();
        }
        fich.transferTo(new File(chmF));

        Fichier nouvF = new Fichier(nomF, chmF, typ, idU);
        return repoFich.save(nouvF);
    }

    @GetMapping
    public List<Fichier> recupFichs(@AuthenticationPrincipal Jwt jwt) {
        String idU = jwt.getSubject();
        return repoFich.findByIdUtil(idU);
    }

    @DeleteMapping("/{idF}")
    public void supprFich(@PathVariable Long idF) {
        Fichier f = repoFich.findById(idF).orElseThrow();
        File fichPhys = new File(f.getPathFich());
        if (fichPhys.exists()) {
            fichPhys.delete();
        }
        repoFich.delete(f);
    }

    @Autowired
    private PartageRepo repoPart;

    @PostMapping("/{idF}/share")
    public void partagerFich(@PathVariable Long idF, @RequestParam("cible") String cible) {
        Partage p = new Partage(idF, cible);
        repoPart.save(p);
    }

    @GetMapping("/shared")
    public List<Fichier> recupPartages(@AuthenticationPrincipal Jwt jwt) {
        String monNom = jwt.getClaimAsString("preferred_username");
        List<Partage> parts = repoPart.findByCible(monNom);
        return parts.stream()
                .map(p -> repoFich.findById(p.getFichId()).orElse(null))
                .filter(f -> f != null)
                .toList();
    }

    @GetMapping("/{idF}/dl")
    public org.springframework.http.ResponseEntity<byte[]> dlFich(@PathVariable Long idF) throws java.io.IOException {
        Fichier f = repoFich.findById(idF).orElseThrow();
        java.nio.file.Path chm = java.nio.file.Paths.get(f.getPathFich());
        byte[] oct = java.nio.file.Files.readAllBytes(chm);
        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + f.getNameFich() + "\"")
                .body(oct);
    }
}