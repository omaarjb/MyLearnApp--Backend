# 📚 Backend Spring Boot - Application de Quiz avec Correction Automatique 🎓

Bienvenue dans la partie backend de notre **application de génération de quiz avec correction automatique** ! Ce projet a été conçu pour offrir une API robuste permettant aux **professeurs** de créer des quiz interactifs pour leurs étudiants, tout en simplifiant la gestion et l'évaluation des performances. 🚀

## 🌟 Fonctionnalités

### 👨‍🏫 Pour les Professeurs :
- **Création et gestion de quiz** : Les enseignants peuvent facilement créer des quiz couvrant plusieurs sujets, avec la possibilité de les **éditer** et de les **supprimer** à tout moment. ✏️
- **Génération de quiz avec l'IA** : Grâce à une intégration intelligente, les professeurs peuvent générer des quiz automatiquement en utilisant l'intelligence artificielle 🤖, ce qui permet de gagner du temps et d'améliorer la variété des questions. 🧠

### 🧑‍🎓 Pour les Étudiants :
- **Passage des quiz** : Les étudiants peuvent facilement passer les quiz disponibles, dans différents sujets et niveaux. 📋
- **Correction automatique** : Une fois un quiz terminé, les étudiants reçoivent immédiatement leur **score** 🏆 ainsi que la **correction détaillée** 📑, leur permettant de comprendre leurs erreurs et d'améliorer leurs connaissances. 📈
- **Suivi des performances** : Les étudiants ont accès à leurs **statistiques personnelles** 📊, leur permettant de suivre leur progression au fil du temps. 📈

## 🛠️ Technologies

- **Spring Boot** : Framework Java pour le développement rapide d'applications
- **MySQL** : Base de données relationnelle pour le stockage sécurisé des données
- **JPA/Hibernate** : ORM pour la persistance des données
- **Postman** : Outil pour tester les API REST
- **Clerk** : Gestion des utilisateurs et de l'authentification
- **Ngrok** : Outil pour exposer l'API localement et tester les intégrations
- **API Gemini** : Intégration avec l'IA de Google pour la génération automatique de questions

---

## 🚀 Démarrage avec le Backend

### Prérequis
- **JDK 24** ou supérieur
- **Maven 3.8+**
- **MySQL 8.0+**

### 1️⃣ Configuration de la Base de Données

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/quiz_db
spring.datasource.username=votre_username
spring.datasource.password=votre_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
gemini.api.key=votre_api_key
gemini.model.id=votre_model_id
```

### 2️⃣ Compilation et Lancement
```bash
# Compiler le projet
mvn clean package

# Lancer l'application
java -jar target/quiz-app-backend-0.0.1-SNAPSHOT.jar
```

### 3️⃣ Accès à l'API
- Une fois l'application démarrée, l'API est accessible à:  
  `http://localhost:8080/api/`

## 📚 Ressources Supplémentaires
- 📄 [Documentation Spring Boot](https://spring.io/projects/spring-boot)
- 📘 [Guide de Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- 🔒 [Tutoriel Spring Security](https://spring.io/guides/gs/securing-web/)


