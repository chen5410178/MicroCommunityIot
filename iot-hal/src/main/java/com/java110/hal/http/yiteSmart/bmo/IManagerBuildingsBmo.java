package com.java110.hal.http.yiteSmart.bmo;

import org.springframework.http.ResponseEntity;

public interface IManagerBuildingsBmo {

    ResponseEntity<String> buildings(String uuid);
}
