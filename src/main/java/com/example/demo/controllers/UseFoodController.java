package com.example.demo.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entities.Food;
import com.example.demo.forms.UpdateFoodForm;
import com.example.demo.services.SearchFoodService;
import com.example.demo.services.UseFoodService;

@Controller
public class UseFoodController {

	@Autowired
	UseFoodService use;
	
	@Autowired
	SearchFoodService search;
	
	@PostMapping({"/useFood"})
	public String useFood(@RequestParam(name = "radio", required = false) String radio, @RequestParam(name = "seisu", required = false) Integer seisu, @RequestParam(name = "bunshiSeisu", required = false) Integer bunshiSeisu, @RequestParam(name = "bunboSeisu", required = false) Integer bunboSeisu,
							@RequestParam(name = "bunshiBunsu", required = false) Integer bunshiBunsu, @RequestParam(name = "bunboBunsu", required = false) Integer bunboBunsu, @RequestParam("foodId") Integer foodId, Model model, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {

		if(radio == null) {
			redirectAttributes.addAttribute("id", foodId);
		    String encodedMessage = URLEncoder.encode("ボタン選択は必須です", "UTF-8");
		    return "redirect:/selectFoodUseFood?message=" + encodedMessage;
		}
		if(bunboSeisu != null && bunshiSeisu >= bunboSeisu) {
			redirectAttributes.addAttribute("id", foodId);
			String encodedMessage = URLEncoder.encode("分子は分母より小さい数を入力してください", "UTF-8");
			return "redirect:/selectFoodUseFood?message=" + encodedMessage;
		}
		if(bunboBunsu != null && bunshiBunsu >= bunboBunsu) {
			redirectAttributes.addAttribute("id", foodId);
			String encodedMessage = URLEncoder.encode("分子は分母より小さい数を入力してください", "UTF-8");
			return "redirect:/selectFoodUseFood?message=" + encodedMessage;
		}
		
		//食材量(整数)に0が入力されていたらnullにする
		if(seisu != null && seisu == 0) {
			seisu = null;
		}
		
		List<Food> foodInfo = search.findById(foodId);
		UpdateFoodForm updateFoodForm = new UpdateFoodForm();
		updateFoodForm = UpdateFoodForm.convertFrom(foodInfo.get(0));
		
		//使い切ったを選択した場合
		if(radio.equals("useAll")) {
			updateFoodForm = use.useAll(updateFoodForm);
		}
		
		//整数を含んだ消費量を選択した場合
		if(radio.equals("useSeisu")) {
			//整数入力欄がnullの場合
			if(seisu == null) {
			    String encodedMessage = URLEncoder.encode("入力が不正です", "UTF-8");
			    return "forward:/selectFoodUseFood?message=" + encodedMessage;				
			}
			
			//消費量が整数のみの場合
			if(bunboSeisu == null) {
				
				//計算結果を在庫(整数)にセット
				updateFoodForm.setAmount(use.simpleCalculate(updateFoodForm.getAmount(), seisu));
				
				//元の在庫が整数のみで在庫(整数)が0以下になった場合
				if(updateFoodForm.getAmount() <= 0 && updateFoodForm.getBunbo() == null) {
					updateFoodForm = use.useAll(updateFoodForm);
				}
				//元の在庫が整数+分数で在庫(整数が)が0以下になった場合
				else if(updateFoodForm.getAmount() <= 0 && updateFoodForm.getBunbo() != null) {
					updateFoodForm.setAmount(null);
				}
			}
			
			//在庫は整数のみで消費量が整数+分数の場合
			else if(bunboSeisu != null && updateFoodForm.getBunbo() == null) {
				Integer currentBunshi = updateFoodForm.getAmount() * bunboSeisu;
				Integer consumeBunshi = seisu * bunboSeisu + bunshiSeisu;
				Integer result = currentBunshi - consumeBunshi;
				
				if(result <= 0) {
					updateFoodForm = use.useAll(updateFoodForm);
				}
				
				//約分してFormに整数、分子、分母をセット
				else{
					updateFoodForm = use.yakubun(updateFoodForm, result, bunboSeisu);
				}
				
			}
			
			//在庫と消費量が両方とも整数+分数の場合
			else if(bunboSeisu != null && updateFoodForm.getBunbo() != null) {
				//最大公約数を求める
				Integer gcd = use.gcd(updateFoodForm.getBunbo(), bunboSeisu);
				//最小公倍数を求める
				Integer newBunbo = use.lcm(updateFoodForm.getBunbo(), bunboSeisu, gcd);
				Integer currentBunshi = updateFoodForm.getAmount() * newBunbo + updateFoodForm.getBunshi() * gcd;
				Integer consumeBunshi = seisu * newBunbo + bunshiSeisu * gcd;
				Integer result = currentBunshi - consumeBunshi;
				
				if(result <= 0) {
					updateFoodForm = use.useAll(updateFoodForm);
				}
				
				//約分してFormに整数、分子、分母をセット
				else{
					updateFoodForm = use.yakubun(updateFoodForm, result, bunboSeisu);
				}
			}
		}
		
		//分数のみの消費量を選択した場合
		if(radio.equals("useBunsu")) {
			//在庫量が整数のみの場合
			if(updateFoodForm.getBunbo() == null) {
				Integer currentBunshi = updateFoodForm.getAmount() * bunboBunsu;
				Integer result = currentBunshi - bunshiBunsu;
				
				if(result <= 0) {
					updateFoodForm = use.useAll(updateFoodForm);
				}
				
				//約分してFormに整数、分子、分母をセット
				else{
					updateFoodForm = use.yakubun(updateFoodForm, result, bunboBunsu);
				}
			}
			
			//在庫量が整数+分数の場合
			else if(updateFoodForm.getAmount() != null && updateFoodForm != null) {
				//最大公約数を求める
				Integer gcd = use.gcd(updateFoodForm.getBunbo(), bunboBunsu);
				//最小公倍数を求める
				Integer newBunbo = use.lcm(updateFoodForm.getBunbo(), bunboBunsu, gcd);
				
				Integer currentBunshi = updateFoodForm.getAmount() * newBunbo + updateFoodForm.getBunshi() * gcd;
				Integer consumeBunshi = bunshiBunsu * gcd;
				Integer result = currentBunshi - consumeBunshi;
				
				if(result <= 0) {
					updateFoodForm = use.useAll(updateFoodForm);
				}
				
				//約分してFormに整数、分子、分母をセット
				else{
					updateFoodForm = use.yakubun(updateFoodForm, result, bunboBunsu);
				}
			}
			
			//在庫が分数だけの場合
			else if(updateFoodForm.getAmount() == null && updateFoodForm.getBunbo() != null) {
				//最大公約数を求める
				Integer gcd = use.gcd(updateFoodForm.getBunbo(), bunboBunsu);
				//最小公倍数を求める
				Integer newBunbo = use.lcm(updateFoodForm.getBunbo(), bunboBunsu, gcd);
				Integer currentBunshi = newBunbo / updateFoodForm.getBunbo() * updateFoodForm.getBunshi();
				Integer consumeBunshi = newBunbo / bunboBunsu * bunshiBunsu;
				Integer result = currentBunshi - consumeBunshi;
				
				if(result <= 0) {
					updateFoodForm = use.useAll(updateFoodForm);
				}
				
				//約分してFormに整数、分子、分母をセット
				else{
					updateFoodForm = use.yakubun(updateFoodForm, result, newBunbo);
				}
			}
		}
		
		use.useFood(updateFoodForm.convertTo(updateFoodForm));
		
		return "redirect:/selectFood";
		
	}
}
