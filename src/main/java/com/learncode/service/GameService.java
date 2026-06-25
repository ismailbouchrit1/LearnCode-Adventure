package com.learncode.service;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service contenant toutes les données des 3 jeux
 * et la logique de validation / scoring.
 */
@Service
public class GameService {

    // ══════════════════════════════════════════════════════════════
    //  JEU 1 : PUZZLES PYTHON
    // ══════════════════════════════════════════════════════════════

    public record PuzzleLine(String code, String hint) {}

    public record Puzzle(
        String id, String difficulte, String titre,
        List<PuzzleLine> lignes, List<Integer> solution
    ) {}

    private static final List<Puzzle> PUZZLES = List.of(

        new Puzzle("p1", "Debutant", "Bonjour le monde",
            List.of(new PuzzleLine("print(\"Bonjour le monde !\")", "Afficher un message")),
            List.of(0)),

        new Puzzle("p2", "Debutant", "Saisie et affichage",
            List.of(
                new PuzzleLine("# Programme de salutation",         "Commentaire en premier"),
                new PuzzleLine("nom = input(\"Quel est ton nom ? \")", "Lire l entree"),
                new PuzzleLine("print(\"Bonjour\", nom)",            "Afficher le resultat")
            ),
            List.of(0, 1, 2)),

        new Puzzle("p3", "Debutant", "Calcul simple",
            List.of(
                new PuzzleLine("a = 10",                     "Variable a"),
                new PuzzleLine("b = 20",                     "Variable b"),
                new PuzzleLine("somme = a + b",              "Calcul"),
                new PuzzleLine("print(\"Resultat :\", somme)", "Afficher")
            ),
            List.of(0, 1, 2, 3)),

        new Puzzle("p4", "Intermediaire", "Condition if / else",
            List.of(
                new PuzzleLine("age = int(input(\"Ton age : \"))", "Lire entier"),
                new PuzzleLine("if age >= 18:",                    "Condition"),
                new PuzzleLine("    print(\"Tu es majeur !\")",    "Bloc if"),
                new PuzzleLine("else:",                            "Sinon"),
                new PuzzleLine("    print(\"Tu es mineur.\")",     "Bloc else")
            ),
            List.of(0, 1, 2, 3, 4)),

        new Puzzle("p5", "Intermediaire", "Boucle for",
            List.of(
                new PuzzleLine("print(\"Table de 2\")",           "Titre"),
                new PuzzleLine("for i in range(1, 11):",          "Boucle"),
                new PuzzleLine("    print(i, \"x 2 =\", i * 2)", "Corps")
            ),
            List.of(0, 1, 2)),

        new Puzzle("p6", "Avance", "Fonction Python",
            List.of(
                new PuzzleLine("def saluer(nom):",               "Definir"),
                new PuzzleLine("    message = \"Bonjour \" + nom","Construire"),
                new PuzzleLine("    return message",              "Retourner"),
                new PuzzleLine("resultat = saluer(\"Abir\")",    "Appeler"),
                new PuzzleLine("print(resultat)",                 "Afficher")
            ),
            List.of(0, 1, 2, 3, 4))
    );

    public List<Puzzle> getPuzzles() { return PUZZLES; }

    public Puzzle getPuzzleById(String id) {
        return PUZZLES.stream()
            .filter(p -> p.id().equals(id))
            .findFirst()
            .orElse(PUZZLES.get(0));
    }

    public Puzzle getPuzzleAleatoire() {
        return PUZZLES.get(new Random().nextInt(PUZZLES.size()));
    }

    public boolean validerPuzzle(String id, List<Integer> reponse) {
        Puzzle p = getPuzzleById(id);
        return p.solution().equals(reponse);
    }

    // ══════════════════════════════════════════════════════════════
    //  JEU 2 : QUESTIONS QUIZ RUNNER
    // ══════════════════════════════════════════════════════════════

    public record Question(
        String id, String difficulte, String question,
        List<String> options, int bonneReponse, String explication
    ) {}

    private static final Map<String, List<Question>> QUESTIONS = new HashMap<>();

    static {
        QUESTIONS.put("easy", List.of(
            new Question("q1","easy","Que fait print() ?",
                List.of("Lire une entree","Afficher du texte","Supprimer","Calculer"), 1,
                "print() affiche du texte dans la console."),
            new Question("q2","easy","Comment declare-t-on une variable en Python ?",
                List.of("var x = 5","int x = 5","x = 5","declare x"), 2,
                "En Python on ecrit simplement x = 5."),
            new Question("q3","easy","Quel symbole commence un commentaire Python ?",
                List.of("//","/* */","#","--"), 2,
                "Le # commence un commentaire."),
            new Question("q4","easy","Que retourne 2 + 3 en Python ?",
                List.of("23","5","6","Erreur"), 1,
                "2 + 3 = 5."),
            new Question("q5","easy","input() sert a :",
                List.of("Afficher","Calculer","Lire une saisie","Importer"), 2,
                "input() lit ce que l utilisateur tape.")
        ));

        QUESTIONS.put("medium", List.of(
            new Question("q6","medium","range(5) genere :",
                List.of("[1,2,3,4,5]","[0,1,2,3,4]","[0,1,2,3,4,5]","Rien"), 1,
                "range(5) genere 0,1,2,3,4."),
            new Question("q7","medium","'if age >= 18:' est :",
                List.of("Une boucle","Une fonction","Une condition","Une variable"), 2,
                "if est une instruction conditionnelle."),
            new Question("q8","medium","len('Bonjour') retourne :",
                List.of("6","7","8","Erreur"), 1,
                "Bonjour contient 7 caracteres."),
            new Question("q9","medium","for i in range(3) s execute :",
                List.of("2 fois","3 fois","4 fois","1 fois"), 1,
                "range(3) donne 0,1,2 donc 3 iterations."),
            new Question("q10","medium","Bonne syntaxe de liste ?",
                List.of("{1,2,3}","(1,2,3)","[1,2,3]","<1,2,3>"), 2,
                "Les listes utilisent [ ].")
        ));

        QUESTIONS.put("hard", List.of(
            new Question("q11","hard","list.append(x) fait :",
                List.of("Supprime x","Ajoute x a la fin","Trie","Compte"), 1,
                "append() ajoute a la fin."),
            new Question("q12","hard","type(3.14) retourne :",
                List.of("int","str","float","num"), 2,
                "3.14 est un float."),
            new Question("q13","hard","def maFonction(): est :",
                List.of("Un appel","Une declaration","Un import","Une condition"), 1,
                "def declare une fonction."),
            new Question("q14","hard","'2' * 3 donne :",
                List.of("6","Erreur","222","2x3"), 2,
                "'2' * 3 repete la chaine : 222."),
            new Question("q15","hard","dict.keys() retourne :",
                List.of("Les valeurs","Les cles","Les paires","La longueur"), 1,
                "keys() retourne les cles.")
        ));
    }

    public Question getQuestionAleatoire(String diff) {
        List<Question> pool = QUESTIONS.getOrDefault(diff, QUESTIONS.get("easy"));
        return pool.get(new Random().nextInt(pool.size()));
    }

    public boolean validerReponse(String questionId, int reponse) {
        return QUESTIONS.values().stream()
            .flatMap(List::stream)
            .filter(q -> q.id().equals(questionId))
            .findFirst()
            .map(q -> q.bonneReponse() == reponse)
            .orElse(false);
    }

    // ══════════════════════════════════════════════════════════════
    //  JEU 3 : MEMORY CHALLENGE
    // ══════════════════════════════════════════════════════════════

    public record PaireMemory(String motCle, String definition) {}

    private static final List<PaireMemory> TOUTES_PAIRES = List.of(
        new PaireMemory("if",       "condition"),
        new PaireMemory("print()",  "afficher"),
        new PaireMemory("input()",  "saisir"),
        new PaireMemory("list",     "liste"),
        new PaireMemory("for",      "boucle"),
        new PaireMemory("def",      "fonction"),
        new PaireMemory("int()",    "entier"),
        new PaireMemory("str()",    "texte"),
        new PaireMemory("True",     "vrai"),
        new PaireMemory("False",    "faux"),
        new PaireMemory("while",    "repeter si"),
        new PaireMemory("return",   "retourner")
    );

    public List<PaireMemory> getPaires(int n) {
        List<PaireMemory> copy = new ArrayList<>(TOUTES_PAIRES);
        Collections.shuffle(copy);
        return copy.subList(0, Math.min(n, copy.size()));
    }

    // ══════════════════════════════════════════════════════════════
    //  CONSTANTES SCORING
    // ══════════════════════════════════════════════════════════════
    public static final int PTS_PUZZLE  = 10;
    public static final int PTS_QUIZ    = 20;
    public static final int PTS_MEMORY  = 5;
    public static final int PTS_ETOILE  = 2;
}
