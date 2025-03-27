package org.asm_java6.asm_java6.Service;

import org.asm_java6.asm_java6.Repository.CartItemRepository;
import org.asm_java6.asm_java6.Repository.ItemRepository;
import org.asm_java6.asm_java6.entity.CartItemEntity;
import org.asm_java6.asm_java6.entity.CartSummary;
import org.asm_java6.asm_java6.entity.Item;
import org.asm_java6.asm_java6.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private ItemRepository itemRepo;

    @Autowired
    private CartItemRepository cartItemRepo;

    // Thêm sản phẩm vào giỏ hàng của user (DB)
    public void addToCart(Integer itemId, User user) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));
        Optional<CartItemEntity> existingOpt = cartItemRepo.findByUserAndItem(user, item);
        if(existingOpt.isPresent()){
            CartItemEntity cartItem = existingOpt.get();
            // Nếu sản phẩm đã có trong giỏ và đang ở trạng thái "CART", tăng số lượng
            if("CART".equals(cartItem.getStatus())){
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                cartItemRepo.save(cartItem);
                System.out.println("Increased quantity for item id=" + itemId + " to " + cartItem.getQuantity());
            } else {
                // Nếu sản phẩm đã có nhưng đã được đặt (ORDERED), có thể tạo một dòng mới (tùy theo logic kinh doanh)
                CartItemEntity newCartItem = new CartItemEntity();
                newCartItem.setUser(user);
                newCartItem.setItem(item);
                newCartItem.setQuantity(1);
                newCartItem.setStatus("CART");
                cartItemRepo.save(newCartItem);
                System.out.println("Added new item id=" + itemId + " to cart (new row)");
            }
        } else {
            CartItemEntity cartItem = new CartItemEntity();
            cartItem.setUser(user);
            cartItem.setItem(item);
            cartItem.setQuantity(1);
            cartItem.setStatus("CART");
            cartItemRepo.save(cartItem);
            System.out.println("Added new item id=" + itemId + " to cart");
        }
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng của user (DB)
    public void updateQuantity(Integer itemId, int quantity, User user) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));
        Optional<CartItemEntity> existingOpt = cartItemRepo.findByUserAndItem(user, item);
        if(existingOpt.isPresent()){
            CartItemEntity cartItem = existingOpt.get();
            if("CART".equals(cartItem.getStatus())){
                cartItem.setQuantity(quantity);
                cartItemRepo.save(cartItem);
                System.out.println("Updated quantity for item id=" + itemId + " to " + quantity);
            }
        }
    }

    // Xóa sản phẩm khỏi giỏ hàng của user (DB)
    public void removeItem(Integer itemId, User user) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));
        Optional<CartItemEntity> existingOpt = cartItemRepo.findByUserAndItem(user, item);
        if(existingOpt.isPresent()){
            cartItemRepo.delete(existingOpt.get());
            System.out.println("Removed item id=" + itemId + " from cart");
        }
    }

    // Lấy tóm tắt giỏ hàng của user từ DB (chỉ lấy các mục có status "CART")
    public CartSummary getCartSummary(User user) {
        List<CartItemEntity> cartItems = cartItemRepo.findByUser(user)
                .stream().filter(ci -> "CART".equals(ci.getStatus()))
                .toList();
        int totalItems = cartItems.stream().mapToInt(CartItemEntity::getQuantity).sum();
        double totalPrice = cartItems.stream().mapToDouble(ci -> ci.getItem().getPrice() * ci.getQuantity()).sum();
        return new CartSummary(cartItems, totalItems, totalPrice);
    }

    // Checkout: cập nhật trạng thái của các mục trong giỏ hàng từ "CART" sang "ORDERED"
    public void checkout(User user) {
        List<CartItemEntity> cartItems = cartItemRepo.findByUser(user)
                .stream().filter(ci -> "CART".equals(ci.getStatus()))
                .toList();
        for (CartItemEntity ci : cartItems) {
            ci.setStatus("ORDERED");
            cartItemRepo.save(ci);
        }
        System.out.println("Checkout completed for user: " + user.getEmail());
    }
}
