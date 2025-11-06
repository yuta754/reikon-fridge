package com.example.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.entities.FoodGenreEntity;
import com.example.demo.repositories.SearchFoodGenreRepository;

@Service
public class SearchFoodGenreService {

	@Autowired
	SearchFoodGenreRepository dao;
	
	public List<FoodGenreEntity> findAll(){
		return dao.findAll(Sort.by(Sort.Direction.ASC, "genreId"));
	}
}
