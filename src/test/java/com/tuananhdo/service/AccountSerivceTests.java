package com.tuananhdo.service;

import com.tuananhdo.repository.UserRepository;
import com.tuananhdo.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountSerivceTests {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    public void testGetAccountInLogged() {
    }

}
