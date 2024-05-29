package com.ssafy.mimo.sleep.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import com.ssafy.mimo.common.BaseEntity;
import com.ssafy.mimo.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class SleepData extends BaseEntity {

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@CreatedDate
	@Column(columnDefinition = "datetime(0) default now(0)", nullable = false, updatable = false)
	private LocalDateTime createdDttm;

	private Integer sleepLevel;

}
