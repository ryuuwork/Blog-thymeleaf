package com.tuananhdo.mapper;

import com.tuananhdo.entity.User;
import com.tuananhdo.payload.UserDTO;

public class UserMapper {
    public static UserDTO mapToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .photos(user.getPhotos())
                .password(user.getPassword())
                .createdOn(user.getCreatedOn())
                .updatedOn(user.getUpdatedOn())
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
                .createdOn(userDTO.getCreatedOn())
                .updatedOn(userDTO.getUpdatedOn())
                .photos(userDTO.getPhotos())
                .address(userDTO.getAddress())
                .phoneNumber(userDTO.getPhoneNumber())
                .enabled(userDTO.isEnabled())
                .authenticationType(userDTO.getAuthenticationType())
                .roles(userDTO.getRoles())
                .build();
    }
}
