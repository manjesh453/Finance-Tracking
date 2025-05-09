package com.users.repo;

import com.users.entity.Users;
import com.users.shared.Status;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users, Long> {
    @Query("SELECT u from Users u where u.email= :email")
    Optional<Users> findByEmail(@Param("email")String email);

    List<Users> findByStatus(Status status);

    @Query("SELECT u FROM Users u WHERE u.verificationCode = :verificationCode")
    Users findByVerificationCode(@Param("verificationCode") int verificationCode);

    List<Users> findByCreatedDateBetween(Date startDate, Date endDate);

    Integer countByStatus(Status status);
}
