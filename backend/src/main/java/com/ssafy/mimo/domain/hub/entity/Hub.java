package com.ssafy.mimo.domain.hub.entity;

import com.ssafy.mimo.common.BaseUnregisterableEntity;
import com.ssafy.mimo.domain.curtain.entity.Curtain;
import com.ssafy.mimo.domain.house.entity.House;
import com.ssafy.mimo.domain.lamp.entity.Lamp;
import com.ssafy.mimo.domain.light.entity.Light;
import com.ssafy.mimo.domain.window.entity.SlidingWindow;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "HUB")
public class Hub extends BaseUnregisterableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HOUSE_ID")
    @Nullable
    private House house;

    @NotNull
    private String serialNumber;

    @OneToMany(mappedBy = "hub")
    private List<Lamp> lamp;

    @OneToMany(mappedBy = "hub")
    private List<Light> light;

    @OneToMany(mappedBy = "hub")
    private List<SlidingWindow> slidingWindow;

    @OneToMany(mappedBy = "hub")
    private List<Curtain> curtain;

    public List<String> getDevices() {
        List<String> devices = new ArrayList<>();
        if (!lamp.isEmpty()) {
            devices.add("lamp");
        }
        if (!light.isEmpty()) {
            devices.add("light");
        }
        if (!slidingWindow.isEmpty()) {
            devices.add("window");
        }
        if (!curtain.isEmpty()) {
            devices.add("curtain");
        }
        return devices;
    }
}
