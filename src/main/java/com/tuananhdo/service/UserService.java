package com.tuananhdo.service;

import com.tuananhdo.entity.Role;
import com.tuananhdo.exception.EmailDuplicatedException;
import com.tuananhdo.exception.UserNotFoundException;
import com.tuananhdo.payload.UserDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

    Page<UserDTO> findAllUserByPage(int pageNumber,String keyword);

    List<Role> listRoles();

    UserDTO saveUser(UserDTO userDTO);

    void updateUser(UserDTO user) throws UserNotFoundException, EmailDuplicatedException;

    void deleteUserById(Long userId);

    UserDTO findByUserId(Long id) throws UserNotFoundException;

    void updateUserEnabledStatus(Long userId,boolean enabled);

}
