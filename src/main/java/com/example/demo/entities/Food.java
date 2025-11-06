package com.example.demo.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "food")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Food {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "food_id", nullable = false)
	private Integer foodId;
	
	@Column(name = "food_name", nullable = false)
	private String foodName;
	
	@Column(name = "food_genre_id", nullable = false)
	private Integer genreId;
	
	@Column(name = "user_id", nullable = false)
	private Integer userId;
	
	@Column(name = "food_amount", nullable = true)
	private Integer amount;
	
	@Column(name = "bunshi", nullable = true)
	private Integer bunshi;
	
	@Column(name = "bunbo", nullable = true)
	private Integer bunbo;
	
	@Column(name = "expiration_date", nullable = true)
	private LocalDate date;
	
	@Column(name = "flag", nullable = false)
	private Integer flag;
	
	@Column(name = "waste_date", nullable = true)
	private LocalDate wasteDate;
	
	@Transient
	private double remainingPercent;

	@Transient
	private String barColor;
	
	public Food(Integer foodId, String foodName, Integer genreId, Integer userId, Integer amount, Integer bunshi, Integer bunbo, LocalDate date, Integer flag, LocalDate wasteDate) {
		this.foodId = foodId;
		this.foodName = foodName;
		this.genreId = genreId;
		this.userId = userId;
		this.amount = amount;
		this.bunshi = bunshi;
		this.bunbo = bunbo;
		this.date = date;
		this.flag = flag;
		this.wasteDate = wasteDate;
	}
}
