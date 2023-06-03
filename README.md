<p align="center"><img src="/app/src/main/res/mipmap-xhdpi/ic_launcher.png"></p>
<h2 align="center">Travelogue</h2>
<p align="center">Créez des souvenirs, nous les sauvegardons pour vous</p>


# À propos

Dans le module « Développement Mobile 1 (Android) » de la première année du Master Informatique parcours « Cloud Computing et Mobilité » de l'INSSET (Institut Supérieur des Sciences Et Techniques, situé à Saint-Quentin), nous devons réaliser une application Android.  
Le sujet est fixé par notre professeur, M. Logé.  
Nous devons créer une application de carnet de voyage.

# Cahier des charges

Le sujet propose différents niveaux de difficultés qui vous permettront de gagner différents points.  

- La version minimaliste vous rapportera au maximum 10/20 :  
Dans cette version, votre application permettra de créer des voyages.  
Entre le moment ou un voyage commence et le moment où il se termine, votre application enregistrera périodiquement (cette période devra pouvoir être configurée) ou à la demande dans une base de données SQLite les instants (date, heure, minute, seconde) et les coordonnées GPS de votre périple.   
Votre application permettra le regroupement des points GPS dans un fichier .gpx/.kml et elle donnera la possibilité à l’utilisateur de l’envoyer par mail à un destinataire.  Cette version de l’application vous donnera l’occasion de mettre en application un grand nombre de notions vu ensemble.   
Elle vous permettra également de découvrir comment utiliser le GPS sous Android, comment utiliser le SGBD SQLite embarqué par tout périphérique Android, comment écrire/lire un fichier sous Android et enfin comment envoyer un courriel à partir d’une application Android.
- La version intermédiaire vous rapportera au maximum 14/20 :  
Dans cette version, votre application ne sauvera pas nécessairement ses données dans une base de données locale SQLite mais dans une base de données Firebase/Firestore.  
Elle proposera en plus la possibilité de visualiser votre voyage sur une GoogleMap. La fonction d’envoi par courriel de fichiers .gpx/.kml demandée dans la version minimaliste devra également être proposée.

- La version évoluée vous rapportera au maximum 17/20 :
Cette version de l’application reprendra la version intermédiaire.  
Elle permettra en plus au voyageur de prendre des photos qui seront associées à une position durant le voyage et qui seront stockées dans un Storage (sur Firebase).  
Des markers sur la GoogleMap permettront de donner accès aux photo associées (soit en les visualisant soit en en donnant une url).

- La super version vous rapportera au maximum 20/20 :  
Cette version de l’application reprendra la version évoluée.  
Elle permettra en plus au voyageur de prendre des photos qui seront associées à une position durant le voyage et qui seront stockées en utilisant ’API Google Photo.  
Nul besoin dans ce cas de stocker les photos dans Storage.

# Notre groupe

Notre groupe est composé de :

- [Sharonn Zounon](https://github.com/SharonnElfride),
- [Augustin Desainfucien](https://github.com/augustinde),
- [Gaëtan LE HEURT-FINOT](https://github.com/gaetanlhf).

Nous avons choisi de réaliser la « super version ».  
Néanmoins, l'utilisation de la bibliothèque Java de Google Photos API en parallèle de celles de Firebase est impossible : elles utilisent des versions de bibliothèques de Protobuf incompatibles (protobuf-java vs protobuf-javalite) entrainant des soucis de classes dupliquées.
Ce problème est connu depuis 2019 : 
- https://github.com/google/java-photoslibrary/issues/22 
- https://github.com/google/java-photoslibrary/issues/34 

> We'll see if we can investigate this again, but currently Android is not supported by this client library.

