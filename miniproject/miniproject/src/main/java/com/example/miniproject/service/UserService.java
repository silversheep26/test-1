package com.example.miniproject.service;

import com.example.miniproject.dto.UserRequestDto;
import com.example.miniproject.dto.UserResponseDto;
import com.example.miniproject.entity.User;
import com.example.miniproject.entity.UserRoleEnum;
import com.example.miniproject.exception.ApiException;
import com.example.miniproject.exception.ExceptionEnum;
import com.example.miniproject.jwt.JwtUtil;
import com.example.miniproject.repository.BlackListRepository;
import com.example.miniproject.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    //    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";
    private final UserRepository userRepository;
    private final BlackListRepository blackListRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    // 회원가입
    public UserResponseDto singup(UserRequestDto userRequestDto){
        String userid = userRequestDto.getUserid();
        String password = passwordEncoder.encode(userRequestDto.getPassword());

        // 중복된 아이디값 체크
        if(userRepository.findByUserid(userRequestDto.getUserid()).isPresent()){
            throw new ApiException(ExceptionEnum.DUPLICATED_USER_NAME);
        }

        //관리자 권한 체크
        UserRoleEnum role = UserRoleEnum.USER;
        if(userRequestDto.isAdmin()){
//            if (!userRequestDto.getAdminToken().equals(ADMIN_TOKEN)) {
//                return new UserResponseDto("관리자 권한이 없습니다");
//            }
            role = UserRoleEnum.ADMIN;
        }

        userRepository.save(new User(userid, password, role)); // 회원저장
        return new UserResponseDto("회원가입 성공");
    }


    // 로그인
    public UserResponseDto login(UserRequestDto userRequestDto, HttpServletResponse response){
        String userId = userRequestDto.getUserid();
        String password =  userRequestDto.getPassword();

        // 사용자 확인
        Optional<User> user = userRepository.findByUserid(userId);
        if(user.isEmpty()){
            throw new ApiException(ExceptionEnum.BAD_REQUEST);
        }

        // 비밀번호 확인
        if(!passwordEncoder.matches(password, user.get().getPassword())){
            throw new ApiException(ExceptionEnum.BAD_REQUEST);
        }

        // 여기서 블랙리스트에 있는 토큰 값으로 로그인할 경우시 로그인 못하게 막기


        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.get().getUserid(), user.get().getRole()));
        return new UserResponseDto("로그인 성공");
    }

    //로그아웃
    public UserResponseDto logout(HttpServletRequest request){
        System.out.println("로그아웃 시작");
        request.removeAttribute("Authorization");
        String header = request.getHeader("Authorization");
        System.out.println("헤어입니다 : "+header);
//        Claims claims = jwtUtil.getUserInfoFromToken(jwtUtil.resolveToken(request));
//        Optional<User> user = userRepository.findByUserid(claims.getSubject());
//        blackListRepository.save(new BlackList(claims.getSubject(), header));

        System.out.println("로그아웃 종료");
        return new UserResponseDto("로그아웃 성공");
    }

}