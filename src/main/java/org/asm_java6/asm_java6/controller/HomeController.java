package org.asm_java6.asm_java6.controller;

import org.asm_java6.asm_java6.Service.ItemService;
import org.asm_java6.asm_java6.entity.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ItemService itemService;

    @GetMapping("/")
    public String home(Model model) {
        List<Item> items = itemService.findAll();
        model.addAttribute("items", items);
        return "index";
    }

}
