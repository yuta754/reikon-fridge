package com.example.demo.forms;

import lombok.Data;

@Data
public class UseFoodForm {

	//ラジオボタン
	private String amount;
	
	//食材量(整数)
	private Integer seisu;
	
	//食材量(分子)
	private Integer bunshi;
	
	//食材量(分母)
	private Integer bunbo;
	
}
