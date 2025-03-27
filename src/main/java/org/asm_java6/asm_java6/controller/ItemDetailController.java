package org.asm_java6.asm_java6.controller;

import org.asm_java6.asm_java6.entity.Item;
import org.asm_java6.asm_java6.Service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/items")
public class ItemDetailController {

    @Autowired
    private ItemService itemService;

    @GetMapping("/{id}")
    public String getItemDetail(@PathVariable("id") Integer id, Model model) {
        Item item = itemService.findById(id);
        if(item == null) {
            return "redirect:/";
        }
        model.addAttribute("item", item);
        List<Item> related = itemService.findByCategory(item.getCategory());
        related.removeIf(i -> i.getId().equals(id));
        model.addAttribute("relatedItems", related);
        return "detail";
    }
}
