package com.example.bff.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/my-files")
public class BffController {

    private final RestClient rc = RestClient.create();
    private final String urlApi = "http://localhost:8082/api/my-files";

    @GetMapping
    public ResponseEntity<String> recupFichs(@RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient cl) {
        String tok = cl.getAccessToken().getTokenValue();
        return rc.get()
                .uri(urlApi)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tok)
                .retrieve()
                .toEntity(String.class);
    }

    @GetMapping("/me")
    public java.util.Map<String, String> moi(@org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.oauth2.core.user.OAuth2User usr) {
        String nom = usr.getAttribute("preferred_username");
        return java.util.Map.of("nom", nom != null ? nom : usr.getName());
    }

    @GetMapping("/shared")
    public ResponseEntity<String> recupPartages(@RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient cl) {
        String tok = cl.getAccessToken().getTokenValue();
        return rc.get()
                .uri(urlApi + "/shared")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tok)
                .retrieve()
                .toEntity(String.class);
    }

    @GetMapping("/{idF}/dl")
    public ResponseEntity<byte[]> dlFich(@PathVariable Long idF, @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient cl) {
        String tok = cl.getAccessToken().getTokenValue();
        return rc.get()
                .uri(urlApi + "/" + idF + "/dl")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tok)
                .retrieve()
                .toEntity(byte[].class);
    }

    @GetMapping("/api/users")
    public List<Map<String, String>> getAllUsers() {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl("http://localhost:8080")          
                .realm("mira-realm")                         
                .clientId("bff-admin-client")
                .clientSecret("VOTRE_SECRET_COPIE_A_L_ETAPE_1")
                .grantType("client_credentials")
                .build();

        List<UserRepresentation> masterUsers = keycloak.realm("mira-realm").users().list();

        return masterUsers.stream().map(user -> Map.of(
                "id", user.getId(),
                "username", user.getUsername()
        )).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<String> ajoutFich(@RequestParam("fich") MultipartFile fich,
            @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient cl) {
        String tok = cl.getAccessToken().getTokenValue();

        MultiValueMap<String, Object> corps = new LinkedMultiValueMap<>();
        corps.add("fich", fich.getResource());

        return rc.post()
                .uri(urlApi)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tok)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(corps)
                .retrieve()
                .toEntity(String.class);
    }

    @PostMapping("/{idF}/share")
    public ResponseEntity<String> partagerFich(@PathVariable Long idF, @RequestParam("cible") String cible, @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient cl) {
        String tok = cl.getAccessToken().getTokenValue();
        return rc.post()
                .uri(urlApi + "/" + idF + "/share?cible=" + cible)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tok)
                .retrieve()
                .toEntity(String.class);
    }

    @DeleteMapping("/{idF}")
    public ResponseEntity<String> supprFich(@PathVariable Long idF,
            @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient cl) {
        String tok = cl.getAccessToken().getTokenValue();
        return rc.delete()
                .uri(urlApi + "/" + idF)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tok)
                .retrieve()
                .toEntity(String.class);
    }
}