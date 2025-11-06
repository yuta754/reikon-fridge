package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	@Query("SELECT u.userId FROM User u WHERE u.userMail = :userMail")
	Integer findByMail(@Param("userMail") String userMail);

}