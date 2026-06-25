package com.learncode;

import com.learncode.model.Student;
import com.learncode.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * ╔══════════════════════════════════════════════════════╗
 * ║   LearnCode Adventure — Spring Boot                 ║
 * ║   Lancer : clic droit → Run                        ║
 * ║   Accès  : http://localhost:8080                   ║
 * ╚══════════════════════════════════════════════════════╝
 */
@SpringBootApplication
public class LearnCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearnCodeApplication.class, args);
        System.out.println("\n");
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║  LearnCode Adventure est lance !             ║");
        System.out.println("║  Ouvre ton navigateur :                      ║");
        System.out.println("║  >>> http://localhost:8080 <<<               ║");
        System.out.println("╚══════════════════════════════════════════════╝");
    }

    /**
     * Insère les données de démonstration au démarrage
     * si la base est vide (premier lancement).
     */
    @Bean
    CommandLineRunner initDatabase(StudentRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                System.out.println(">>> Insertion des donnees de demonstration...");

                repo.saveAll(List.of(
                    createStudent("Abir",    "girl", 350, 4, 8,  12, 3,
                        "Debutant Python,Expert Puzzle,Roi du Quiz,Centenaire"),
                    createStudent("Youssef", "boy",  290, 3, 5,  9,  2,
                        "Debutant Python,Expert Puzzle"),
                    createStudent("Sara",    "girl", 240, 3, 4,  7,  1,
                        "Debutant Python"),
                    createStudent("Amine",   "boy",  180, 2, 3,  5,  1,
                        "Debutant Python"),
                    createStudent("Nour",    "girl", 120, 2, 2,  3,  0,
                        ""),
                    createStudent("Karim",   "boy",  95,  1, 1,  2,  0,
                        ""),
                    createStudent("Lina",    "girl", 60,  1, 0,  1,  0,
                        "")
                ));

                System.out.println(">>> " + repo.count() + " joueurs inseres !");
            } else {
                System.out.println(">>> Base de donnees : " + repo.count() + " joueurs trouves.");
            }
        };
    }

    private Student createStudent(String nom, String perso,
                                   int score, int niveau,
                                   int puzzles, int quiz, int memory,
                                   String badges) {
        Student s = new Student(nom, perso);
        s.setScore(score);
        s.setNiveau(niveau);
        s.setPuzzlesResolus(puzzles);
        s.setQuizReussis(quiz);
        s.setPartiesMemory(memory);
        s.setBadges(badges);
        return s;
    }
}
