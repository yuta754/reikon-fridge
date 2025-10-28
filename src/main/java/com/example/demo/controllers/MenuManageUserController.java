package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class MenuManageUserController {
	@PostMapping("/menuManageUser")
	public String doManageUser(HttpSession session, Model model) {

		if (session.getAttribute("loggedInUserId") == null) {
			return "redirect:/login"; // 未ログインならログイン画面へ
		}

		model.addAttribute("loggedInUserName", session.getAttribute("loggedInUserName")); 
        return "manageUser";
	}
}