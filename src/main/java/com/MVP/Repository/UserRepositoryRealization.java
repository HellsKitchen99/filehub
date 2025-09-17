package com.MVP.Repository;

import java.util.List;
import java.util.Optional;
import com.MVP.Models.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.MVP.Models.User;
import com.MVP.Models.UserDTO;

@Repository
public class UserRepositoryRealization implements UserRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public UserRepositoryRealization(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //добавление юзера в базу
    public boolean addUserToDataBase(UserDTO userDTO) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO users (username, password, role) VALUES (?, ?, ?)");

        int result = jdbcTemplate.update(sql.toString(), userDTO.getUsername(), userDTO.getPassword(), userDTO.getRole().name());

        if (result != 1) {
            return false;
        }

        return true;
    }

    //взятие пользователя из базы
    public Optional<User> getUserFromDataBase(String username) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT id, username, password, role FROM users WHERE username = ?");

        List<User> users = jdbcTemplate.query(sql.toString(), (rs, rowNum) -> new User(
            rs.getInt("id"), 
            rs.getString("username"), 
            rs.getString("password"),
            Role.valueOf(rs.getString("role"))), 
            username);

            return users.stream().findFirst();
    }
}
