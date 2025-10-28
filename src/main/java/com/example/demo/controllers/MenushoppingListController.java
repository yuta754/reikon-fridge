package com.example.demo.controllers;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.entities.Food;
import com.example.demo.repositories.FoodRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class MenushoppingListController {

    @Autowired
    FoodRepository repository;
    @Autowired
    HttpSession session;

    // 賞味期限が近い順に並べる共通処理
    private List<Food> getSortedShoppingList(Integer userId) {
        return repository.findByUserIdAndFlagIn(userId, Arrays.asList(2, 3))
                .stream()
                .sorted(Comparator.comparing(
                        Food::getDate,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .collect(Collectors.toList());
    }

    @PostMapping("/shoppingList")
    public String handleShoppingList(Model model) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        List<Food> shoppingList = getSortedShoppingList(userId);
        model.addAttribute("shoppingList", shoppingList);
        return "shoppingList";
    }

    @GetMapping("/shoppingList")
    public String showList(Model model) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        List<Food> shoppingList = getSortedShoppingList(userId);
        model.addAttribute("shoppingList", shoppingList);
        return "shoppingList";
    }
}
