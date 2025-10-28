package com.example.demo.forms;

import java.time.LocalDate;
import java.util.List;

import com.example.demo.entities.Food;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFoodForm {

	//食材ID
	private Integer foodId;
	
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

	//入力された値(Formクラス)をEntityクラスに変換
	public Food convertTo(UpdateFoodForm form) {
		return new Food(
				form.getFoodId(),
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
	
	//DBから取得した値(Entityクラス)をFormクラスに変換
	public static UpdateFoodForm convertFrom(Food entity) {
        return new UpdateFoodForm(
                entity.getFoodId(),
                entity.getFoodName(),
                entity.getGenreId(),
                entity.getUserId(),
                entity.getAmount(),
                entity.getBunshi(),
                entity.getBunbo(),
                entity.getDate(),
                entity.getFlag(),
                entity.getWasteDate()
        );
    }
	
	//DBから取得したList<Entity>をList<Form>に変換して返す
	public static List<UpdateFoodForm> convertFrom(List<Food> foodList) {
        return foodList.stream().map(UpdateFoodForm::convertFrom).toList();
    }
	
	//エラーチェック
	public String checkError() {
		String foodNameNullError = "食材名は必須項目です";
		String foodNameLengthError = "食材名が長すぎます";
		String genreIdError = "食材ジャンルは必須項目です";
		String bunsuError = "分子は分母より小さい数を入力してください";
		String dateError = "昨日以前の日付には設定できません";
		
		if(foodName == null || foodName.trim().isEmpty()) {
			return foodNameNullError;
		}else if(foodName.length() > 100) {
			return foodNameLengthError;
		}
		
		if(genreId == null) {
			return genreIdError;
		}
		
		if(bunshi != null && bunshi >= bunbo) {
			return bunsuError;
		}
		
		if(date != null && date.isBefore(LocalDate.now())) {
			return dateError;
		}
		
		return null;
	}
}
