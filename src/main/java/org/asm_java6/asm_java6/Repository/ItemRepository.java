package org.asm_java6.asm_java6.Repository;

import org.asm_java6.asm_java6.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    // Tìm danh sách Item theo category
    List<Item> findByCategory(String category);
}
