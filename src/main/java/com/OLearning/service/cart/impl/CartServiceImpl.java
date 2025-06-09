package com.OLearning.service.cart.impl;

import com.OLearning.dto.cart.CartDTO;
import com.OLearning.entity.Cart;
import com.OLearning.entity.CartDetail;
import com.OLearning.entity.Course;
import com.OLearning.entity.User;
import com.OLearning.mapper.cart.CartMapper;
import com.OLearning.repository.CartDetailRepository;
import com.OLearning.repository.CartRepository;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.cart.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartDetailRepository cartDetailRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CartMapper cartMapper;

    @Override
    @Transactional
    public CartDTO addToCart(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));

        Cart cart = cartRepository.findByUserUserId(userId);
        if (cart == null) {
            cart = Cart.builder()
                    .user(user)
                    .total(0L)
                    .build();
            cart = cartRepository.save(cart);
        }

        CartDetail existingDetail = cartDetailRepository.findByCartCartIdAndCourseCourseId(cart.getCartId(), courseId);
        if (existingDetail != null) {
            throw new IllegalArgumentException("Course already exists in cart");
        }

        CartDetail cartDetail = CartDetail.builder()
                .cart(cart)
                .course(course)
                .price(course.getPrice())
                .build();
        cartDetailRepository.save(cartDetail);

        cart.setTotal(cart.getTotal() + 1);
        cartRepository.save(cart);

        return cartMapper.toDTO(cart);
    }

    @Override
    public CartDTO getCartByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Cart cart = cartRepository.findByUserUserId(userId);
        if (cart == null) {
            throw new IllegalArgumentException("Cart not found for user ID: " + userId);
        }

        return cartMapper.toDTO(cart);
    }

    @Override
    @Transactional
    public void removeFromCart(Long cartDetailId) {
        CartDetail cartDetail = cartDetailRepository.findById(cartDetailId)
                .orElseThrow(() -> new IllegalArgumentException("Cart detail not found with ID: " + cartDetailId));

        Cart cart = cartDetail.getCart();
        cart.setTotal(cart.getTotal() - 1);
        cartDetailRepository.delete(cartDetail);
        cartRepository.save(cart);
    }
}