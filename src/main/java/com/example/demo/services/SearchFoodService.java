package com.example.demo.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Food;
import com.example.demo.repositories.SearchFoodRepository;

@Service
public class SearchFoodService {

	@Autowired
	SearchFoodRepository dao;
	
	public List<Food> findAll(){
		return dao.findAll(Sort.by(Sort.Direction.ASC, "foodId"));
	}
	
	public List<Food> findById(Integer foodId){
		Optional<Food> optional = dao.findById(foodId);
		
		return optional
				.map(Collections::singletonList)  // 値があれば1要素のリストに変換
				.orElseGet(Collections::emptyList); // 値がなければ空リストを返す
	}
}
