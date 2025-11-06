package com.example.demo.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.entities.Food;
import com.example.demo.entities.User;
import com.example.demo.repositories.FoodRepository;
import com.example.demo.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginMenuController {

	private final UserService userService;

	public LoginMenuController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * ログイン画面の表示
	 */
	@GetMapping("/login")
	public String showLoginPage() {
		return "login";
	}

	/**
	 * ログイン処理の実行
	 */
	@PostMapping("/login")
	public String performLogin(
			@RequestParam("userMail") String userMail,
			@RequestParam("userPassword") String userPassword,
			HttpSession session,
			Model model) {

		// 1. ユーザー認証
		User user = userService.authenticate(userMail, userPassword);

		if (user != null) {
			// 2. 認証成功: セッションにユーザーIDを保存し、メニュー画面にリダイレクト
			session.setAttribute("loggedInUserId", user.getUserId());
			session.setAttribute("loggedInUserName", user.getUserName());

			// メニュー画面にリダイレクト
			return "redirect:/menu";
		} else {
			// 3. 認証失敗: エラーメッセージを表示し、ログイン画面に戻る
			model.addAttribute("loginError", "メールアドレスまたはパスワードが正しくありません。");
			return "login";
		}
	}

	@GetMapping("/menu")
	public String showMenuPage(HttpSession session, Model model) {
		if (session.getAttribute("loggedInUserId") == null) {
			// セッションにIDがない場合は未ログインとみなし、ログイン画面へリダイレクト
			return "redirect:/login";
		}
		// ログイン済みの場合のみメニュー画面を表示
		model.addAttribute("userName", session.getAttribute("loggedInUserName"));
		return "menu";
	}

	@Autowired
	FoodRepository repository;

	@GetMapping("/menu/events")
	@ResponseBody
	public List<Map<String, Object>> getEvents(HttpSession session) {
		// 1. セッションからユーザーIDを取得（一時的に固定値を使用）
		Integer userId = (Integer) session.getAttribute("loggedInUserId");

		List<Food> allFoods = repository.findAll();

		List<Food> userFoods = repository.findByUserIdAndFlag(userId, 1);

		userFoods.stream()
				.limit(3)
				.forEach(food -> {
					System.out.println("  - " + food.getFoodName() +
							" (ID:" + food.getFoodId() +
							", userID:" + food.getUserId() +
							", flag:" + food.getFlag() +
							", 賞味期限:" + food.getDate() + ")");
				});

		// 6. FullCalendar形式に変換
		List<Map<String, Object>> events = userFoods.stream()
				.filter(food -> food.getDate() != null)
				.map(food -> {
					Map<String, Object> event = new HashMap<>();
					event.put("title", food.getFoodName());
					event.put("start", food.getDate().toString());
					event.put("allDay", true);

					LocalDate now = LocalDate.now();
					if (food.getDate().isBefore(now)) {
						event.put("backgroundColor", "#ff6b6b");
						event.put("borderColor", "#ff6b6b");
					} else if (food.getDate().minusDays(3).isBefore(now)) {
						event.put("backgroundColor", "#ffa500");
						event.put("borderColor", "#ffa500");
					} else {
						event.put("backgroundColor", "#28a745");
						event.put("borderColor", "#28a745");
					}

					System.out.println("イベント作成: " + food.getFoodName() + " → " + food.getDate());
					return event;
				})
				.collect(Collectors.toList());

		System.out.println("返却するイベント数: " + events.size());
		return events;
	}
}