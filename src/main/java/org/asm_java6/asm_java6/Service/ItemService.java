// ItemService.java
package org.asm_java6.asm_java6.Service;

import org.asm_java6.asm_java6.Repository.ItemRepository;
import org.asm_java6.asm_java6.entity.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepo;

    @Value("${upload.dir}")
    private String uploadDir;

    public List<Item> findAll() {
        return itemRepo.findAll();
    }

    public Item findById(Integer id) {
        return itemRepo.findById(id).orElse(null);
    }

    public Item save(Item item) {
        return itemRepo.save(item);
    }

    public void deleteById(Integer id) {
        itemRepo.deleteById(id);
    }

    public Item create(String name, double price, int qty, MultipartFile file, String seller, String category, double discount) {
        Item item = new Item();
        item.setName(name);
        item.setPrice(price);
        item.setQty(qty);
        item.setSeller(seller);
        item.setCategory(category);
        item.setDiscount(discount);
        item.setSold(0);
        if (file != null && !file.isEmpty()) {
            String filename = saveFile(file);
            item.setImage(filename);
        }
        return itemRepo.save(item);
    }

    public Item update(Integer id, String name, double price, int qty, MultipartFile file, String seller, String category, double discount) {
        Item item = itemRepo.findById(id).orElse(null);
        if (item != null) {
            item.setName(name);
            item.setPrice(price);
            item.setQty(qty);
            item.setSeller(seller);
            item.setCategory(category);
            item.setDiscount(discount);
            if (file != null && !file.isEmpty()) {
                String filename = saveFile(file);
                item.setImage(filename);
            }
            return itemRepo.save(item);
        }
        return null;
    }

    private String saveFile(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) return null;

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir, filename);
            Files.createDirectories(path.getParent()); // Tạo thư mục nếu chưa tồn tại
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + e);
        }
    }
    public List<Item> findByCategory(String category) {
        return itemRepo.findByCategory(category);
    }
}
