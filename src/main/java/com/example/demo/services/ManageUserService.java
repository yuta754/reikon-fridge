package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
public class ManageUserService {

    @Autowired
    UserRepository dao;

    @PersistenceContext
    EntityManager em;

    // 全件取得
    public List<User> getAllUser() {
        return dao.findAll();
    }

    // 1件取得（IDで）
    public Optional<User> findById(Integer id) {
        return dao.findById(id);
    }

    // ユーザIDを取得（メールアドレスで）
    public Integer findByMail(String mail) {
    	return dao.findByMail(mail);
    }
    
    // 登録
    public void insertUser(User user) {
        dao.save(user);
    }

    // 更新
    public void updateUser(User user) {
        dao.save(user);
    }

    // 削除
    @Transactional
    public void deleteUser(Integer id) {
        em.createNativeQuery("DELETE FROM food WHERE user_id = ?")
            .setParameter(1, id)
            .executeUpdate();

        dao.deleteById(id);
    }

    /**
     * ログイン認証処理（平文パスワード版）
     */
    @Transactional(readOnly = true)
    public User authenticate(String userMail, String rawPassword) {
        // 1) メールアドレスに一致するユーザ取得
        List<User> list = em.createQuery(
                "SELECT u FROM User u WHERE u.userMail = :mail", User.class)
                .setParameter("mail", userMail)
                .setMaxResults(1)
                .getResultList();

        if (list.isEmpty()) return null;

        User u = list.get(0);
        String stored = u.getUserPassword();

        if (stored == null) return null;

        // 2) 平文で照合
        return stored.equals(rawPassword) ? u : null;
    }
}