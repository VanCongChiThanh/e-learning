package com.pbl.elearning.course.payload.response;

import com.pbl.elearning.course.domain.Resource;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ResourceResponse {
    private UUID resourceId;
    private UUID lectureId;
    private String fileURL;
    private String fileType;

    public static ResourceResponse fromEntity(Resource resource) {
        return ResourceResponse.builder()
                .resourceId(resource.getResourceId())
                .lectureId(resource.getLecture() != null ? resource.getLecture().getLectureId() : null)
                .fileURL(resource.getFileUrl())
                .fileType(resource.getFileType())
                .build();
    }

}
