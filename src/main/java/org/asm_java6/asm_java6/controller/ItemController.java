package org.asm_java6.asm_java6.controller;

import org.asm_java6.asm_java6.Service.ItemService;
import org.asm_java6.asm_java6.entity.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public List<Item> getAll() {
        return itemService.findAll();
    }

    @PostMapping
    public Item createItem(@RequestParam("name") String name,
                           @RequestParam("price") double price,
                           @RequestParam("qty") int qty,
                           @RequestParam("file") MultipartFile file,
                           @RequestParam("seller") String seller,
                           @RequestParam("category") String category,
                           @RequestParam("discount") double discount) {
        return itemService.create(name, price, qty, file, seller, category, discount);
    }

    @PutMapping("/{id}")
    public Item updateItem(@PathVariable("id") Integer id,
                           @RequestParam("name") String name,
                           @RequestParam("price") double price,
                           @RequestParam("qty") int qty,
                           @RequestParam(value = "file", required = false) MultipartFile file,
                           @RequestParam("seller") String seller,
                           @RequestParam("category") String category,
                           @RequestParam("discount") double discount) {
        return itemService.update(id, name, price, qty, file, seller, category, discount);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable("id") Integer id) {
        itemService.deleteById(id);
    }
}
