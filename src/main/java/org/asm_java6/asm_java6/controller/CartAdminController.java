package org.asm_java6.asm_java6.controller;

import org.asm_java6.asm_java6.Repository.CartItemRepository;
import org.asm_java6.asm_java6.entity.CartItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
public class CartAdminController {

    @Autowired
    private CartItemRepository cartItemRepository;

    // Trả về danh sách các đơn đã được đặt (ORDERED)
    @GetMapping
    public List<CartItemEntity> getAllOrderedItems() {
        return cartItemRepository.findByStatus("ORDERED");
    }

    // (Tuỳ chọn) Xoá đơn hàng
    @DeleteMapping("/{id}")
    public void deleteOrderedItem(@PathVariable("id") Integer id) {
        cartItemRepository.deleteById(id);
    }
}
