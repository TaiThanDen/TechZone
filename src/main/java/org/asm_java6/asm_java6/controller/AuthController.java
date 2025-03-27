package org.asm_java6.asm_java6.controller;

import org.asm_java6.asm_java6.entity.User;
import org.asm_java6.asm_java6.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    // Hiển thị trang đăng nhập
    @GetMapping("/login")
    public String showLoginForm() {
        return "login-register"; // file login-register.html trong folder templates
    }

    // Hiển thị trang đăng ký
    @GetMapping("/register")
    public String showRegisterForm() {
        return "login-register";
    }

    // Xử lý đăng ký
    @PostMapping("/register")
    public String doRegister(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ) {
        if (userRepo.findByEmail(email).isPresent()) {
            return "redirect:/register?error=EmailExists";
        }
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(password); // Demo: plain-text
        newUser.setRole(true);
        userRepo.save(newUser);
        return "redirect:/login?success=registered";
    }
}
