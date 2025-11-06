package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class LogoutController {

    /**
     * ログアウト処理
     */
    @PostMapping("/logout")
    public String performLogout(HttpSession session) {
        // セッションを無効化し、保存されているすべての属性をクリア
        session.invalidate(); 
        
        // ログイン画面にリダイレクト
        return "redirect:/login";
    }
}