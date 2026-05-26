package com.cts.grantserve.Auth_service.util;


import com.cts.grantserve.Auth_service.dto.request.UserRequestDto;
import com.cts.grantserve.Auth_service.entity.User;
import lombok.Data;

@Data
public class ClassUtilSeparator {
    public static User userUpdateUtil(UserRequestDto userRequestDto, User user){
        user.setEmail(userRequestDto.email());
        user.setName(userRequestDto.name());
        user.setPhone(userRequestDto.phone());
        user.setEmail(userRequestDto.email());
        return  user;
    }
    public static User userRegisterUtil(UserRequestDto userRequestDto) {

        User newUser = new User();
        newUser.setName(userRequestDto.name());
        newUser.setEmail(userRequestDto.email());
        newUser.setPhone(userRequestDto.phone());
        newUser.setRole(userRequestDto.role());

        return newUser;
    }

}
