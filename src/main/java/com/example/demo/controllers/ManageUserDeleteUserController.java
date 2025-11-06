package com.example.demo.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entities.User;
import com.example.demo.services.ManageUserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ManageUserDeleteUserController {

	@Autowired
	ManageUserService userService;

	//	//削除画面→ユーザ管理メニューに戻る
	//	@GetMapping("/manageUser")
	//	public String showManageUser(Model model, Principal principal) {
	//	    if (principal != null) model.addAttribute("displayName", principal.getName());
	//	    return "manageUser";
	//	}

	// 削除確認（GET）
	@GetMapping("/manageUserDeleteUser")
	public String manageUserDeleteUser(HttpSession session, Model model) {

		// PRG戻り時（削除完了）はそのまま表示
		Map<String, Object> map = model.asMap();
		if (Boolean.TRUE.equals(map.get("deleted"))) {
			// 削除済みなのでセッションを破棄し、ログアウト状態にする
			session.invalidate();
			return "deleteUser";
		}

		// ログインチェック
		Object sid = session.getAttribute("loggedInUserId");
		if (sid == null)
			return "redirect:/login";

		// Integer/Long対応
		Integer userId = (sid instanceof Integer) ? (Integer) sid : ((Long) sid).intValue();

		// ユーザ取得
		var opt = userService.findById(userId);
		if (opt.isEmpty()) {
			model.addAttribute("notFoundMessage", "ユーザが見つかりません（ID: " + userId + "）");
			return "deleteUser";
		}

		User user = opt.get();
		model.addAttribute("userId", user.getUserId());
		model.addAttribute("userName", user.getUserName());
		model.addAttribute("userMail", user.getUserMail());

		return "deleteUser";
	}

	// 削除実行（POST）: PRG + Flash
	@PostMapping("/deleteUser")
	public String deleteUser(HttpSession session, RedirectAttributes ra) {

		// ログインチェック（キー名を統一）
		Object sid = session.getAttribute("loggedInUserId");
		if (sid == null)
			return "redirect:/login";

		Integer userId = (sid instanceof Integer) ? (Integer) sid : ((Long) sid).intValue();

		// モーダルに出す名前を削除前に退避
		userService.findById(userId).ifPresent(u -> ra.addFlashAttribute("deletedUserName", u.getUserName()));

		userService.deleteUser(userId);

		ra.addFlashAttribute("deleted", true);

		//※ここではsession.invalidate()しない（Flashが消えるため）
		return "redirect:/manageUserDeleteUser";
	}
}