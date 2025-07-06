package com.example.guessgame.service;

import com.example.guessgame.dto.LeaderboardEntryDTO;
import com.example.guessgame.entity.GameResult;
import com.example.guessgame.repository.GameResultRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {
    private final GameResultRepository gameResultRepository;
    private final FileEncryptionService fileEncryptionService;

    public LeaderboardService(GameResultRepository gameResultRepository, FileEncryptionService fileEncryptionService) {
        this.gameResultRepository = gameResultRepository;
        this.fileEncryptionService = fileEncryptionService;
    }

    public List<LeaderboardEntryDTO> getLeaderboard(String sortBy) {
        // Сначала пытаемся загрузить лидерборд из файла
        try {
            List<LeaderboardEntryDTO> entries = fileEncryptionService.loadLeaderboard();
            if (!entries.isEmpty()) {
                return sortLeaderboard(entries, sortBy);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Если файл пуст или произошла ошибка, загружаем из базы данных
        List<GameResult> results = sortBy.equals("attempts") ?
                gameResultRepository.findAllByOrderByAttemptsAsc() :
                gameResultRepository.findAllByOrderByTimeTakenAsc();

        return results.stream().map(result -> {
            LeaderboardEntryDTO dto = new LeaderboardEntryDTO();
            dto.setUsername(result.getUser().getUsername());
            dto.setTimeTaken(result.getTimeTaken());
            dto.setHintsUsed(result.getHintsUsed());
            dto.setAttempts(result.getAttempts());
            return dto;
        }).collect(Collectors.toList());
    }

    public void saveLeaderboardToFile() throws Exception {
        List<GameResult> results = gameResultRepository.findAllByOrderByTimeTakenAsc();
        List<LeaderboardEntryDTO> entries = results.stream().map(result -> {
            LeaderboardEntryDTO dto = new LeaderboardEntryDTO();
            dto.setUsername(result.getUser().getUsername());
            dto.setTimeTaken(result.getTimeTaken());
            dto.setHintsUsed(result.getHintsUsed());
            dto.setAttempts(result.getAttempts());
            return dto;
        }).collect(Collectors.toList());
        fileEncryptionService.saveLeaderboard(entries);
    }

    private List<LeaderboardEntryDTO> sortLeaderboard(List<LeaderboardEntryDTO> entries, String sortBy) {
        if (sortBy.equals("attempts")) {
            return entries.stream()
                    .sorted((a, b) -> Integer.compare(a.getAttempts(), b.getAttempts()))
                    .collect(Collectors.toList());
        } else {
            return entries.stream()
                    .sorted((a, b) -> Integer.compare(a.getTimeTaken(), b.getTimeTaken()))
                    .collect(Collectors.toList());
        }
    }
}