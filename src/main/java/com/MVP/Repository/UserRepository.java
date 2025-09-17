package com.MVP.Repository;

import com.MVP.Models.UserDTO;
import com.MVP.Models.User;
import java.util.Optional;

public interface UserRepository {
    boolean addUserToDataBase(UserDTO user);
    Optional<User> getUserFromDataBase(String username);
}
