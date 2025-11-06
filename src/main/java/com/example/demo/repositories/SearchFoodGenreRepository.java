package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entities.FoodGenreEntity;

public interface SearchFoodGenreRepository extends JpaRepository<FoodGenreEntity, Integer>{

}
