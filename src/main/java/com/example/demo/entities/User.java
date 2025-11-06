package com.example.demo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    
    private String userName;
    
    private String userMail;
    
    private String userPassword;

    

    public Integer getUserId() {
    	return userId;
    }
    public void setUserId(Integer userId) {
    	this.userId = userId;
    }
    
    public String getUserName() { 
    	return userName;
    }
    public void setUserName(String userName) {
    	this.userName = userName; 
    }
    
    public String getUserMail() { 
    	return userMail; 
    }
    public void setUserMail(String userMail) { 
    	this.userMail = userMail; 
    }
    
    public String getUserPassword() { 
    	return userPassword; 
    }
    public void setUserPassword(String userPassword) { 
    	this.userPassword = userPassword; 
    }
}