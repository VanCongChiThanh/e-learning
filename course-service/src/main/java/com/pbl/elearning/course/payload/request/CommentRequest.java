package com.pbl.elearning.course.payload.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentRequest {

    @NotBlank(message = "Nội dung comment không được để trống")
    private String content;
}
