package com.MVP.RepositoryTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.MVP.Models.Role;
import com.MVP.Models.User;
import com.MVP.Models.UserDTO;
import com.MVP.Repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryRealizationTest {

    private final String tableName = "users";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //функции для тестов
    private void truncateTable(String tableName) throws DataAccessException {
        StringBuilder sql = new StringBuilder();
        sql.append("TRUNCATE TABLE ");
        sql.append(tableName);
        sql.append(" CASCADE");

        StringBuilder sqlForRestartId = new StringBuilder();
        sqlForRestartId.append("ALTER SEQUENCE users_id_seq RESTART WITH 1");

        try {
            jdbcTemplate.execute(sql.toString());
            jdbcTemplate.execute(sqlForRestartId.toString());
        } catch (DataAccessException ex) {
            throw ex;
        }
    }

    private void setTestData() {
        //1 вставка
        StringBuilder sql1 = new StringBuilder();
        UserDTO user1 = new UserDTO("username1", "password1", Role.USER);
        sql1.append("INSERT INTO users (username, password, role) VALUES (?, ?, ?)");

        try {
            jdbcTemplate.update(sql1.toString(), user1.getUsername(), user1.getPassword(), user1.getRole().name());
        } catch (DataAccessException ex) {
            throw ex;   
        }

        //2 вставка
        StringBuilder sql2 = new StringBuilder();
        UserDTO user2 = new UserDTO("username2", "password2", Role.USER);
        sql2.append("INSERT INTO users (username, password, role) VALUES (?, ?, ?)");

        try {
            jdbcTemplate.update(sql2.toString(), user2.getUsername(), user2.getPassword(), user2.getRole().name());
        } catch (DataAccessException ex) {
            throw ex;   
        }

        //3 вставка
        StringBuilder sql3 = new StringBuilder();
        UserDTO user3 = new UserDTO("username3", "password3", Role.USER);
        sql3.append("INSERT INTO users (username, password, role) VALUES (?, ?, ?)");

        try {
            jdbcTemplate.update(sql3.toString(), user3.getUsername(), user3.getPassword(), user3.getRole().name());
        } catch (DataAccessException ex) {
            throw ex;   
        }
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    public void addUserToDataBaseTest_Success() {
        //preparing
        try {
            truncateTable(tableName);
        } catch (DataAccessException ex) {
            fail("не удалось очистить таблицу - " + ex.getMessage());
        }

        UserDTO user1 = new UserDTO("username1", "password1", Role.USER);
        UserDTO user2 = new UserDTO("username2", "password2", Role.USER);
        UserDTO user3 = new UserDTO("username3", "password3", Role.USER);

        //act
        boolean user1IsAdded = userRepository.addUserToDataBase(user1);
        boolean user2IsAdded = userRepository.addUserToDataBase(user2);
        boolean user3IsAdded = userRepository.addUserToDataBase(user3);

        //assert
        if (user1IsAdded == false || user2IsAdded == false || user3IsAdded == false) {
            fail();
        }

        StringBuilder sqlForCheck = new StringBuilder();
        sqlForCheck.append("SELECT * FROM users");
        List<User> users = jdbcTemplate.query(sqlForCheck.toString(), (rs, rowNum) -> new User(
            rs.getInt("id"),
            rs.getString("username"), 
            rs.getString("password"), 
            Role.valueOf(rs.getString("role")))
        );

        if (users.get(2).getId() != 3 || !users.get(2).getUsername().equals("username3")) {
            System.out.println(">>> ID - " + users.get(2).getId() + " <<<");
            System.out.println(">>> USERNAME - " + users.get(2).getUsername() + " <<<");
            fail();
        }
    }

    @Test
    public void getUserFromDataBaseTest_Success() {
        //preparing
        try {
            truncateTable(tableName);
        } catch (DataAccessException ex) {
            fail("не удалось очистить таблицу - " + ex.getMessage());
        }

        setTestData();

        //act
        User user1 = userRepository.getUserFromDataBase("username1").orElse(null);
        User user2 = userRepository.getUserFromDataBase("username2").orElse(null);

        //assert
        if (user1 == null || user2 == null) {
            fail();
        }

        User expectedUser1 = new User(1, "username1", "password1", Role.USER);
        User expectedUser2 = new User(2, "username2", "password2", Role.USER);

        assertEquals(expectedUser1, user1);
        assertEquals(expectedUser2, user2);
    }
}
