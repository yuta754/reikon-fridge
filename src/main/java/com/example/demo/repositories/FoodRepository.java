package com.example.demo.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Food;

@Repository
public interface FoodRepository extends JpaRepository<Food, Integer> {

	List<Food> findByfoodNameContaining(String keyword);
	
	List<Food> findByFlagOrderByFoodIdAsc(Integer flag);
	
	List<Food> findByFoodNameContainingAndFlag(String keyword, Integer flag);

	List<Food> findByDateBeforeAndFlagNot(LocalDate date, int flag);
	
	List<Food> findByFlag(Integer flag);
	
	List<Food> findByUserIdAndFlagIn(Integer userId, List<Integer> flags);

	List<Food> findByUserIdAndFlagOrderByDateDesc(Integer userId, Integer flag);

	List<Food> findByUserIdAndFoodNameContainingAndFlag(Integer userId, String foodName, Integer flag);

	
	/**
     * ユーザーIDとflagで食材を検索
     * @param userId ユーザーID
     * @param flag フラグ（0=有効、1=削除済みなど）
     * @return 該当する食材リスト
     */
    List<Food> findByUserIdAndFlag(Integer userId, Integer flag);
    
    /**
     * ユーザーIDで食材を検索
     * @param userId ユーザーID
     * @return 該当する食材リスト
     */
    List<Food> findByUserId(Integer userId);
	
	@Query("SELECT f FROM Food f ORDER BY (f.bunshi * 1.0 / f.bunbo) ASC")
    List<Food> findAllOrderByRemaining();
}