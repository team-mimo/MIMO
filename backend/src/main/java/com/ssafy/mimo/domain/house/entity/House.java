package com.ssafy.mimo.domain.house.entity;

import com.ssafy.mimo.common.BaseDeletableEntity;
import com.ssafy.mimo.domain.hub.entity.Hub;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "HOUSE")
public class House extends BaseDeletableEntity {

    @NotNull
    private String address;

    @OneToMany(mappedBy = "house")
    private List<UserHouse> userHouse;

    @OneToMany(mappedBy = "house")
    private List<Hub> hub;

    public List<String> getDevices() {
        List<String> devices = new ArrayList<>();
        for (Hub hub : this.hub) {
            devices.addAll(hub.getDevices());
        }
        return devices;
    }
}
