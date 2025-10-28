package com.example.demo.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.forms.UpdateFoodForm;
import com.example.demo.services.SearchFoodService;
import com.example.demo.services.UpdateFoodService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UpdateFoodController {

	@Autowired
	UpdateFoodService update;
	
	@Autowired
	SearchFoodService search;
	
	@PostMapping({"/updateFood"})
	public String updateFood(HttpSession session, UpdateFoodForm form, Model model, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
		
		//食材量(整数)に0が入力されていたらnullにする
		if(form.getAmount() != null && form.getAmount() == 0) {
			form.setAmount(null);
		}
		
		//入力情報に誤りがないかチェック
		String error = form.checkError();
		if(error != null) {
			redirectAttributes.addFlashAttribute("updateFoodForm", form);
		    String encodedMessage = URLEncoder.encode(error, "UTF-8");
		    return "forward:/insertFoodSelectFood?message=" + encodedMessage;
		}

		Integer userId = (Integer) session.getAttribute("loggedInUserId");
		form.setUserId(userId);
		update.updateFood(form.convertTo(form));
		
		model.addAttribute("message", "更新が完了しました");

		return "redirect:/selectFood";
	}
}
