# My Spring Social

*A small social media aggregator*

## Description sujet

Il s'agit de créer une application permettant d'agréger toutes les activités d'un utilisateur sur les réseaux sociaux.
L'application conservera un flux complet et chronologique de ce qu'un utilisateur a fait sur l'ensemble de ses réseaux sociaux y compris les interactions que d'autres utilisateurs ont fait sur ces actions (like, commentaires, réponses...)
L'application pourrait donc être uniquement côté serveur (dans ce cas elle devra comporter une petite interface web afin de paramétrer son compte et de voir ce qui a été collecté) ou uniquement client (android, ios) afin d'utiliser les connexions aux réseaux sociaux du téléphone lui même et d'éviter à l'utilisateur de devoir donner ses mots de passe ou autorisation d'accès.
L'application devra permettre d'exporter les données dans un format standard (json ou xml) à tout moment.

## Installation

Pour l'installation :
Lancer l'application Maven
* Une base de donnée "my-spring-social" sera créée
* Ouvrir l'application : [localhost:8080](http://localhost:8080)

## Utilisation

Pour utiliser l'application : 
* Sign up : Choix entre Facebook, Twitter ou sans réseau social
* Connections : Configurer les connexions
* Refresh : Permet de réccupérer les informations de réseaux sociaux auxquels on est connecté
* Download : Permet d'exporter les informations reccupérées au format JSON

Si on se déconnecte (Sign out) : 
* Sign in : Choix du moyen de connexion 



