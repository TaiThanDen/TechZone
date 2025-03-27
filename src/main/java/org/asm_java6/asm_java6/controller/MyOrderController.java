package org.asm_java6.asm_java6.controller;

import jakarta.servlet.http.HttpSession;
import org.asm_java6.asm_java6.Repository.CartItemRepository;
import org.asm_java6.asm_java6.entity.CartItemEntity;
import org.asm_java6.asm_java6.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/order")
public class MyOrderController {

    @Autowired
    private CartItemRepository cartItemRepo;

    @GetMapping("/my-products")
    public String myOrders(HttpSession session, Model model) {
        User user = (User) session.getAttribute("LOGGED_IN_USER");
        if (user == null) {
            return "redirect:/login?redirect=/order/my-products";
        }

        List<CartItemEntity> orderedItems = cartItemRepo.findByUser(user).stream()
                .filter(ci -> "ORDERED".equals(ci.getStatus()))
                .toList();

        model.addAttribute("orders", orderedItems);
        return "myorder"; // trỏ tới myorder.html
    }
}