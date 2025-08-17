package com.pbl.elearning.web.endpoint.user;

import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.security.annotation.CurrentUser;
import com.pbl.elearning.security.domain.UserPrincipal;
import com.pbl.elearning.user.domain.UserInfo;
import com.pbl.elearning.user.payload.request.profile.UserProfileRequest;
import com.pbl.elearning.user.payload.response.UserProfileResponse;
import com.pbl.elearning.user.service.UserInfoService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RequestMapping("/v1/users")
@RestController
@RequiredArgsConstructor
public class UserInfoController {
    private final UserInfoService userInfoService;
    @PatchMapping("/me/profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiOperation("Update current user profile")
    public ResponseEntity<ResponseDataAPI> updateMyProfile(
            @RequestBody @Valid UserProfileRequest userProfileRequest,
            @CurrentUser UserPrincipal userPrincipal
            ) {
        UserInfo userInfo=userInfoService.updateUserInfo(
                userPrincipal.getId(),
                userProfileRequest.getFirstName(),
                userProfileRequest.getLastName(),
                null
        );
        return ResponseEntity.ok(ResponseDataAPI.successWithoutMeta(UserProfileResponse.toResponse(userInfo)));
    }
    @GetMapping("/me/profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiOperation("Get current user profile")
    public ResponseEntity<ResponseDataAPI> getMyProfile(
            @CurrentUser UserPrincipal userPrincipal
    ) {
        UserInfo userInfo = userInfoService.getUserInfoByUserId(userPrincipal.getId());
        return ResponseEntity.ok(ResponseDataAPI.successWithoutMeta(UserProfileResponse.toResponse(userInfo)));
    }
    @GetMapping("/{userId}/profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiOperation("Get user profile by ID")
    public ResponseEntity<ResponseDataAPI> getUserProfile(
            @PathVariable("userId") UUID userId
    ) {
        UserInfo userInfo = userInfoService.getUserInfoByUserId(userId);
        return ResponseEntity.ok(ResponseDataAPI.successWithoutMeta(UserProfileResponse.toResponse(userInfo)));
    }
}