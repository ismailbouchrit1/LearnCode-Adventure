package com.learncode.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String nom;

    @Column(nullable = false, length = 10)
    private String personnage = "girl";

    @Column(nullable = false)
    private int score = 0;

    @Column(nullable = false)
    private int niveau = 1;

    @Column(nullable = false)
    private int vies = 3;

    @Column(name = "puzzles_resolus")
    private int puzzlesResolus = 0;

    @Column(name = "quiz_reussis")
    private int quizReussis = 0;

    @Column(name = "parties_memory")
    private int partiesMemory = 0;

    @Column(length = 500)
    private String badges = "";

    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(name = "derniere_connexion")
    private LocalDateTime derniereConnexion = LocalDateTime.now();

    public Student() {}

    public Student(String nom, String personnage) {
        this.nom = nom;
        this.personnage = personnage;
        this.dateCreation = LocalDateTime.now();
    }

    public void addScore(int pts) {
        this.score = Math.max(0, this.score + pts);
        this.niveau = this.score / 100 + 1;
    }

    public boolean perdreVie() {
        if (vies > 0) vies--;
        return vies > 0;
    }

    public void resetVies() { this.vies = 3; }

    public List<String> getBadgesList() {
        if (badges == null || badges.isBlank()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(badges.split(",")));
    }

    public void setBadgesList(List<String> list) {
        this.badges = String.join(",", list);
    }

    public List<String> verifierBadges() {
        List<String> actuels  = getBadgesList();
        List<String> nouveaux = new ArrayList<>();
        check(actuels, nouveaux, "Debutant Python",  score >= 50);
        check(actuels, nouveaux, "Expert Puzzle",    puzzlesResolus >= 5);
        check(actuels, nouveaux, "Roi du Quiz",      quizReussis >= 10);
        check(actuels, nouveaux, "Memoire Pro",      partiesMemory >= 3);
        check(actuels, nouveaux, "Centenaire",       score >= 100);
        check(actuels, nouveaux, "Expert Python",    niveau >= 5);
        check(actuels, nouveaux, "En Feu",           score >= 500);
        if (!nouveaux.isEmpty()) setBadgesList(actuels);
        return nouveaux;
    }

    private void check(List<String> actuels, List<String> nouveaux, String nom, boolean cond) {
        if (cond && !actuels.contains(nom)) { actuels.add(nom); nouveaux.add(nom); }
    }

    // Getters / Setters
    public Long   getId()                        { return id; }
    public void   setId(Long id)                 { this.id = id; }
    public String getNom()                       { return nom; }
    public void   setNom(String nom)             { this.nom = nom; }
    public String getPersonnage()                { return personnage; }
    public void   setPersonnage(String p)        { this.personnage = p; }
    public int    getScore()                     { return score; }
    public void   setScore(int s)                { this.score = s; this.niveau = s/100+1; }
    public int    getNiveau()                    { return niveau; }
    public void   setNiveau(int n)               { this.niveau = n; }
    public int    getVies()                      { return vies; }
    public void   setVies(int v)                 { this.vies = v; }
    public int    getPuzzlesResolus()            { return puzzlesResolus; }
    public void   setPuzzlesResolus(int v)       { this.puzzlesResolus = v; }
    public int    getQuizReussis()               { return quizReussis; }
    public void   setQuizReussis(int v)          { this.quizReussis = v; }
    public int    getPartiesMemory()             { return partiesMemory; }
    public void   setPartiesMemory(int v)        { this.partiesMemory = v; }
    public String getBadges()                    { return badges; }
    public void   setBadges(String b)            { this.badges = b; }
    public LocalDateTime getDateCreation()       { return dateCreation; }
    public void setDateCreation(LocalDateTime d) { this.dateCreation = d; }
    public LocalDateTime getDerniereConnexion()  { return derniereConnexion; }
    public void setDerniereConnexion(LocalDateTime d) { this.derniereConnexion = d; }
}
