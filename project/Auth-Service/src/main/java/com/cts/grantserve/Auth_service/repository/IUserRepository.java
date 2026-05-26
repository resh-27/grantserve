package com.cts.grantserve.Auth_service.repository;

import com.cts.grantserve.Auth_service.entity.User;
import com.cts.grantserve.Auth_service.projection.IUserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface IUserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query("SELECT u.userID AS userID, " +
            "u.name AS name, " +
            "u.email AS email, " +
            "u.role AS role, " +
            "u.status AS status " +
            "FROM User u WHERE u.userID = :userId")
    ResponseEntity<IUserProjection> findByUserID(@Param("userId") Long userId);

    Optional<User> findByuserID(Long userID);

    Optional<User> findByEmail(String email);

    boolean existsByEmail( String email);
    List<User> findByRole(String role);
}