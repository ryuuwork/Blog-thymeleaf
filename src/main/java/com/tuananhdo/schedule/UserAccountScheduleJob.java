package com.tuananhdo.schedule;

import com.tuananhdo.entity.User;
import com.tuananhdo.service.UserService;
import com.tuananhdo.service.impl.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
@AllArgsConstructor
public class UserAccountScheduleJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    public static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private final UserService userService;

//    @Scheduled(cron = "0 0 * * * *")
    public void unLockAllUsersAccount() {
        List<User> lockUserList = userService.getAllExpiredLockedAccounts();
        if (!lockUserList.isEmpty()) {
            lockUserList.forEach(userService::unlock);
        } else {
            LOGGER.info("No accounts found to unlock.");
        }
        LOGGER.info("Cron job to unlock accounts finished.");
    }
}
