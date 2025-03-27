package org.asm_java6.asm_java6.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.asm_java6.asm_java6.Repository.UserRepository;
import org.asm_java6.asm_java6.Service.CartService;
import org.asm_java6.asm_java6.Service.ItemService;
import org.asm_java6.asm_java6.entity.CartSummary;
import org.asm_java6.asm_java6.entity.CartSummaryDTO;
import org.asm_java6.asm_java6.entity.Item;
import org.asm_java6.asm_java6.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private CartService cartService;
    @Autowired
    private UserRepository userRepository;

    private static final String CART_SESSION_KEY = "CART";

    public static class CartItem {
        private Item item;
        private int quantity;

        public CartItem(Item item, int quantity) {
            this.item = item;
            this.quantity = quantity;
        }

        public Item getItem() { return item; }
        public void setItem(Item item) { this.item = item; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> getSessionCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        User user = (User) session.getAttribute("LOGGED_IN_USER");

        // Nếu user null nhưng vẫn đang authenticated, ta gán lại user vào session
        if (user == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                String email = auth.getName();
                user = userRepository.findByEmail(email).orElse(null);
                if (user != null) {
                    session.setAttribute("LOGGED_IN_USER", user);
                    System.out.println("Tự động lấy lại thông tin người dùng từ remember-me: " + user.getEmail());
                }
            }
        }

        if (user != null) {
            CartSummary cartSummary = cartService.getCartSummary(user);
            model.addAttribute("cartSummary", cartSummary);
            model.addAttribute("user", user); // Đảm bảo luôn có "user" cho cart.html

            try {
                ObjectMapper mapper = new ObjectMapper();
                String cartSummaryJson = mapper.writeValueAsString(cartSummary);
                model.addAttribute("cartSummaryJson", cartSummaryJson);
            } catch(Exception e) {
                e.printStackTrace();
            }
        } else {
            List<CartItem> cart = getSessionCart(session);
            int totalItems = cart.stream().mapToInt(CartItem::getQuantity).sum();
            double totalPrice = cart.stream().mapToDouble(ci -> ci.getItem().getPrice() * ci.getQuantity()).sum();
            CartSummaryDTO summary = new CartSummaryDTO(cart, totalItems, totalPrice);
            model.addAttribute("cartSummary", summary);
        }

        return "cart";
    }


    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable("id") Integer id,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                            HttpSession session) {
        User user = (User) session.getAttribute("LOGGED_IN_USER");
        if (user != null) {
            for (int i = 0; i < quantity; i++) {
                cartService.addToCart(id, user);
            }
        } else {
            Item item = itemService.findById(id);
            if (item == null) {
                return "redirect:/";
            }
            List<CartItem> cart = getSessionCart(session);
            Optional<CartItem> existing = cart.stream()
                    .filter(ci -> ci.getItem().getId().equals(id))
                    .findFirst();
            if (existing.isPresent()) {
                CartItem ci = existing.get();
                ci.setQuantity(ci.getQuantity() + quantity);
            } else {
                cart.add(new CartItem(item, quantity));
            }
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam("itemId") int itemId,
                                 @RequestParam("quantity") int quantity,
                                 HttpSession session) {
        User user = (User) session.getAttribute("LOGGED_IN_USER");
        if (user != null) {
            cartService.updateQuantity(itemId, quantity, user);
        } else {
            List<CartItem> cart = getSessionCart(session);
            for (CartItem ci : cart) {
                if (ci.getItem().getId() == itemId) {
                    ci.setQuantity(quantity);
                    break;
                }
            }
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove/{id}")
    public String removeFromCart(@PathVariable("id") Integer id, HttpSession session) {
        User user = (User) session.getAttribute("LOGGED_IN_USER");
        if (user != null) {
            cartService.removeItem(id, user);
        } else {
            List<CartItem> cart = getSessionCart(session);
            cart.removeIf(ci -> ci.getItem().getId().equals(id));
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/login?redirect=/cart/checkout";
        }

        Cookie[] cookies = request.getCookies();
        String userIdStr = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("userId".equals(cookie.getName())) {
                    userIdStr = cookie.getValue();
                    break;
                }
            }
        }

        if (userIdStr == null) {
            return "redirect:/login?redirect=/cart/checkout";
        }

        cartService.checkout((User) session.getAttribute("LOGGED_IN_USER"));
        return "redirect:/order/my-products";
    }
}
