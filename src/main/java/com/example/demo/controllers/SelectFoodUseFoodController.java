package com.example.demo.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entities.Food;
import com.example.demo.forms.UpdateFoodForm;
import com.example.demo.services.SearchFoodService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class SelectFoodUseFoodController {

	@Autowired
	SearchFoodService search;
	
	@ModelAttribute
	public UpdateFoodForm updateFoodForm(){
		return new UpdateFoodForm();
	}
	
	@GetMapping({"/selectFoodUseFood"})
	public String useFood(@RequestParam("id") Integer foodId, Model model, @RequestParam(required = false) String message) {
		
		//各食材ジャンルに対応する単位を設定
	    Map<Integer, String> unitMap = new HashMap<>();
	    unitMap.put(1, "g");
	    unitMap.put(2, "個");
	    unitMap.put(3, "個(本)");
	    unitMap.put(4, "g");
	    unitMap.put(5, "ml");
	    unitMap.put(6, "個(本)");
	    unitMap.put(7, "ml");
	    unitMap.put(8, "個");
	    
	    //JSON文字列に変換（JSに渡す用）
	    ObjectMapper mapper = new ObjectMapper();
	    try {
	    	String unitMapJson = mapper.writeValueAsString(unitMap);
	    	model.addAttribute("unitMapJson", unitMapJson);
	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	    }
	    
		//食材情報の取得
		List<Food> foodInfo = search.findById(foodId);
		model.addAttribute("updateFoodForm", UpdateFoodForm.convertFrom(foodInfo.get(0)));
		
		//入力情報にエラーがあった場合のメッセージを設定
		if(message != null) {
			model.addAttribute("message", message);
		}
		
		return "useFood";
	}
}
