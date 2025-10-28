package com.playground.flatfilewriter.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class DeathNote {
//    private String victimId;
//    private String victimName;
//    private String executionDate;
//    private String causeOfDeath;
//}
public record DeathNote(
        String victimId,
        String victimName,
        String executionDate,
        String causeOfDeath
) {}