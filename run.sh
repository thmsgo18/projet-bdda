#!/bin/bash

# Configuration
SRC_DIR="src/main/java"
OUTPUT_DIR="out"
LIB_DIR="libs"
JSON_JAR="$LIB_DIR/json-20240303.jar"  # Remplacez par le nom exact du fichier JAR
DEFAULT_CONFIG_FILE="file-config.json"  # Fichier JSON de configuration par défaut

# Vérification de la bibliothèque JSON
if [ ! -f "$JSON_JAR" ]; then
    echo "Erreur : Le fichier $JSON_JAR est introuvable."
    echo "Veuillez télécharger la bibliothèque JSON et la placer dans le dossier $LIB_DIR."
    exit 1
fi

# Vérification du fichier de configuration JSON par défaut
if [ ! -f "$DEFAULT_CONFIG_FILE" ]; then
    echo "Erreur : Le fichier de configuration par défaut $DEFAULT_CONFIG_FILE est introuvable."
    echo "Veuillez vérifier que le fichier JSON est bien placé."
    exit 1
fi

# Création du dossier pour les fichiers compilés
echo "Création du répertoire de sortie : $OUTPUT_DIR"
mkdir -p $OUTPUT_DIR

# Compilation de tous les fichiers Java de façon récursive
echo "Compilation des fichiers Java..."
javac -d $OUTPUT_DIR -cp "$JSON_JAR" $(find $SRC_DIR -name "*.java")
if [ $? -ne 0 ]; then
    echo "Erreur lors de la compilation."
    exit 1
fi

echo "Compilation réussie."

# Détection du fichier de configuration fourni en paramètre
CONFIG_FILE="${1:-$DEFAULT_CONFIG_FILE}"

# Vérification du fichier de configuration
if [ ! -f "$CONFIG_FILE" ]; then
    echo "Erreur : Le fichier de configuration $CONFIG_FILE est introuvable."
    exit 1
fi

# Exécution de l'application principale
echo "Exécution de l'application principale avec le fichier de configuration $CONFIG_FILE..."
java -cp "$OUTPUT_DIR:$JSON_JAR" Sgbd "$CONFIG_FILE"
