package com.cts.grantserve.Auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "researcher") // Prevents infinite loops if you log the User object
@EqualsAndHashCode(exclude = "researcher")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userID;

    private String name;
    private String role;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;
    private String status;
    private String password;

}