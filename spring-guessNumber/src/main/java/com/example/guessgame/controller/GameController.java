package com.example.guessgame.controller;

import com.example.guessgame.service.AuthService;
import com.example.guessgame.service.GameService;
import com.example.guessgame.service.LeaderboardService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GameController {
    private final GameService gameService;
    private final AuthService authService;
    private final LeaderboardService leaderboardService;

    public GameController(GameService gameService, AuthService authService, LeaderboardService leaderboardService) {
        this.gameService = gameService;
        this.authService = authService;
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/game")
    public String gamePage(Model model) {
        gameService.startNewGame();
        model.addAttribute("attempts", gameService.getAttempts());
        model.addAttribute("hintsUsed", gameService.getHintsUsed());
        return "game";
    }

    @PostMapping("/guess")
    public String guess(@RequestParam int number, Model model) {
        String result = gameService.guess(number);
        model.addAttribute("result", result);
        model.addAttribute("attempts", gameService.getAttempts());
        model.addAttribute("hintsUsed", gameService.getHintsUsed());
        if (result.equals("Верно!")) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            gameService.saveResult(authService.authenticate(username));
            // Сохраняем лидерборд в файл после завершения игры
            try {
                leaderboardService.saveLeaderboardToFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "redirect:/leaderboard";
        }
        return "game";
    }

    @PostMapping("/hint")
    public String hint(Model model) {
        String hint = gameService.getHint();
        model.addAttribute("result", hint);
        model.addAttribute("attempts", gameService.getAttempts());
        model.addAttribute("hintsUsed", gameService.getHintsUsed());
        return "game";
    }
}