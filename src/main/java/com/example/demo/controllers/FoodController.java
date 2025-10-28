package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.services.FoodService;

import jakarta.servlet.http.HttpSession;

@Controller
public class FoodController {
    private final FoodService foodService;
     
    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }
    
    
    @GetMapping("/waste-rate") 
    public String showWasteRate(@RequestParam(name = "period", defaultValue = "all") String period, HttpSession session, Model model) {
        
        //  セッションからuserIdを取得し、未ログインならリダイレクト 
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) {
            return "redirect:/login"; 
            
        }
        // サービスにuserIdを渡す
        // --- 1. データ取得 ---
        var wasteRates = foodService.getWasteRates(period, userId); 
        var wasteDetails = foodService.getWasteDetails(period, userId);
        var wasteRankings = foodService.getWasteRankings(userId); 
        // --- 2. トレンド分析のためのデータ取得と計算 ---
        double currentWasteAmount = foodService.getTotalWasteAmount(period, userId);
        double previousWasteAmount = foodService.getTotalWasteAmount(getPreviousPeriod(period), userId);
        String trendStatus = calculateWasteTrend(currentWasteAmount, previousWasteAmount);
        
        // --- 3. モデルへの追加 ---
        model.addAttribute("wasteRates", wasteRates);
        model.addAttribute("wasteDetails", wasteDetails); 
        model.addAttribute("wasteRankings", wasteRankings); 
        model.addAttribute("currentPeriod", period);
        model.addAttribute("trendStatus", trendStatus);
        
        return "foodWasteRate"; 
    }
    
    
    private String calculateWasteTrend(double current, double previous) {
        if (previous == 0.0) {
            return (current > 0.0) ? "up" : "stable";
        }
        double change = (current - previous) / previous;
        if (change > 0.05) return "up";
        if (change < -0.05) return "down";
        return "stable";
    }
    
    private String getPreviousPeriod(String currentPeriod) {
        if ("week".equals(currentPeriod)) return "prev_week";
        if ("month".equals(currentPeriod)) return "prev_month";
        if ("year".equals(currentPeriod)) return "prev_year";
        return "all"; 
        
        
    }
}