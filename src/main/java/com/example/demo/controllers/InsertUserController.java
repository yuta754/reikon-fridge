package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entities.User;
import com.example.demo.services.ManageUserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class InsertUserController {

	@Autowired
	ManageUserService service;

	@PostMapping("/insertUser")
	public String insertUser(
			@RequestParam("userName") String userName,
			@RequestParam("userMail") String userMail,
			@RequestParam("userPass") String userPass,
			@RequestParam("confirmUserPass") String confirmUserPass,
			HttpSession session,
			Model model) {

		System.out.println(userName);
		System.out.println(userMail);
		System.out.println(userPass);
		System.out.println(confirmUserPass);
		
		Integer userId = service.findByMail(userMail);
		//パスワードとパスワード(確認用)の入力が一致しているか確認
		if(!(userPass.equals(confirmUserPass)) || userId != null) {
			if(!userPass.equals(confirmUserPass)) {
				model.addAttribute("errorMessage", "パスワードが一致しませんでした");
			}
			if(userId != null) {
				model.addAttribute("errorMessage", "既に登録されているメールアドレスです");
			}
			model.addAttribute("userName", userName);
			model.addAttribute("userMail", userMail);
			model.addAttribute("userPass", userPass);
			return "insertUser";
		}
		
		User user = new User();
		user.setUserName(userName);
		user.setUserMail(userMail);
		user.setUserPassword(userPass);

		service.insertUser(user);
		
		//メールアドレスを検索条件に指定してユーザIDを取得
		Integer loggedInUserId = service.findByMail(userMail);
		
		System.out.println(userId);

		//セッション領域にユーザ情報を格納
		session.setAttribute("loggedInUserId", loggedInUserId);
		session.setAttribute("userName", userName);
		session.setAttribute("userMail", userMail);

		//insertUser.html に戻ってモーダル表示
		model.addAttribute("inserted", true);
		
		//return "insertUser";
		return "insertUser";

	}

}