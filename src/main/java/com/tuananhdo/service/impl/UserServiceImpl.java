package com.tuananhdo.service.impl;

import com.tuananhdo.entity.Role;
import com.tuananhdo.entity.User;
import com.tuananhdo.exception.UserNotFoundException;
import com.tuananhdo.mapper.UserMapper;
import com.tuananhdo.payload.UserDTO;
import com.tuananhdo.repository.RoleRepository;
import com.tuananhdo.repository.UserRepository;
import com.tuananhdo.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    public static final int USERS_SIZE_PAGE = 6;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<UserDTO> findAllUserByPage(int pageNumber, String keyword) {
        Pageable pageable = PageRequest.of(pageNumber - 1, USERS_SIZE_PAGE);
        Page<User> user = Objects.nonNull(keyword)
                ? userRepository.findAllUserByKeyWord(pageable, keyword)
                : userRepository.findAll(pageable);
        return user.map(UserMapper::mapToUserDTO);
    }


    @Override
    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        User user = UserMapper.maptoUser(userDTO);
        encodedPassword(user);
        User saveUser = userRepository.save(user);
        return UserMapper.mapToUserDTO(saveUser);
    }

    private void encodedPassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    @Override
    public void updateUser(UserDTO userInForm) throws UserNotFoundException {
        User userInDB = userRepository.findById(userInForm.getId()).orElseThrow(()
                -> new UserNotFoundException("User ID " + userInForm.getId() + " not found"));
        if (userInForm.getPassword().isEmpty()) {
            userInForm.setPassword(userInDB.getPassword());
        } else {
            encodedPassword(userInDB);
        }
        User user = UserMapper.maptoUser(userInForm);
        userRepository.save(user);
        UserMapper.mapToUserDTO(user);
    }

    @Override
    public UserDTO findByUserId(Long id) throws UserNotFoundException {
        return userRepository.findById(id)
                .map(UserMapper::mapToUserDTO)
                .orElseThrow(() -> new UserNotFoundException("User ID " + id + " not found"));
    }

    @Override
    public void updateUserEnabledStatus(Long userId, boolean enabled) {
        userRepository.updateEnabledStatus(userId, enabled);
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }
}
