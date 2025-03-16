package com.example.ipro_hr.repository;


import java.util.UUID;
import com. example.ipro_hr.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByChatId(String chatId);

    User findByChatId(String chatId);

//    User getUserByFistNameAndLastName(String firstName,String lastName);
//    User getUserByFullName(String fullName);
}
