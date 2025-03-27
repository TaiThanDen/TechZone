package org.asm_java6.asm_java6.Repository;

import org.asm_java6.asm_java6.entity.CartItemEntity;
import org.asm_java6.asm_java6.entity.Item;
import org.asm_java6.asm_java6.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Integer> {
    List<CartItemEntity> findByUser(User user);
    Optional<CartItemEntity> findByUserAndItem(User user, Item item);
    List<CartItemEntity> findByStatus(String status);
}
