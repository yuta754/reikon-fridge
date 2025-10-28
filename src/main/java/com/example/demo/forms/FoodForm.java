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
public class FoodForm {

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
	private Integer flag;

	public static FoodForm convertFrom(Food entity) {
        return new FoodForm(
                entity.getFoodId(),
                entity.getFoodName(),
                entity.getGenreId(),
                entity.getUserId(),
                entity.getAmount(),
                entity.getBunshi(),
                entity.getBunbo(),
                entity.getDate(),
                entity.getFlag()
        );
    }
	
	public static List<FoodForm> convertFrom(List<Food> foodList) {
        return foodList.stream().map(FoodForm::convertFrom).toList();
    }
}
