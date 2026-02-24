# App Co-Voiturage

Module principal de l'application **Plateforme de Covoiturage Étudiants**.

Pour la description complète du projet, l'installation et la configuration, consultez le [README principal](../README.md) à la racine du dépôt.

---

## Structure des sources

```
app/src/main/
├── frontend/
│   └── themes/default/          # Thème Lumo personnalisé
│       ├── styles.css
│       └── theme.json
├── java/com/covoiturage/
│   ├── Application.java        # Point d'entrée Spring Boot
│   ├── model/                  # Entités JPA (User, Trip, Booking, etc.)
│   ├── repository/             # Spring Data JPA
│   ├── service/                # Services métier
│   ├── dto/                    # Objets de transfert
│   ├── views/                  # Vues Vaadin (pages)
│   └── security/               # Authentification, configuration
└── resources/
    └── application.properties
```

---

## Lancer en développement

```bash
./mvnw spring-boot:run
```

## Build production

```bash
./mvnw -Pproduction package
```

## Build Docker

```bash
docker build -t covoiturage:latest .
```
