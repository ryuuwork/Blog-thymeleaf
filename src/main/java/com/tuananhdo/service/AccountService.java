package com.tuananhdo.service;

import com.tuananhdo.exception.PasswordValidationException;
import com.tuananhdo.exception.UserNotFoundException;
import com.tuananhdo.payload.AccountDTO;

public interface AccountService {

    AccountDTO getLoggedAccount();

    AccountDTO updateAccountDetails(AccountDTO accountDTO) throws UserNotFoundException, PasswordValidationException;
}
