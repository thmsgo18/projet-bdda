# SGBD - Système de Gestion de Base de Données

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)

> *[English version](README.md)*

Un système de gestion de base de données relationnel développé en Java, implémentant les concepts fondamentaux d'un SGBD : gestion de disque, buffer manager, traitement de requêtes et opérations relationnelles.

---

## Table des matières

- [Fonctionnalités](#-fonctionnalités)
- [Architecture](#-architecture)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Utilisation](#-utilisation)
- [Structure du projet](#-structure-du-projet)
- [Auteurs](#-auteurs)

---

## Fonctionnalités

### Gestion du Disque
- **DiskManager** : Allocation et gestion des pages sur disque
- **PageId** : Système d'identification unique des pages
- Gestion efficace de l'espace disque avec allocation dynamique

### Buffer Manager
- Cache en mémoire pour optimiser les accès disque
- Politique de remplacement des pages (LRU)
- Gestion des pages sales (dirty pages)

### Opérations Relationnelles
- **Scan de relations** : Parcours séquentiel des tuples
- **Projection** : Sélection de colonnes spécifiques
- **Jointure** : Implémentation de page-oriented join
- **Filtrage** : Application de conditions sur les données

### Gestion des Données
- Import de données CSV
- Stockage orienté enregistrements (Record-oriented)
- Itérateurs pour parcourir efficacement les données
- Support des types de données multiples (INT, REAL, VARCHAR, CHAR)

---

## Architecture

Le projet suit une architecture modulaire en couches :

```
┌─────────────────────────────────┐
│    Interface Utilisateur         │
│         (Sgbd.java)              │
└─────────────────────────────────┘
           ↓
┌─────────────────────────────────┐
│    Couche Requête                │
│  (DBManager, Operators)          │
└─────────────────────────────────┘
           ↓
┌─────────────────────────────────┐
│   Couche Relationnelle           │
│  (Relation, Record, ColInfo)     │
└─────────────────────────────────┘
           ↓
┌─────────────────────────────────┐
│     Buffer Manager               │
│   (BufferManager.java)           │
└─────────────────────────────────┘
           ↓
┌─────────────────────────────────┐
│     Disk Manager                 │
│   (DiskManager, PageId)          │
└─────────────────────────────────┘
```

---

## Prérequis

- **Java** : Version 17 ou supérieure
- **Maven** : Version 3.6 ou supérieure
- **Système d'exploitation** : Linux ou macOS (recommandé)

### Vérifier les installations

```bash
# Vérifier Java
java -version

# Vérifier Maven
mvn -version
```

---

## Installation

### 1. Cloner le dépôt

```bash
git clone https://github.com/thmsgo18/projet-bdda.git
cd projet-bdda
```

### 2. Compiler le projet

```bash
mvn clean compile
```

### 3. Rendre le script exécutable

```bash
chmod +x run.sh
```

---

## Utilisation

### Mode par défaut

Utilise le fichier de configuration `file-config.json` :

```bash
./run.sh
```

### Mode personnalisé

Spécifier un fichier de configuration personnalisé :

```bash
./run.sh chemin/vers/configuration.json
```

### Fichier de configuration

Exemple de structure du fichier `file-config.json` :

```json
{
  "dbpath": "DataBase",
  "pagesize": 4096,
  "dm_maxfilesize": 100,
  "bm_buffercount": 2,
  "bm_policy": "LRU"
}
```

**Paramètres :**
- `dbpath` : Chemin du répertoire de la base de données
- `pagesize` : Taille d'une page en octets
- `dm_maxfilesize` : Nombre maximum de pages par fichier
- `bm_buffercount` : Nombre de frames dans le buffer
- `bm_policy` : Politique de remplacement (LRU recommandé)

---

## Structure du projet

```
projet-bdda/
├── src/main/java/
│   ├── buffer/              # Gestion du cache
│   │   └── BufferManager.java
│   ├── espaceDisque/        # Gestion du disque
│   │   ├── DiskManager.java
│   │   ├── PageId.java
│   │   └── DBConfig.java
│   ├── relationnel/         # Modèle relationnel
│   │   ├── Relation.java
│   │   ├── Record.java
│   │   └── ColInfo.java
│   ├── requete/             # Traitement des requêtes
│   │   ├── DBManager.java
│   │   ├── RelationScanner.java
│   │   ├── ProjectOperator.java
│   │   └── PageOrientedJoinOperator.java
│   ├── test/                # Tests unitaires
│   ├── Sgbd.java           # Point d'entrée principal
│   └── Main.java
├── file-config.json         # Configuration par défaut
├── run.sh                   # Script d'exécution
├── pom.xml                  # Configuration Maven
└── README.md
```

---

## Auteurs

- [**Thomas Gourmelen**](https://github.com/thmsgo18)
- [**Ali Traore**](https://github.com/Taliii7)
- [**Valentin Ponnoussamy**](https://github.com/Yvngval)
- [**Malik Djaffer Abdel**](https://github.com/malik439)
