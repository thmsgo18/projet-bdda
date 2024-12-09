# Guide d'Exécution du Programme

Ce guide explique comment exécuter le programme sur un terminal Linux ou macOS. **Assurez-vous de vous placer à la racine du projet : `projet-bdda`.**

---
## Pré-requis

Avant de lancer le programme, vérifiez les éléments suivants :

### 1. Terminal Linux ou macOS
Le script est compatible avec ces systèmes uniquement.
### 2. Java
Assurez-vous que Java est installé sur votre machine. Vous pouvez vérifier sa version avec la commande suivante :
``` bash 
java -version
```
### Script run.sh
Si nécessaire, rendez le script exécutable avec la commande :
```bash
chmod +x run.sh
```
---
## Modes d'Exécution

Le programme peut être lancé de deux manières différentes :

### 1. Exécution avec le fichier de configuration par défaut

Dans ce mode, le programme utilise automatiquement le fichier `file-config.json` situé dans le répertoire courant.

### Commande :
```bash
./run.sh
```
### 2. Exécution avec un fichier de configuration personalisé
Vous pouvez spécifier un fichier de configuration spécifique que le programme utilisera.

### Commande :
```bash
./run.sh cheminFichierConfiguration
```

