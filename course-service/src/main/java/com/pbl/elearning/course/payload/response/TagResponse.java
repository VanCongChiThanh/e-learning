package com.pbl.elearning.course.payload.response;

import com.pbl.elearning.course.domain.Tag;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;
@Data
@Builder
public class TagResponse {
    private String name;

    public static TagResponse fromEntity(Tag tag) {
        return TagResponse.builder()
                .name(tag.getName())
                .build();
    }
}
