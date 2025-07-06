package com.example.guessgame.service;

import com.example.guessgame.entity.GameResult;
import com.example.guessgame.entity.User;
import com.example.guessgame.repository.GameResultRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class GameService {
    private final GameResultRepository gameResultRepository;
    private final Random random = new Random();
    private int targetNumber;
    private int attempts;
    private int hintsUsed;
    private long startTime;

    public GameService(GameResultRepository gameResultRepository) {
        this.gameResultRepository = gameResultRepository;
    }

    public void startNewGame() {
        targetNumber = random.nextInt(100) + 1; // 1-100
        attempts = 0;
        hintsUsed = 0;
        startTime = System.currentTimeMillis();
    }

    public String guess(int number) {
        attempts++;
        if (number == targetNumber) {
            return "Верно!";
        } else if (number < targetNumber) {
            return "Число Больше.";
        } else {
            return "Число Меньше.";
        }
    }

    public String getHint() {
        hintsUsed++;
        if (targetNumber % 2 == 0) {
            return "Число Четное.";
        } else {
            return "Число Нечетное.";
        }
    }

    public void saveResult(User user) {
        int timeTaken = (int) ((System.currentTimeMillis() - startTime) / 1000);
        GameResult result = new GameResult();
        result.setUser(user);
        result.setTimeTaken(timeTaken);
        result.setHintsUsed(hintsUsed);
        result.setAttempts(attempts);
        result.setGameDate(LocalDateTime.now());
        gameResultRepository.save(result);
    }

    public int getAttempts() {
        return attempts;
    }

    public int getHintsUsed() {
        return hintsUsed;
    }
}