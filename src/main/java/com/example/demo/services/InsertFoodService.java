package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Food;
import com.example.demo.repositories.InsertFoodRepository;

@Service
public class InsertFoodService {

	@Autowired
	InsertFoodRepository dao;
	
	public void insertFood(Food entity) {
		dao.save(entity);
	}
}
