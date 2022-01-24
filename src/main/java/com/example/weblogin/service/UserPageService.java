package com.example.weblogin.service;

import com.example.weblogin.domain.user.User;
import com.example.weblogin.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserPageService {

    private final UserRepository userRepository;

    public User findUser(Integer id) {
        return userRepository.findById(id).get();
    }

    // 회원(판매자) 정보 수정
    public void userModify(User user) {
        User update = userRepository.findById(user.getId());
        update.setUsername(user.getUsername());
        update.setEmail(user.getEmail());
        update.setAddress(user.getAddress());
        update.setPhone(user.getPhone());
        userRepository.save(update);

    }

}
