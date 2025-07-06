package com.example.guessgame.controller;

import com.example.guessgame.service.LeaderboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/leaderboard")
    public String leaderboard(@RequestParam(defaultValue = "time") String sortBy, Model model) {
        model.addAttribute("leaderboard", leaderboardService.getLeaderboard(sortBy));
        model.addAttribute("sortBy", sortBy);
        return "leaderboard";
    }
}