package com.pbl.elearning.user.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ConfigurationProperties(prefix = "e-learning-mail")
public class DomainProperties {
    @NotBlank
    private String domain;
}