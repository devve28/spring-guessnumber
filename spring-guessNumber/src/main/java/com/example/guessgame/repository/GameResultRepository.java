package com.example.guessgame.repository;

import com.example.guessgame.entity.GameResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameResultRepository extends JpaRepository<GameResult, Long> {
    List<GameResult> findAllByOrderByTimeTakenAsc();
    List<GameResult> findAllByOrderByAttemptsAsc();
}