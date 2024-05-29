package com.ssafy.mimo.common;

import com.ssafy.mimo.domain.hub.entity.Hub;
import com.ssafy.mimo.user.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class BaseDeviceEntity extends BaseUnregisterableEntity {
    @ManyToOne
    @JoinColumn(name = "user_id")
    @Nullable
    private User user;

    @ManyToOne
    @JoinColumn(name = "hub_id")
    @Nullable
    private Hub hub;

    private boolean isAccessible;

    @NotNull
    private String macAddress;
}
