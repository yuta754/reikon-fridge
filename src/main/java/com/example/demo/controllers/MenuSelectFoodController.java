package com.example.demo.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.entities.Food;
import com.example.demo.repositories.FoodRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class MenuSelectFoodController {

    private final FoodRepository foodRepository;
    private final HttpSession session;

    public MenuSelectFoodController(FoodRepository foodRepository, HttpSession session) {
        this.foodRepository = foodRepository;
        this.session = session;
    }

    @GetMapping("/selectFood")
    public String showFridge(
            @RequestParam(name = "keyword", required = false) String keyword,
            Model model) {

    	Integer userId = (Integer) session.getAttribute("loggedInUserId");
    	
        List<Food> foods;

        if (keyword != null && !keyword.isEmpty()) {
            // キーワード検索（在庫中のみ）
            foods = foodRepository.findByUserIdAndFoodNameContainingAndFlag(userId, keyword, 1);
            model.addAttribute("keyword", keyword);
        } else {
            // 在庫中の全件取得
            foods = foodRepository.findByUserIdAndFlagOrderByDateDesc(userId, 1);
        }

        LocalDate today = LocalDate.now();

        // 賞味期限切れでないものだけ残す
        for (Food food : foods) {
            if (food.getDate() != null && food.getDate().isBefore(today)) {
                food.setFlag(3); // flag=3 → 廃棄
                foodRepository.save(food);
            }
        }

        // 再取得（flagが3に変わったものを除く）
        foods = foods.stream()
                .filter(food -> food.getDate() == null || !food.getDate().isBefore(today))
                .collect(Collectors.toList());
          
        
        // バー色を設定
        foods.forEach(food -> {
            double remainingPercent = 0.0;

            if (food.getBunshi() != null && food.getBunbo() != null && food.getBunbo() != 0) {
                remainingPercent = food.getBunshi() * 100.0 / food.getBunbo();
            } else if (food.getAmount() != null) {
                remainingPercent = 100.0;
            }

            food.setRemainingPercent(remainingPercent);

            if (remainingPercent <= 30) {
                food.setBarColor("#ef4444"); // 赤
            } else if (remainingPercent <= 70) {
                food.setBarColor("#facc15"); // 黄
            } else {
                food.setBarColor("#3b82f6"); // 青
            }
        });

        model.addAttribute("foods", foods);
        return "selectFood";
    }
    
    @PostMapping("/updateFoodFlag")
    @ResponseBody
    public Map<String, Object> updateFoodFlag(@RequestBody Map<String, Object> payload) {
    	
    	Integer id = Integer.parseInt((String) payload.get("id"));
        Integer flag = (Integer) payload.get("flag");

        // idから食材を取得
        Food food = foodRepository.findById(id).orElse(null);

        if (food == null) {
            return Map.of("success", false, "message", "対象の食材が見つかりません");
        }

        // flag更新（3 = 廃棄）
        food.setFlag(flag);
        
        if (flag == 3) {
            food.setWasteDate(LocalDate.now());
        }
        
        foodRepository.save(food);
        

        return Map.of("success", true);
    }
    
    @PostMapping("/deleteFood")
    @ResponseBody
    public Map<String, Object> deleteFood(@RequestBody Map<String, Object> payload) {
    	Integer id = Integer.parseInt(payload.get("id").toString());

        if (!foodRepository.existsById(id)) {
            return Map.of("success", false, "message", "対象の食材が見つかりません");
        }

        foodRepository.deleteById(id);
        return Map.of("success", true);
    }
}