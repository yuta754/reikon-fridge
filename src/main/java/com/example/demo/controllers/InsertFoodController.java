package com.example.demo.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.forms.InsertFoodForm;
import com.example.demo.services.InsertFoodService;
import com.example.demo.services.SearchFoodService;

import jakarta.servlet.http.HttpSession;

@Controller
public class InsertFoodController {

	@Autowired
	InsertFoodService insert;
	
	@Autowired
	SearchFoodService search;
	
	@PostMapping({"/insertFood"})
	public String insertFood(HttpSession session,@ModelAttribute InsertFoodForm form, Model model, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
		
		//食材量(整数)に0が入力されていたらnullにする
		if(form.getAmount() != null && form.getAmount() == 0) {
			form.setAmount(null);
		}
		
		//入力情報に誤りがないかチェック
		String error = form.checkError();
		if(error != null) {
			redirectAttributes.addFlashAttribute("insertFoodForm", form);
		    String encodedMessage = URLEncoder.encode(error, "UTF-8");
		    return "redirect:/insertFoodSelectFood?message=" + encodedMessage;
		}
		
		//セッションからユーザIDを取得&登録情報としてセット
		Integer userId = (Integer) session.getAttribute("loggedInUserId");
		form.setUserId(userId);
		
		//食材情報登録処理
		insert.insertFood(form.converTo(form));
		
		//メッセージを設定
		String encodedMessage = URLEncoder.encode("登録が完了しました", "UTF-8");
		
		return "redirect:/selectFood?message=" + encodedMessage;
	}
}
