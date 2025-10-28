package com.example.demo.services;

import org.springframework.stereotype.Service;

import com.example.demo.entities.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

@Service
public class UserService {

    private final EntityManager entityManager;

    public UserService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    
   
    public User authenticate(String email, String password) {
        try {
            

            String hql = "SELECT u FROM User u WHERE u.userMail = :email AND u.userPassword = :password";
            
            

            Query query = entityManager.createQuery(hql, User.class);
            query.setParameter("email", email);
            query.setParameter("password", password);

            
            
            return (User) query.getSingleResult();

        } catch (NoResultException e) {
            // ユーザーが見つからない、またはパスワードが一致しない
            return null;
        } catch (Exception e) {
            // その他のエラー
            e.printStackTrace();
            return null;
            
            
        }
    }
}