package com.ssafy.mimo.domain.window.entity;

import com.ssafy.mimo.common.BaseDeviceEntity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class SlidingWindow extends BaseDeviceEntity {
	@NotNull
	private Integer openDegree;
}
