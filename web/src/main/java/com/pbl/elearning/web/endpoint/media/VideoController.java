package com.pbl.elearning.web.endpoint.media;

import com.amazonaws.HttpMethod;
import com.pbl.elearning.common.exception.BadRequestException;
import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.file.payload.response.PresignedURLResponse;
import com.pbl.elearning.file.service.VideoHandlerService;
import com.pbl.elearning.security.annotation.CurrentUser;
import com.pbl.elearning.security.domain.UserPrincipal;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/v1/videos")
@RestController
@RequiredArgsConstructor
@Api(tags = "File APIs")
public class VideoController {
    private final VideoHandlerService videoHandlerService;

    @PostMapping("/presigned-url")
    @ApiOperation("Generate presigned URL for video upload")
    public ResponseEntity<ResponseDataAPI> getVideoPresignedUrl(
            @RequestParam String extension,
            @CurrentUser UserPrincipal userPrincipal
    ) {
        List<String> allowedExtensions = List.of(".mp4", ".mov", ".webm");

        if (!allowedExtensions.contains(extension.toLowerCase())) {
            throw new BadRequestException("File type not supported");
        }

        String fileName = "video-" + userPrincipal.getId() + "-" + System.currentTimeMillis() + extension;

        // Tạo URL presigned 30 phút
        PresignedURLResponse presignedUrl = videoHandlerService.generateVideoPresignedUrl(fileName, HttpMethod.PUT, 10);

        return ResponseEntity.ok(ResponseDataAPI.successWithoutMeta(presignedUrl));
    }

}