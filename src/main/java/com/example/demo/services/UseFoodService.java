package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Food;
import com.example.demo.forms.UpdateFoodForm;
import com.example.demo.repositories.UseFoodRepository;

@Service
public class UseFoodService {

	@Autowired
	UseFoodRepository dao;
	
	public void useFood(Food entity) {
		dao.save(entity);
	}
	
	//在庫量も消費量も整数の場合の計算メソッド
	public Integer simpleCalculate(Integer amount, Integer seisu) {
		Integer result = amount - seisu;
		return result;
	}
	
	//在庫を全て消費した時に呼び出されるメソッド
	public UpdateFoodForm useAll(UpdateFoodForm form) {
		form.setAmount(null);
		form.setBunshi(null);
		form.setBunbo(null);
		form.setFlag(2);
		return form;
	}
	
	//最大公約数を求めるメソッド
	public Integer gcd(Integer x, Integer y) {
		while(y != 0) {
			Integer temp = y;
			y = x % y;
			x = temp;
		}
		Integer gcd = Math.abs(x);
		return gcd;
	}
	//最小公倍数を求めるメソッド
	public Integer lcm(Integer x, Integer y, Integer gcd) {
		if(x == 0 || y == 0) {
			return 0;
		}
		
		return Math.abs(x * y) / gcd;
	}
	
	//約分するメソッド
	public UpdateFoodForm yakubun(UpdateFoodForm form, Integer bunshi, Integer bunbo) {
		// 『分子 < 分母』 の場合は整数(null)を返す
		if(bunshi < bunbo) {
			form.setAmount(null);
		}
		
		// 『分子 > 分母』 の場合は『分子 < 分母』になるまで約分
		Integer seisu = 0;
		while(bunshi >= bunbo) {
			bunshi -= bunbo;
			seisu++;
			form.setAmount(seisu);
			
			// 『分子 = 0(分数が残らない)』の場合
			if(bunshi == 0) {
				form.setBunshi(null);
				form.setBunbo(null);
				return form;
			}
		}
		
		form.setBunshi(bunshi);
		form.setBunbo(bunbo);
		
		return form;
	}
}
