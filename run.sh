#!/bin/bash

# Configuration
SRC_DIR="src/main/java"
OUTPUT_DIR="out"
LIB_DIR="libs"
JSON_JAR="$LIB_DIR/json-20240303.jar"  # Remplacez par le nom exact du fichier JAR
CONFIG_FILE="src/main/json/file-config.json"  # Chemin du fichier JSON de configuration

# Vérification de la bibliothèque JSON
if [ ! -f "$JSON_JAR" ]; then
    echo "Erreur : Le fichier $JSON_JAR est introuvable."
    echo "Veuillez télécharger la bibliothèque JSON et la placer dans le dossier $LIB_DIR."
    exit 1
fi

# Vérification du fichier de configuration JSON
if [ ! -f "$CONFIG_FILE" ]; then
    echo "Erreur : Le fichier de configuration $CONFIG_FILE est introuvable."
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

# Exécution de l'application principale
# Ajustez ici si Sgbd est dans un package, par exemple : java -cp "$OUTPUT_DIR:$JSON_JAR" monpackage.Sgbd "$CONFIG_FILE"
echo "Exécution de l'application principale avec le fichier de configuration $CONFIG_FILE..."
java -cp "$OUTPUT_DIR:$JSON_JAR" Sgbd "$CONFIG_FILE"
