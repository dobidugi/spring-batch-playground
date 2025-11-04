package com.playground.redis.vo;

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