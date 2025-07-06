package com.example.guessgame.service;

import com.example.guessgame.dto.LeaderboardEntryDTO;
import com.example.guessgame.util.EncryptionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileEncryptionService {
    private static final Logger logger = LoggerFactory.getLogger(FileEncryptionService.class);
    private final EncryptionUtil encryptionUtil;
    private final ObjectMapper objectMapper;
    private static final String FILE_PATH = "src/main/resources/leaderboard.txt";

    public FileEncryptionService(EncryptionUtil encryptionUtil, ObjectMapper objectMapper) {
        this.encryptionUtil = encryptionUtil;
        this.objectMapper = objectMapper;
    }

    public void saveLeaderboard(List<LeaderboardEntryDTO> entries) throws Exception {
        try {
            String json = objectMapper.writeValueAsString(entries);
            String encrypted = encryptionUtil.encrypt(json);
            File file = new File(FILE_PATH);
            logger.info("Сохранение лидерборда в файл: {}", file.getAbsolutePath());
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(encrypted);
            }
            logger.info("Лидерборд успешно сохранён в {}", file.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Ошибка при сохранении лидерборда: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<LeaderboardEntryDTO> loadLeaderboard() throws Exception {
        File file = new File(FILE_PATH);
        logger.info("Чтение лидерборда из файла: {}", file.getAbsolutePath());
        if (!file.exists()) {
            logger.warn("Файл {} не существует, возвращается пустой список", file.getAbsolutePath());
            return new ArrayList<>();
        }
        String encrypted = Files.readString(file.toPath());
        String json = encryptionUtil.decrypt(encrypted);
        return objectMapper.readValue(json, objectMapper.getTypeFactory()
                .constructCollectionType(List.class, LeaderboardEntryDTO.class));
    }
}