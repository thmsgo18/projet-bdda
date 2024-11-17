#!/bin/bash

# Configuration
SRC_DIR="src/main/java"
OUTPUT_DIR="out"
LIB_DIR="libs"
JSON_JAR="$LIB_DIR/json-20240303.jar" # Remplace par le nom exact du fichier JAR

# Vérification que la bibliothèque JSON existe
if [ ! -f "$JSON_JAR" ]; then
    echo "Erreur : Le fichier $JSON_JAR est introuvable."
    echo "Veuillez télécharger la bibliothèque JSON et la placer dans le dossier $LIB_DIR."
    exit 1
fi

# Création du dossier pour les fichiers compilés
echo "Création du répertoire de sortie : $OUTPUT_DIR"
mkdir -p $OUTPUT_DIR

# Compilation
echo "Compilation des fichiers Java..."
javac -d $OUTPUT_DIR -cp "$JSON_JAR" $SRC_DIR/*.java
if [ $? -ne 0 ]; then
    echo "Erreur lors de la compilation."
    exit 1
fi

echo "Compilation réussie."

# Exécution
echo "Exécution de l'application principale..."
java -cp "$OUTPUT_DIR:$JSON_JAR" RelationTest
