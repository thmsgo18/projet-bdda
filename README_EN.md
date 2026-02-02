# 🗄️ DBMS - Database Management System

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Academic-green.svg)]()

> *[Version française](README.md)*

A relational database management system developed in Java, implementing fundamental DBMS concepts: disk management, buffer manager, query processing, and relational operations.

---

## 📋 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Usage](#-usage)
- [Project Structure](#-project-structure)
- [Authors](#-authors)

---

## ✨ Features

### Disk Management
- **DiskManager**: Page allocation and management on disk
- **PageId**: Unique page identification system
- Efficient disk space management with dynamic allocation

### Buffer Manager
- In-memory cache to optimize disk access
- Page replacement policy (LRU)
- Dirty pages management

### Relational Operations
- **Relation Scan**: Sequential tuple traversal
- **Projection**: Selection of specific columns
- **Join**: Page-oriented join implementation
- **Filtering**: Applying conditions on data

### Data Management
- CSV data import
- Record-oriented storage
- Iterators for efficient data traversal
- Multiple data types support (INT, REAL, VARCHAR, CHAR)

---

## 🏗️ Architecture

The project follows a modular layered architecture:

```
┌─────────────────────────────────┐
│    User Interface                │
│         (Sgbd.java)              │
└─────────────────────────────────┘
           ↓
┌─────────────────────────────────┐
│    Query Layer                   │
│  (DBManager, Operators)          │
└─────────────────────────────────┘
           ↓
┌─────────────────────────────────┐
│   Relational Layer               │
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

## 🔧 Prerequisites

- **Java**: Version 17 or higher
- **Maven**: Version 3.6 or higher
- **Operating System**: Linux or macOS (recommended)

### Check installations

```bash
# Check Java
java -version

# Check Maven
mvn -version
```

---

## 📦 Installation

### 1. Clone the repository

```bash
git clone https://github.com/thmsgo18/projet-bdda.git
cd projet-bdda
```

### 2. Compile the project

```bash
mvn clean compile
```

### 3. Make the script executable

```bash
chmod +x run.sh
```

---

## 🚀 Usage

### Default mode

Uses the default configuration file `file-config.json`:

```bash
./run.sh
```

### Custom mode

Specify a custom configuration file:

```bash
./run.sh path/to/configuration.json
```

### Configuration file

Example structure of `file-config.json`:

```json
{
  "dbpath": "DataBase",
  "pagesize": 4096,
  "dm_maxfilesize": 100,
  "bm_buffercount": 2,
  "bm_policy": "LRU"
}
```

**Parameters:**
- `dbpath`: Database directory path
- `pagesize`: Page size in bytes
- `dm_maxfilesize`: Maximum number of pages per file
- `bm_buffercount`: Number of frames in the buffer
- `bm_policy`: Replacement policy (LRU recommended)

---

## 📁 Project Structure

```
projet-bdda/
├── src/main/java/
│   ├── buffer/              # Cache management
│   │   └── BufferManager.java
│   ├── espaceDisque/        # Disk management
│   │   ├── DiskManager.java
│   │   ├── PageId.java
│   │   └── DBConfig.java
│   ├── relationnel/         # Relational model
│   │   ├── Relation.java
│   │   ├── Record.java
│   │   └── ColInfo.java
│   ├── requete/             # Query processing
│   │   ├── DBManager.java
│   │   ├── RelationScanner.java
│   │   ├── ProjectOperator.java
│   │   └── PageOrientedJoinOperator.java
│   ├── test/                # Unit tests
│   ├── Sgbd.java           # Main entry point
│   └── Main.java
├── file-config.json         # Default configuration
├── run.sh                   # Execution script
├── pom.xml                  # Maven configuration
└── README.md
```

---

## 👥 Authors

This project was developed as part of a university project in Computer Science Bachelor's degree (Licence 3).

- [**Thomas Gourmelen**](https://github.com/thmsgo18)
- [**Ali Traore**](https://github.com/Taliii7)
- [**Valentin Ponnoussamy**](https://github.com/Yvngval)
- [**Malik Djaffer Abdel**](https://github.com/malik439)

---

## 📄 License

Academic Project - University of Paris

---

<div align="center">
  <sub>Developed with ☕ and 💻</sub>
</div>
