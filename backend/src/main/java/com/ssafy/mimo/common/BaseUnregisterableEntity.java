package com.ssafy.mimo.common;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class BaseUnregisterableEntity extends BaseEntity {
    @Nullable
    private LocalDateTime registeredDttm;
    @Nullable
    private LocalDateTime unregisteredDttm;
    @Builder.Default
    private boolean isRegistered = false;
    @NotNull
    private String nickname;
}
