package com.example.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer; // <-- IMPORT BIEN AJOUTÉ
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filtre(HttpSecurity http, ClientRegistrationRepository repo) throws Exception {
        OidcClientInitiatedLogoutSuccessHandler dco = new OidcClientInitiatedLogoutSuccessHandler(repo);
        dco.setPostLogoutRedirectUri("http://localhost:5173/");

        http
                .cors(Customizer.withDefaults()) 
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("http://localhost:5173/", true))
                .logout(out -> out
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(dco)
                );
        return http.build();
    }
}