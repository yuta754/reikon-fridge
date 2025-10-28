package com.example.demo.controllers;


import java.util.Arrays;
import java.util.List;

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
	
	@PostMapping("/shoppingList")
	public String handleShoppingList(Model model) {
		
		Integer userId =(Integer) session.getAttribute("loggedInUserId");
		
		List<Food> shoppingList = repository.findByUserIdAndFlagIn(userId, Arrays.asList(2, 3));
		model.addAttribute("shoppingList", shoppingList);
	    return "shoppingList";
	}
	
	
	@GetMapping("/shoppingList")
	public String showList(Model model) {
		
		Integer userId = (Integer) session.getAttribute("loggedInUserId");
		
		List<Food> shoppingList = repository.findByUserIdAndFlagIn(userId, Arrays.asList(2, 3));
		model.addAttribute("shoppingList", shoppingList);
		return "shoppingList";
	}

}