package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entities.User;
import com.example.demo.services.ManageUserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UpdateUserController {

	@Autowired
	ManageUserService userService;

	@PostMapping("/updateUser")
	public String updateUser(
			@RequestParam("userName") String userName,
			@RequestParam("userMail") String userMail,
			@RequestParam("userPass") String userPass,
			HttpSession session, Model model, RedirectAttributes ra) {

		// セッションからログイン中ユーザID取得
		Object sid = session.getAttribute("loggedInUserId");
		if (sid == null) {
			return "redirect:/login";
		}
		Integer userId = (sid instanceof Integer) ? (Integer) sid : ((Long) sid).intValue();

		// DBから本人取得
		User user = userService.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("ユーザが見つかりません。"));

		// 入力値で上書き（エンティティのプロパティ名に合わせる）
		user.setUserName(userName);
		user.setUserMail(userMail);
		user.setUserPassword(userPass);

		// 更新実行
		userService.updateUser(user);

		//右上表示用セッション
		session.setAttribute("loggedInUserName", userName);

		// ★ モーダル用フラグをFlashで渡す
		ra.addFlashAttribute("updated", true);
		ra.addFlashAttribute("updatedUserName", userName);

		// ★ updateUser.html を描画するハンドラへ戻す
		return "redirect:/manageUserUpdate";
	}
}