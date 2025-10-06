package com.pbl.elearning.notification.domain;

import com.pbl.elearning.notification.domain.enums.Platform;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "device_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID userId;

    private String deviceToken;

    @Enumerated(EnumType.STRING)
    private Platform platform; // ANDROID, IOS
}