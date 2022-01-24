package com.example.weblogin.service;

import com.example.weblogin.domain.cart.Cart;
import com.example.weblogin.domain.cart.CartRepository;
import com.example.weblogin.domain.cartitem.CartItem;
import com.example.weblogin.domain.cartitem.CartItemRepository;
import com.example.weblogin.domain.item.Item;
import com.example.weblogin.domain.item.ItemRepository;
import com.example.weblogin.domain.user.User;
import com.example.weblogin.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;

    // 회원가입 하면 회원 당 카트 하나 생성
    public void createCart(User user){
        Cart cart = Cart.createCart(user);
        cartRepository.save(cart);
    }

    // 장바구니 담기
    public void addCart(User user, Item newitem, int amount) {

        // 유저 id로 장바구니가 있는지 없는지 찾기
        Cart cart = cartRepository.findByUserId(user.getId());

        // 장바구니가 존재하지 않는다면
        if (cart == null) {
            cart = Cart.createCart(user);
            cartRepository.save(cart);
        }

        Item item = itemRepository.findItemById(newitem.getId());
        CartItem cartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());


        // 상품이 장바구니에 존재하지 않는다면 카트상품 생성 후 추가
        if (cartItem == null) {
            cartItem = CartItem.createCartItem(cart, item, amount);
            cartItemRepository.save(cartItem);
        }
        // 상품이 장바구니에 이미 존재한다면 수량만 증가
        else {
            CartItem update = cartItem;
            update.setCart(cartItem.getCart());
            update.setItem(cartItem.getItem());
            update.addCount(amount);
            update.setCount(update.getCount());
            cartItemRepository.save(update);

        }
    }

    // id에 해당하는 장바구니 찾기
    public Cart findUserCart(int userId) {
        return cartRepository.findByUserId(userId);
    }

    // 카트 상품 리스트 중 해당하는 유저가 담은 상품만 반환
    // 유저의 카트 id와 카트상품의 id가 같아야 함
    public List<CartItem> allUserCartView(Cart userCart) {

        int userCartId = userCart.getId(); // 유저의 카트 id를 꺼냄
        List<CartItem> UserCartItems = new ArrayList<>(); // id에 해당하는 유저가 담은 상품들 넣어둘 곳

        List<CartItem> CartItems = cartItemRepository.findAll(); // 어느 유저 상관 없이 카트에 있는 상품 모두 불러오기

        for(CartItem cartItem : CartItems) {
            if(cartItem.getCart().getId() == userCartId) {
                UserCartItems.add(cartItem);
            }
        }

        return UserCartItems;
    }

    // 장바구니 상품 개별 삭제 -> cartItemId 받아서 삭제
    public void cartItemDelete(int id) {

        cartItemRepository.deleteById(id);
    }

    // 장바구니 아이템 전체 삭제 -> 매개변수는 유저 id
    public void allCartItemDelete(int id){
        List<CartItem> cartItems = cartItemRepository.findAll(); // 전체 cartItem 찾기

        // 반복문을 이용하여 해당하는 User의 CartItem 만 찾아서 삭제
        for(CartItem cartItem : cartItems){
            if(cartItem.getCart().getUser().getId() == id){
                int stock = cartItem.getItem().getStock();
                stock = stock - cartItem.getCount();
                cartItem.getItem().setStock(stock);
                cartItem.getCart().setCount(cartItem.getCart().getCount() - 1);
                cartItemRepository.deleteById(cartItem.getId());
            }
        }
    }

}
