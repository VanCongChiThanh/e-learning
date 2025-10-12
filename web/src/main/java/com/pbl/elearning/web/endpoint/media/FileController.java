package com.pbl.elearning.web.endpoint.media;

import com.amazonaws.HttpMethod;
import com.pbl.elearning.common.constant.MessageConstant;
import com.pbl.elearning.common.exception.BadRequestException;
import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.file.payload.response.PresignedURLResponse;
import com.pbl.elearning.file.service.FileHandlerService;
import com.pbl.elearning.security.annotation.CurrentUser;
import com.pbl.elearning.security.domain.UserPrincipal;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/files")
@RestController
@RequiredArgsConstructor
@Api(tags = "File APIs")
public class FileController {

    private final FileHandlerService fileHandlerService;

    @PostMapping("/presigned-url")
    @ApiOperation("Generate presigned URL for file upload")
    public ResponseEntity<ResponseDataAPI> getPresignedUrl(
            @RequestParam String extension,
            @CurrentUser UserPrincipal userPrincipal
    ) {
        List<String> allowedExtensions = List.of(".png", ".jpg", ".jpeg", ".pdf");
        if (!allowedExtensions.contains(extension.toLowerCase())) {
            throw new BadRequestException(MessageConstant.FILE_NOT_FORMAT);
        }
        PresignedURLResponse presignedUrl = fileHandlerService.generatePresignedUrl( userPrincipal.getId()+"-"+ DateTime.now()+"."+extension, HttpMethod.PUT);
        return ResponseEntity.ok(ResponseDataAPI.successWithoutMeta(presignedUrl));
    }
    @GetMapping("/download-url")
    @ApiOperation("Generate presigned URL for file download")
    public ResponseEntity<ResponseDataAPI> getDownloadUrl(
            @RequestParam String fileName
    ) {
        PresignedURLResponse presignedUrl = fileHandlerService.generatePresignedUrl(fileName, HttpMethod.GET);
        return ResponseEntity.ok(ResponseDataAPI.successWithoutMeta(presignedUrl));
    }
}