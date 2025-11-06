package com.example.demo.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.forms.InsertFoodForm;
import com.example.demo.services.SearchFoodGenreService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class InsertFoodSelectFoodController {

	@Autowired
	SearchFoodGenreService service;
	
	@ModelAttribute
	public InsertFoodForm insertFoodForm() {
		return new InsertFoodForm();
	}
	
	@GetMapping({"/insertFoodSelectFood"})
	public String insertFoodInput(@RequestParam(required = false) String message, @ModelAttribute InsertFoodForm form, Model model) {
		
		//食材ジャンルテーブルの全レコード取得
		model.addAttribute("food_genre", service.findAll());
		
		//各食材ジャンルに対応する単位を設定
		Map<Integer, String> unitMap = new HashMap<>();
		unitMap.put(1, "g(ml)");
		unitMap.put(2, "個");
		unitMap.put(3, "個(本)");
		unitMap.put(4, "g");
		unitMap.put(5, "ml");
		unitMap.put(6, "個(本)");
		unitMap.put(7, "g(ml)");
		unitMap.put(8, "(個)");
		
		//JSON文字列に変換(JSに渡す用)
		ObjectMapper mapper = new ObjectMapper();
		try {
			String unitMapJson = mapper.writeValueAsString(unitMap);
			model.addAttribute("unitMapJson", unitMapJson);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		//入力情報にエラーがあった場合のメッセージを設定
		if(message != null) {
			model.addAttribute("message", message);
		}

		return "insertFood";
	}
}
