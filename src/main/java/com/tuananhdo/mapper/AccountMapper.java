package com.tuananhdo.mapper;

import com.tuananhdo.entity.User;
import com.tuananhdo.payload.AccountDTO;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class AccountMapper {

    private final ModelMapper mapper;

    public AccountDTO mapToAccountDTO(User user) {
        return mapper.map(user, AccountDTO.class);
    }
}
