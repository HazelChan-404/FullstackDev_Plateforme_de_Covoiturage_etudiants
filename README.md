# Plateforme de Covoiturage Étudiants

Une plateforme web full-stack permettant aux étudiants de proposer et réserver des trajets en covoiturage. Développée avec **Vaadin Flow**, **Spring Boot** et **MySQL**.

---

## 📖 À propos du projet

**Co-Voiturage** est une application web conçue pour faciliter le partage de trajets entre étudiants. Elle permet de :

- **Proposer des trajets** : Les conducteurs publient leurs trajets (départ, arrivée, date, prix par siège)
- **Rechercher et réserver** : Les passagers cherchent des trajets et effectuent des réservations
- **Gérer les réservations** : Acceptation/rejet par le conducteur, annulation
- **Communiquer** : Messagerie entre conducteur et passagers
- **Noter et évaluer** : Système de notes (conducteur ↔ passager) après le trajet
- **Signaler** : Dénonciation d'utilisateurs, trajets ou messages inappropriés
- **Administration** : Gestion des signalements et des utilisateurs par les admins

---

## ✨ Fonctionnalités principales

| Fonctionnalité | Description |
|----------------|-------------|
| **Authentification** | Inscription, connexion, profil utilisateur |
| **Recherche de trajets** | Filtres par ville, date, prix |
| **Création de trajets** | Départ/arrivée, sièges disponibles, prix, description |
| **Réservations** | Demande de réservation, message au conducteur, confirmation |
| **Mes trajets** | Vue conducteur : trajets publiés et réservations associées |
| **Mes réservations** | Vue passager : réservations en attente, acceptées, passées |
| **Notifications** | Alertes pour nouvelles réservations, acceptations, etc. |
| **Profil utilisateur** | Bio, photo, statistiques (notes, nombre de trajets) |
| **Messagerie** | Échanges entre conducteur et passagers par trajet |
| **Avis et notes** | Notation 1–5 étoiles (conducteur / passager) |
| **Signalements** | Rapport d'utilisateur, trajet ou message |
| **Espace admin** | Gestion des signalements, utilisateurs |

---

## 🛠 Technologies utilisées

| Couche | Technologies |
|--------|--------------|
| **Frontend** | Vaadin Flow 24, Lumo Theme |
| **Backend** | Spring Boot 3.5, Spring Data JPA |
| **Base de données** | MySQL 8 |
| **Sécurité** | Spring Security Crypto (BCrypt) |
| **Java** | 21 |

---

## 📁 Structure du projet

```
covoiturage_full_stack/
├── app/                                    # Application principale
│   ├── src/main/java/com/covoiturage/
│   │   ├── Application.java               # Point d'entrée Spring Boot
│   │   ├── model/                          # Entités JPA
│   │   │   ├── User.java
│   │   │   ├── Trip.java
│   │   │   ├── Booking.java
│   │   │   ├── Review.java
│   │   │   ├── Report.java
│   │   │   ├── Message.java
│   │   │   └── Notification.java
│   │   ├── repository/                     # Spring Data JPA
│   │   ├── service/                        # Logique métier
│   │   ├── dto/                            # Data Transfer Objects
│   │   ├── views/                          # Pages Vaadin
│   │   │   ├── HomeView.java
│   │   │   ├── SearchTripsView.java
│   │   │   ├── CreateTripView.java
│   │   │   ├── MyTripsView.java
│   │   │   ├── MyBookingsView.java
│   │   │   ├── ProfileView.java
│   │   │   ├── LoginView.java
│   │   │   ├── RegisterView.java
│   │   │   ├── AdminView.java
│   │   │   └── ...
│   │   └── security/                       # Configuration sécurité
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── src/main/frontend/themes/
│   └── pom.xml
├── README.md
└── application.properties.example
```

---

## 🚀 Démarrage rapide

### Prérequis

- **JDK 21**
- **Maven 3.8+**
- **MySQL 8**
- **Node.js 18+** (pour le frontend Vaadin)

### 1. Cloner le dépôt

```bash
git clone https://github.com/HazelChan-404/Plateforme_de_Covoiturage_etudiants.git
cd Plateforme_de_Covoiturage_etudiants
```

### 2. Configurer la base de données

Créer une base MySQL :

```sql
CREATE DATABASE covoiturage_full_stack CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Copier le fichier de configuration :

```bash
cp application.properties.example app/src/main/resources/application.properties
```

Modifier `app/src/main/resources/application.properties` :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/covoiturage_full_stack
spring.datasource.username=root
spring.datasource.password=VOTRE_MOT_DE_PASSE
```

### 3. Lancer l'application

```bash
cd app
./mvnw spring-boot:run
```

L'application est accessible sur : **http://localhost:8080**

---

## 📦 Build production

```bash
cd app
./mvnw -Pproduction package
```

Le JAR exécutable se trouve dans `app/target/app-1.0-SNAPSHOT.jar`.

---

## 🐳 Docker (optionnel)

```bash
cd app
docker build -t covoiturage:latest .
docker run -p 8080:8080 covoiturage:latest
```

---

## ⚙️ Configuration

| Propriété | Description | Défaut |
|-----------|-------------|--------|
| `server.port` | Port du serveur | 8080 |
| `spring.datasource.url` | URL MySQL | jdbc:mysql://localhost:3306/covoiturage_full_stack |
| `spring.jpa.hibernate.ddl-auto` | Création des tables | update |

---

## 👥 Rôles

- **Visiteur** : Recherche de trajets, inscription, connexion
- **Utilisateur** : Création de trajets, réservations, profil, messagerie
- **Admin** : Gestion des signalements (utilisateurs avec `isVerified = true` dans la base)

---

## 📄 Licence

Projet réalisé dans le cadre du cours M2 Backend/Frontend.
