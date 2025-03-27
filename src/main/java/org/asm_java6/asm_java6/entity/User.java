package org.asm_java6.asm_java6.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Users") // Bảng Users
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // name NVARCHAR(100) NOT NULL
    private String name;

    // email NVARCHAR(100) UNIQUE NOT NULL
    @Column(unique = true, nullable = false)
    private String email;

    // password NVARCHAR(255) NOT NULL
    private String password;

    // avatar NVARCHAR(255)
    private String avatar; // có thể để null

    // bio NVARCHAR(500)
    private String bio; // có thể để null

    // role BIT NOT NULL DEFAULT 1
    // 1 = User, 0 = Admin
    private boolean role = true; // mặc định true (User)
}
