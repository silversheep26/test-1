package com.example.miniproject.controller;

import com.example.miniproject.dto.UserRequestDto;
import com.example.miniproject.dto.UserResponseDto;
import com.example.miniproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping("/api/signup")
    public UserResponseDto signup(@RequestBody UserRequestDto requestDto) {

        return userService.singup(requestDto);
    }

    //로그인
    @PostMapping("/api/login")
    public UserResponseDto login(@RequestBody UserRequestDto requestDto, HttpServletResponse response) {
        return userService.login(requestDto, response);
    }

    //로그아웃
    @PostMapping("/api/logout")
    public UserResponseDto logout(HttpServletRequest request) {
        System.out.println("로그아웃컨트롤러");
        return userService.logout(request);
    }

    @PostMapping("/api/post")
    public void createPost(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        System.out.println("header = " + header);
    }

}
