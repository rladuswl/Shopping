package com.example.weblogin.domain.cartitem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    // 상품이 장바구니에 들어있는지 조회
    CartItem findByCartIdAndItemId(int cartId, int itemId);
    List<CartItem> findCartItemByItemId(int id);
}
