package com.learncode.controller;

import com.learncode.model.Student;
import com.learncode.repository.StudentRepository;
import com.learncode.service.GameService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired private StudentRepository repo;
    @Autowired private GameService       gameSvc;

    // ── GET /api/question ────────────────────────────────────────
    @GetMapping("/question")
    public Map<String, Object> getQuestion(
            @RequestParam(defaultValue = "easy") String diff) {
        GameService.Question q = gameSvc.getQuestionAleatoire(diff);
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("id",       q.id());
        res.put("question", q.question());
        res.put("options",  q.options());
        return res;
    }

    // ── GET /api/classement ──────────────────────────────────────
    @GetMapping("/classement")
    public List<Map<String, Object>> getClassement() {
        return repo.findAllOrderByScoreDesc()
            .stream().limit(10)
            .map(s -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("nom",        s.getNom());
                m.put("personnage", s.getPersonnage());
                m.put("score",      s.getScore());
                m.put("niveau",     s.getNiveau());
                return m;
            }).collect(Collectors.toList());
    }

    // ── POST /api/runner ─────────────────────────────────────────
    @PostMapping("/runner")
    public Map<String, Object> runnerAction(
            @RequestParam String action,
            @RequestParam(required = false) String questionId,
            @RequestParam(required = false, defaultValue = "-1") int reponse,
            HttpSession session) {

        Student s = (Student) session.getAttribute("student");
        Map<String, Object> res = new HashMap<>();
        if (s == null) { res.put("erreur", "Non connecte"); return res; }

        // Refresh depuis DB
        s = repo.findByNomIgnoreCase(s.getNom()).orElse(s);
        List<String> badges = new ArrayList<>();
        boolean correct = false;

        switch (action) {
            case "etoile" -> s.addScore(GameService.PTS_ETOILE);
            case "quiz"   -> {
                if (questionId != null) {
                    correct = gameSvc.validerReponse(questionId, reponse);
                    res.put("correct", correct);
                    if (correct) {
                        s.addScore(GameService.PTS_QUIZ);
                        s.setQuizReussis(s.getQuizReussis() + 1);
                    } else {
                        s.perdreVie();
                    }
                    badges = s.verifierBadges();
                }
            }
            case "vie" -> s.perdreVie();
        }

        boolean gameOver = s.getVies() <= 0;
        if (gameOver) s.resetVies();

        repo.save(s);
        session.setAttribute("student", s);

        res.put("score",    s.getScore());
        res.put("niveau",   s.getNiveau());
        res.put("vies",     s.getVies());
        res.put("badges",   badges);
        res.put("gameOver", gameOver);
        return res;
    }

    // ── POST /api/memory ─────────────────────────────────────────
    @PostMapping("/memory")
    public Map<String, Object> memoryAction(
            @RequestParam String action,
            @RequestParam(defaultValue = "0") int temps,
            HttpSession session) {

        Student s = (Student) session.getAttribute("student");
        Map<String, Object> res = new HashMap<>();
        if (s == null) { res.put("erreur", "Non connecte"); return res; }

        s = repo.findByNomIgnoreCase(s.getNom()).orElse(s);
        List<String> badges = new ArrayList<>();

        if ("paire".equals(action)) {
            s.addScore(GameService.PTS_MEMORY);
            badges = s.verifierBadges();
        } else if ("fin".equals(action)) {
            int bonus = Math.max(0, 200 - temps * 2);
            s.addScore(bonus);
            s.setPartiesMemory(s.getPartiesMemory() + 1);
            badges = s.verifierBadges();
            res.put("bonus", bonus);
        }

        repo.save(s);
        session.setAttribute("student", s);

        res.put("score",  s.getScore());
        res.put("niveau", s.getNiveau());
        res.put("vies",   s.getVies());
        res.put("badges", badges);
        return res;
    }

    // ── GET /api/session ─────────────────────────────────────────
    @GetMapping("/session")
    public ResponseEntity<?> getSession(HttpSession session) {
        Student s = (Student) session.getAttribute("student");
        if (s == null)
            return ResponseEntity.status(401).body(Map.of("erreur", "Non connecte"));
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("nom",        s.getNom());
        res.put("personnage", s.getPersonnage());
        res.put("score",      s.getScore());
        res.put("niveau",     s.getNiveau());
        res.put("vies",       s.getVies());
        res.put("badges",     s.getBadgesList());
        return ResponseEntity.ok(res);
    }
}
