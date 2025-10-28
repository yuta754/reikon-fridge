package com.example.demo.forms;

import java.time.LocalDate;

import com.example.demo.entities.Food;

import lombok.Data;

@Data
public class InsertFoodForm {

	//食材名
	private String foodName;
	
	//食材ジャンルID
	private Integer genreId;
	
	//ユーザID
	private Integer userId;
	
	//食材量(整数)
	private Integer amount;
	
	//食材量(分子)
	private Integer bunshi;
	
	//食材量(分母)
	private Integer bunbo;
	
	//消費期限/賞味期限
	private LocalDate date;

	//フラグ
	private Integer flag = 1;
	
	//廃棄日
	private LocalDate wasteDate = null;
	
	//入力値(Form)をEntityに変換
	public Food converTo(InsertFoodForm form) {
		return new Food(
				(Integer) null,
				form.getFoodName(),
				form.getGenreId(),
				form.getUserId(),
				form.getAmount(),
				form.getBunshi(),
				form.getBunbo(),
				form.getDate(),
				form.getFlag(),
				form.getWasteDate()
				);
	}
	
	//エラーチェック
	public String checkError() {
		String foodNameNullError = "食材名は必須項目です";
		String foodNameLengthError = "食材名が長すぎます";
		String genreIdError = "食材ジャンルは必須項目です";
		String amountError = "食材量は0より大きい値を入力してください";
		String bunsuError = "分子は分母より小さい数を入力してください";
		String bunboError = "分母は2以上の値を入力してください";
		String bunshiError = "分子は1以上の値を入力してください";
		String dateError = "昨日以前の日付には設定できません";
		
		if(foodName == null || foodName.trim().isEmpty()) {
			return foodNameNullError;
		}else if(foodName.length() > 100) {
			return foodNameLengthError;
		}
		
		if(genreId == null) {
			return genreIdError;
		}
		
		if(amount != null && amount <= 0) {
			return amountError;
		}
		
		if(bunshi != null) {
			if(bunshi <= 0) {
				return bunshiError;
			}
		}
		
		if(bunbo != null) {
			if(bunshi >= bunbo) {
				return bunsuError;
			}else if(bunbo <= 1) {
				return bunboError;
			}
		}
		
		if(date != null && date.isBefore(LocalDate.now())) {
			return dateError;
		}
		
		return null;
	}
}
