package com.tuananhdo.mapper;

import com.tuananhdo.entity.User;
import com.tuananhdo.payload.UserDTO;
import com.tuananhdo.utils.AuthenticationType;

public class UserMapper {

    public static UserDTO mapToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .photos(user.getPhotos())
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .enabled(user.isEnabled())
                .roles(user.getRoles())
                .build();
    }

    public static User maptoUser(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .photos(userDTO.getPhotos())
                .address(userDTO.getAddress())
                .phoneNumber(userDTO.getPhoneNumber())
                .enabled(userDTO.isEnabled())
                .authenticationType(AuthenticationType.DATABASE)
                .roles(userDTO.getRoles())
                .build();
    }
}
