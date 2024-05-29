package com.ssafy.mimo.domain.house.entity;

import com.ssafy.mimo.common.BaseDeletableEntity;
import com.ssafy.mimo.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "USER_HOUSE")
public class UserHouse extends BaseDeletableEntity {

    @NotNull
    private boolean isHome;

    @NotNull
    private String nickname;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "house_id")
    private House house;

    public void updateNickname(String nickname) {
        this.setNickname(nickname);
    }

    public void activateHome() {
        this.isHome = true;
    }

    public void deactivateHome() {
        this.isHome = false;
    }
}
