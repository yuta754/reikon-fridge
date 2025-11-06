package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entities.User;
import com.example.demo.services.ManageUserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ManageUserUpdateController {

	@Autowired
	ManageUserService userService;

	@GetMapping("/manageUserUpdate")
	public String manageUserUpdate(HttpSession session, Model model) {

		//セッションからログイン中のユーザID取得
		Object sid = session.getAttribute("loggedInUserId");
		// null チェックを先に
		if (sid == null) {
			return "redirect:/login"; // 未ログインならログイン画面へ
		}

		Integer userId = (sid instanceof Integer) ? (Integer) sid : ((Long) sid).intValue();

		// DBからユーザ情報取得
		User user = userService.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("ユーザが見つかりません。"));

		//modelに詰める
		model.addAttribute("userId", user.getUserId());
		model.addAttribute("userName", user.getUserName());
		model.addAttribute("userMail", user.getUserMail());

//		//右上表示用（テンプレ側で userName or loggedInUserName どちらかに合わせる）
//		model.addAttribute("loggedInUserName", session.getAttribute("loggedInUserName"));

		return "updateUser";
	}
}