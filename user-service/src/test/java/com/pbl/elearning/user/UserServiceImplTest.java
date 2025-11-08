package com.pbl.elearning.user;

import com.pbl.elearning.common.exception.BadRequestException;
import com.pbl.elearning.common.exception.InternalServerException;
import com.pbl.elearning.common.exception.NotFoundException;
import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.security.domain.User;
import com.pbl.elearning.security.repository.UserRepository;
import com.pbl.elearning.user.payload.request.user.ChangePasswordRequest;
import com.pbl.elearning.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl service;

    private UserServiceImpl spyService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setup() {
        spyService =  Mockito.spy(service);
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setEmail("user@site.com");
    }

    private ChangePasswordRequest req(String oldPwd, String newPwd, String confirmNew) {
        ChangePasswordRequest r = new ChangePasswordRequest();
        r.setOldPassword(oldPwd);
        r.setNewPassword(newPwd);
        r.setConfirmNewPassword(confirmNew);
        return r;
    }

    @Test
    void changePassword_shouldThrowBadRequest_whenOldPasswordIncorrect() {
        // BCrypt.checkpw(request.old, providedHashed) sẽ fail nếu chuỗi không khớp
        String hashedStored = BCrypt.hashpw("CORRECT_OLD", BCrypt.gensalt());
        ChangePasswordRequest request = req("WRONG_OLD", "NEW123!", "NEW123!");

        // Không cần findById vì code sẽ ném lỗi trước khi tới findById
        assertThrows(BadRequestException.class,
                () -> spyService.changePassword(userId, hashedStored, request));

        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_shouldThrowBadRequest_whenConfirmNotMatch() {
        String hashedStored = BCrypt.hashpw("OLD_123", BCrypt.gensalt());
        ChangePasswordRequest request = req("OLD_123", "NEW_ABC", "MISMATCH");

        assertThrows(BadRequestException.class,
                () -> spyService.changePassword(userId, hashedStored, request));

        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_shouldThrowBadRequest_whenNewEqualsOld() {
        String hashedStored = BCrypt.hashpw("SAME", BCrypt.gensalt());
        ChangePasswordRequest request = req("SAME", "SAME", "SAME");

        assertThrows(BadRequestException.class,
                () -> spyService.changePassword(userId, hashedStored, request));

        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_shouldThrowInternal_whenUserNotFound() {
        // Vượt qua 3 check đầu tiên để vào findById
        String hashedStored = BCrypt.hashpw("OLD_OK", BCrypt.gensalt());
        ChangePasswordRequest request = req("OLD_OK", "NEW_OK", "NEW_OK");

        doThrow(new NotFoundException("USER_NOT_FOUND"))
                .when(spyService).findById(userId);

        // Mọi Exception (không phải BadRequestException) đều bị catch và ném InternalServerException
        assertThrows(InternalServerException.class,
                () -> spyService.changePassword(userId, hashedStored, request));

        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_shouldThrowInternal_whenSaveThrows() {
        String hashedStored = BCrypt.hashpw("OLD_OK", BCrypt.gensalt());
        ChangePasswordRequest request = req("OLD_OK", "NEW_OK", "NEW_OK");

        doReturn(user).when(spyService).findById(userId);
        when(passwordEncoder.encode("NEW_OK")).thenReturn("ENCODED_NEW");
        doThrow(new RuntimeException("DB down")).when(userRepository).save(any(User.class));

        assertThrows(InternalServerException.class,
                () -> spyService.changePassword(userId, hashedStored, request));
    }

    @Test
    void changePassword_shouldSuccess_whenAllValid() {
        String hashedStored = BCrypt.hashpw("OLD_OK", BCrypt.gensalt());
        ChangePasswordRequest request = req("OLD_OK", "NEW_OK", "NEW_OK");

        doReturn(user).when(spyService).findById(userId);
        when(passwordEncoder.encode("NEW_OK")).thenReturn("ENCODED_NEW");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseDataAPI resp = spyService.changePassword(userId, hashedStored, request);

        // Đã encode & lưu
        assertEquals("ENCODED_NEW", user.getPassword());
        verify(userRepository, times(1)).save(user);

        // Response success
        assertNotNull(resp);
        assertTrue(resp.getStatus()=="success");
    }
}