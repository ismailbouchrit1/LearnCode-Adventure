package com.learncode.controller;

import com.learncode.model.Student;
import com.learncode.repository.StudentRepository;
import com.learncode.service.GameService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MainController {

    @Autowired private StudentRepository repo;
    @Autowired private GameService gameSvc;

    private Student getStudent(HttpSession session) {
        return (Student) session.getAttribute("student");
    }

    private boolean isLoggedIn(HttpSession session) {
        return getStudent(session) != null;
    }

    // ══ ACCUEIL ══════════════════════════════════════════════════
    @GetMapping({"/", "/character"})
    public String pageAccueil(Model model, HttpSession session) {
        model.addAttribute("student", getStudent(session));
        return "index";
    }

    @PostMapping("/character")
    public String choisirPersonnage(
            @RequestParam String nom,
            @RequestParam(defaultValue = "girl") String personnage,
            HttpSession session, Model model) {

        if (nom == null || nom.isBlank()) {
            model.addAttribute("erreur", "Le prenom est obligatoire !");
            return "index";
        }
        nom = nom.trim().substring(0, Math.min(nom.trim().length(), 20));

        Optional<Student> existing = repo.findByNomIgnoreCase(nom);
        Student student;
        if (existing.isPresent()) {
            student = existing.get();
            student.setPersonnage(personnage);
            if (student.getVies() <= 0) student.resetVies();
        } else {
            student = new Student(nom, personnage);
        }
        student.setDerniereConnexion(LocalDateTime.now());
        repo.save(student);
        session.setAttribute("student", student);
        return "redirect:/menu";
    }

    // ══ MENU ═════════════════════════════════════════════════════
    @GetMapping("/menu")
    public String pageMenu(HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/character";
        Student s = getStudent(session);
        Student fresh = repo.findByNomIgnoreCase(s.getNom()).orElse(s);
        session.setAttribute("student", fresh);
        model.addAttribute("student", fresh);
        return "menu";
    }

    // ══ PUZZLE ═══════════════════════════════════════════════════
    @GetMapping("/game/puzzle")
    public String pagePuzzle(
            @RequestParam(required = false) String id,
            HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/character";

        Student s = getStudent(session);
        if (s.getVies() <= 0) {
            s.resetVies(); repo.save(s); session.setAttribute("student", s);
        }

        GameService.Puzzle puzzle = (id != null)
            ? gameSvc.getPuzzleById(id)
            : gameSvc.getPuzzleAleatoire();

        List<Integer> ordre = new ArrayList<>();
        for (int i = 0; i < puzzle.lignes().size(); i++) ordre.add(i);
        Collections.shuffle(ordre);
        while (puzzle.lignes().size() > 1 && ordre.equals(puzzle.solution())) {
            Collections.shuffle(ordre);
        }

        model.addAttribute("student",      s);
        model.addAttribute("puzzle",       puzzle);
        model.addAttribute("ordre",        ordre);
        model.addAttribute("totalPuzzles", gameSvc.getPuzzles().size());
        return "game_puzzle";
    }

    @PostMapping("/game/puzzle")
    public String verifierPuzzle(
            @RequestParam String puzzleId,
            @RequestParam String reponse,
            HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/character";

        Student student = getStudent(session);
        List<Integer> rep = new ArrayList<>();
        try {
            for (String n : reponse.split(","))
                rep.add(Integer.parseInt(n.trim()));
        } catch (NumberFormatException ignored) {}

        boolean correct = gameSvc.validerPuzzle(puzzleId, rep);
        List<String> nouveauxBadges = new ArrayList<>();

        if (correct) {
            student.addScore(GameService.PTS_PUZZLE);
            student.setPuzzlesResolus(student.getPuzzlesResolus() + 1);
            nouveauxBadges = student.verifierBadges();
        } else {
            student.perdreVie();
        }
        repo.save(student);
        session.setAttribute("student", student);

        model.addAttribute("student",        student);
        model.addAttribute("correct",        correct);
        model.addAttribute("puzzle",         gameSvc.getPuzzleById(puzzleId));
        model.addAttribute("nouveauxBadges", nouveauxBadges);
        return "game_puzzle_result";
    }

    // ══ RUNNER ═══════════════════════════════════════════════════
    @GetMapping("/game/runner")
    public String pageRunner(
            @RequestParam(defaultValue = "easy") String diff,
            HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/character";
        Student s = getStudent(session);
        if (s.getVies() <= 0) {
            s.resetVies(); repo.save(s); session.setAttribute("student", s);
        }
        model.addAttribute("student",    s);
        model.addAttribute("difficulte", diff);
        return "game_runner";
    }

    // ══ MEMORY ═══════════════════════════════════════════════════
    @GetMapping("/game/memory")
    public String pageMemory(
            @RequestParam(defaultValue = "easy") String diff,
            HttpSession session, Model model) {
        if (!isLoggedIn(session)) return "redirect:/character";
        Student s = getStudent(session);
        int n = "hard".equals(diff) ? 12 : "medium".equals(diff) ? 8 : 4;
        List<GameService.PaireMemory> paires = gameSvc.getPaires(n);
        model.addAttribute("student",    s);
        model.addAttribute("paires",     paires);
        model.addAttribute("difficulte", diff);
        model.addAttribute("nbPaires",   n);
        return "game_memory";
    }

    // ══ CLASSEMENT ═══════════════════════════════════════════════
    @GetMapping("/leaderboard")
    public String pageClassement(HttpSession session, Model model) {
        try {
            // Récupère tous les joueurs triés par score, limite à 10
            List<Student> classement = repo.findAllOrderByScoreDesc()
                .stream()
                .limit(10)
                .collect(Collectors.toList());

            model.addAttribute("student",    getStudent(session));
            model.addAttribute("classement", classement);
        } catch (Exception e) {
            // En cas d'erreur, envoie une liste vide
            model.addAttribute("student",    getStudent(session));
            model.addAttribute("classement", new ArrayList<>());
            model.addAttribute("erreurDb",   "Erreur base de donnees : " + e.getMessage());
        }
        return "leaderboard";
    }

    // ══ LOGOUT ═══════════════════════════════════════════════════
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/character";
    }
}
