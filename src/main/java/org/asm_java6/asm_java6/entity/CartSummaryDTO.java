package org.asm_java6.asm_java6.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartSummaryDTO {
    private List<?> items;  // dùng List<?> để có thể chứa cả các đối tượng của DB lẫn session
    private int totalItems;
    private double totalPrice;
}
