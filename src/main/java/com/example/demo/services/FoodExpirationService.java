package com.example.demo.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Food;
import com.example.demo.repositories.FoodRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FoodExpirationService {
	
	private final FoodRepository foodRepository;

    // 毎日午前0時に実行されます
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void checkExpiration() {
        List<Food> expiredFoods = foodRepository.findByDateBeforeAndFlagNot(LocalDate.now(), 3);
        for (Food food : expiredFoods) {
            food.setFlag(3); // 廃棄
            foodRepository.save(food);
        }
    }
}