package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Food;
import com.example.demo.repositories.UpdateFoodRepository;

@Service
public class UpdateFoodService {

	@Autowired
	UpdateFoodRepository dao;
	
	public void updateFood(Food entity) {
		dao.save(entity);
	}
}
