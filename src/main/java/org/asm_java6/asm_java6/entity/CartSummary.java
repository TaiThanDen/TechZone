package org.asm_java6.asm_java6.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CartSummary {
    private List<CartItemEntity> items;
    private int totalItems;
    private double totalPrice;
}
