package org.asm_java6.asm_java6.controller;

import jakarta.servlet.http.HttpSession;
import org.asm_java6.asm_java6.Service.CartService;
import org.asm_java6.asm_java6.entity.CartSummary;
import org.asm_java6.asm_java6.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartRestfulAPI {

    @Autowired
    private CartService cartService;

    @GetMapping
    public CartSummary getCart(HttpSession session) {
        User user = (User) session.getAttribute("LOGGED_IN_USER");
        if(user == null) {
            throw new RuntimeException("User not logged in");
        }
        return cartService.getCartSummary(user);
    }

    @PostMapping("/add/{id}")
    public CartSummary addItem(@PathVariable("id") Integer id, HttpSession session) {
        User user = (User) session.getAttribute("LOGGED_IN_USER");
        if(user == null) {
            throw new RuntimeException("User not logged in");
        }
        cartService.addToCart(id, user);
        return cartService.getCartSummary(user);
    }

    @PostMapping("/update/{id}")
    public CartSummary updateQuantity(@PathVariable("id") Integer id,
                                      @RequestParam("qty") int qty,
                                      HttpSession session) {
        User user = (User) session.getAttribute("LOGGED_IN_USER");
        if(user == null) {
            throw new RuntimeException("User not logged in");
        }
        cartService.updateQuantity(id, qty, user);
        return cartService.getCartSummary(user);
    }

    @DeleteMapping("/remove/{id}")
    public CartSummary removeItem(@PathVariable("id") Integer id, HttpSession session) {
        User user = (User) session.getAttribute("LOGGED_IN_USER");
        if(user == null) {
            throw new RuntimeException("User not logged in");
        }
        cartService.removeItem(id, user);
        return cartService.getCartSummary(user);
    }
}
