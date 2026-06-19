# Projet de gestion et partage de fichiers en ligne

Ce projet est une application web qui permet à des utilisateurs de stocker leurs fichiers en ligne, de les télécharger, de les supprimer et de les partager de manière dynamique avec d'autres utilisateurs inscrits sur la plateforme.

## Fonctionnalités principales

1. Gestion des fichiers : Chaque utilisateur connecté peut uploader ses propres fichiers, voir la liste de ses fichiers, les ouvrir (télécharger) ou les supprimer.
2. Contraintes de formats : L'application accepte uniquement les extensions suivantes : pdf, xlsx, xls, doc, docx, mp3 et mp4.
3. Partage dynamique : Un utilisateur peut choisir un autre membre de la plateforme dans une liste déroulante mise à jour en temps réel pour lui partager un fichier. Les fichiers partagés apparaissent dans une section dédiée chez le destinataire.
4. Authentification et inscription : La sécurité, la connexion et l'inscription des nouveaux comptes sont entièrement gérées par Keycloak.

## Architecture du projet

L'application est découpée en trois grandes parties :

* Frontend (React) : L'interface graphique avec laquelle l'utilisateur interagit. Elle tourne par défaut sur le port 5173.
* BFF - Backend For Frontend (Spring Boot) : Il sert de passerelle unique pour le Frontend sur le port 8081. Il gère la session utilisateur, communique avec Keycloak pour récupérer les informations de sécurité (comme la liste des utilisateurs) et relaie les requêtes légitimes vers l'API de stockage.
* API Storage Service (Spring Boot) : Le microservice situé sur le port 8082 qui s'occupe uniquement de stocker les fichiers sur le disque, de les supprimer et d'enregistrer leurs informations en base de données.
* Base de données : PostgreSQL est utilisé pour sauvegarder l'historique des fichiers et l'état des partages.

## Prérequis pour lancer le projet

Avant de démarrer l'application, il faut s'assurer d'avoir installé :
* Java 17 ou supérieur
* Node.js et npm
* PostgreSQL
* Un serveur Keycloak 

## Configuration et Lancement

### 1. Base de données PostgreSQL
Créer une base de données nommée selon les configurations du projet. Les tables nécessaires (Fichier et Partage) sont automatiquement générées par Hibernate au démarrage du service de stockage.

### 2. Configuration Keycloak
Dans la console d'administration Keycloak, il faut :
* Créer un Realm nommé gestion-fichiers.
* Activer l'option d'inscription automatique des utilisateurs (User Registration) dans les paramètres du Realm.
* Créer un client pour l'application avec les redirections vers le port 5173.
* Créer un client confidentiel nommé bff2-admin-client avec le Service Account activé. Dans ses rôles de compte de service, lui attribuer les droits query-users et view-users du client système realm-management (nécessaire pour l'affichage dynamique des utilisateurs).

### 3. Lancement du Backend For Frontend (BFF)
Ajouter le secret du client Keycloak généré dans les propriétés ou le code du contrôleur, puis lancer le BFF :
```bash
cd bff
./mvnw spring-boot:run
```
Le serveur démarre sur http://localhost:8081

### 4. Lancement de l'API Storage
Lancer le microservice de stockage :
```bash
cd demo
./mvnw spring-boot:run
```
Le serveur démarre sur http://localhost:8082

### 5. Lancement du Frontend 
Installer les dépendances de Node puis lancer le serveur de développement Vite : 
```bash
cd frontend
npm install 
npm run dev
```
L'interface est accessible sur http://localhost:5173