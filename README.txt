# LearnCode Adventure — Spring Boot
# Guide complet pour IntelliJ IDEA Community

═══════════════════════════════════════════════════════════
  RÉSUMÉ : 5 étapes pour lancer le jeu
═══════════════════════════════════════════════════════════

  ÉTAPE 1 → Installer Java 17
  ÉTAPE 2 → Ouvrir le projet dans IntelliJ
  ÉTAPE 3 → Télécharger les dépendances Maven
  ÉTAPE 4 → Lancer LearnCodeApplication.java
  ÉTAPE 5 → Ouvrir http://localhost:8080

═══════════════════════════════════════════════════════════


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 ÉTAPE 1 — Installer Java 17 (si pas déjà fait)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

1. Va sur : https://adoptium.net
2. Clique sur : "Latest LTS release" → Java 17
3. Télécharge le fichier .msi (Windows) ou .pkg (Mac)
4. Installe normalement (suivre les étapes)
5. Pour vérifier : ouvre un terminal et tape :
   java -version
   → Tu dois voir : openjdk version "17.x.x"


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 ÉTAPE 2 — Ouvrir le projet dans IntelliJ
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

1. Extrais le fichier ZIP → tu obtiens le dossier "LearnCodeSpring"
2. Ouvre IntelliJ IDEA Community
3. Clique sur : "Open" (pas "New Project" !)
4. Navigue vers le dossier "LearnCodeSpring"
5. IMPORTANT : sélectionne le dossier qui contient "pom.xml"
6. Clique "OK"
7. Si IntelliJ demande "Trust project?" → clique "Trust Project"


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 ÉTAPE 3 — Télécharger les dépendances Maven
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Après l'ouverture, IntelliJ détecte pom.xml automatiquement.

Option A (automatique) :
  → Une notification apparaît en bas à droite :
    "Maven build scripts found"
  → Clique "Load Maven Project"
  → Attends que la barre de progression en bas finisse
  → (Nécessite Internet — télécharge environ 50 Mo)

Option B (manuel si la notification n'apparaît pas) :
  1. Clique sur l'onglet "Maven" à droite de l'écran
  2. Clique sur le bouton ↺ (Reload All Maven Projects)
  3. Attends la fin du téléchargement

⚠️ SI ERREUR "Cannot resolve symbol" :
  → File → Settings → Build → Build Tools → Maven
  → Maven home path : sélectionne "Bundled (Maven 3)"
  → Clique Apply → OK → Recharge Maven


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 ÉTAPE 4 — Configurer Java 17
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

1. File → Project Structure (ou Ctrl+Alt+Shift+S)
2. Dans "Project" :
   - SDK : sélectionne "17" (ou "Add SDK" → "JDK" → navigue vers Java 17)
   - Language level : "17"
3. Clique "Apply" → "OK"


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 ÉTAPE 5 — Lancer le jeu (1 seul clic !)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Dans le panneau de fichiers à gauche, navigue vers :
  src → main → java → com → learncode → LearnCodeApplication.java

Double-clique sur ce fichier pour l'ouvrir.

Tu verras un triangle vert ▶ à gauche de "public static void main"

Clique sur ce triangle ▶ → "Run 'LearnCodeApplication'"

Attends 5-10 secondes. Dans la console en bas tu verras :
  ╔══════════════════════════════════════════════════════╗
  ║  LearnCode Adventure est lance !                    ║
  ║  Ouvre ton navigateur :                             ║
  ║  >>> http://localhost:8080 <<<                      ║
  ╚══════════════════════════════════════════════════════╝


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 ÉTAPE 6 — Jouer !
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Ouvre ton navigateur (Chrome, Firefox, Edge...)
Va sur : http://localhost:8080

Voilà ! Le jeu fonctionne !


═══════════════════════════════════════════════════════════
 RÉSOLUTION DES ERREURS FRÉQUENTES
═══════════════════════════════════════════════════════════

❌ ERREUR : "Port 8080 already in use"
   → Un autre programme utilise le port 8080
   → Solution : ouvre application.properties
                change : server.port=8080
                en     : server.port=8081
                puis va sur http://localhost:8081

❌ ERREUR : "Cannot resolve symbol 'SpringApplication'"
   → Maven n'a pas encore téléchargé les dépendances
   → Solution : clique sur l'onglet Maven → ↺ Reload

❌ ERREUR : "UnsupportedClassVersionError"
   → Java version trop ancienne
   → Solution : installe Java 17 (voir Étape 1)

❌ ERREUR : "No suitable driver found for jdbc:h2"
   → Maven n'a pas téléchargé H2
   → Solution : vérifie la connexion Internet → Reload Maven

❌ Le jeu s'ouvre mais la page est blanche
   → Vide le cache du navigateur (Ctrl+Shift+R)
   → Essaie un autre navigateur

❌ ERREUR dans pom.xml (lignes rouges)
   → C'est normal pendant le téléchargement
   → Attends que Maven finisse → les erreurs disparaissent


═══════════════════════════════════════════════════════════
 STRUCTURE DU PROJET
═══════════════════════════════════════════════════════════

LearnCodeSpring/
├── pom.xml                          ← Config Maven (NE PAS MODIFIER)
├── src/main/
│   ├── java/com/learncode/
│   │   ├── LearnCodeApplication.java   ← FICHIER À LANCER ▶
│   │   ├── model/Student.java          ← Modèle élève
│   │   ├── repository/StudentRepository.java ← Base de données
│   │   ├── service/GameService.java    ← Données des jeux
│   │   └── controller/
│   │       ├── MainController.java     ← Pages HTML
│   │       └── ApiController.java      ← API JSON (jeux)
│   └── resources/
│       ├── application.properties      ← Configuration
│       ├── templates/                  ← Pages HTML (Thymeleaf)
│       │   ├── index.html              ← Accueil + personnages
│       │   ├── menu.html               ← Menu principal
│       │   ├── game_puzzle.html        ← Jeu 1 : Puzzle
│       │   ├── game_runner.html        ← Jeu 2 : Runner
│       │   ├── game_memory.html        ← Jeu 3 : Memory
│       │   ├── leaderboard.html        ← Classement
│       │   └── fragments.html          ← HUD réutilisable
│       └── static/
│           ├── css/                    ← Styles
│           └── js/                     ← JavaScript des jeux


═══════════════════════════════════════════════════════════
 LES 3 JEUX
═══════════════════════════════════════════════════════════

🧩 JEU 1 : PUZZLE PYTHON
   Remets les lignes de code dans le bon ordre
   Utilise le drag & drop avec ta souris
   +10 pts si correct / -1 vie si faux
   6 niveaux : Débutant → Avancé

🏃 JEU 2 : RUNNER QUIZ
   Cours et saute par-dessus les obstacles (Espace)
   Collecte des étoiles (+2 pts chacune)
   Entre dans une porte → Question Python en 30 secondes
   +20 pts bonne réponse / -1 vie mauvaise réponse

🧠 JEU 3 : CODE MEMORY
   Retourne les cartes et trouve les paires
   Mot-clé Python ↔ Définition
   +5 pts par paire trouvée + bonus de temps


═══════════════════════════════════════════════════════════
 BASE DE DONNÉES
═══════════════════════════════════════════════════════════

La base H2 se crée AUTOMATIQUEMENT au 1er lancement.
Fichier créé : learncode_db.mv.db (dans le dossier du projet)

Pour voir la base : http://localhost:8080/h2-console
  JDBC URL : jdbc:h2:file:./learncode_db
  User : sa
  Password : (vide)
