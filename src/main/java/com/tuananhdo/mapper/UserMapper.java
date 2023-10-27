package com.tuananhdo.mapper;

import com.tuananhdo.entity.User;
import com.tuananhdo.payload.UserDTO;
import com.tuananhdo.utils.AuthenticationType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserMapper {

    public static UserDTO mapToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .enabled(true)
                .photos(user.getPhotos())
                .roles(user.getRoles())
                .build();
    }

    public static User maptoUser(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .enabled(true)
                .photos(userDTO.getPhotos())
                .authenticationType(AuthenticationType.DATABASE)
                .roles(userDTO.getRoles())
                .build();
    }
}
