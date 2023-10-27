package com.tuananhdo.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public enum TimeUnit {
    YEAR(365L, "year"),
    MONTH(30L, "month"),
    WEEK(7L, "week"),
    DAY(1L, "day"),
    HOUR(1L / 24L, "hour"),
    MINUTE(1 / (24L * 60L), "minute"),
    AGO(0L, "ago"),
    JUST_NOW(0L, "Just now");


    private static final String PLURAL_SUFFIX = "s";
    private static final long VALUE_ONE = 1L;
    private static final long VALUE_TWO = 2L;

    private final Long value;
    private final String name;

    TimeUnit(Long value, String name) {
        this.value = value;
        this.name = name;
    }

    public static String formatTimeWithPlural(long value, String name, String agoName) {
        if (value >= VALUE_TWO) {
            name = name.concat(TimeUnit.PLURAL_SUFFIX);
        }
        return String.format("%d %s %s", value, name, agoName);
    }

    public Long getValue() {
        return value;
    }

    public String getName() {
        return name;
    }


    public static String getTimePost(LocalDateTime pastTime, LocalDateTime nowTime) {
        Duration duration = Duration.between(pastTime, nowTime);
        long numberOfDays = getNumberOfDays(duration);
        String agoName = TimeUnit.AGO.getName();
        return getFormattedTime(duration, numberOfDays, agoName);
    }

    private static long getNumberOfDays(Duration duration) {
        return duration.toDays();
    }

    private static String getFormattedTime(Duration duration, long numberOfDays, String agoName) {
        Map<TimeUnit, Long> timeUnits = new LinkedHashMap<>();
        timeUnits.put(TimeUnit.YEAR, TimeUnit.YEAR.getValue());
        timeUnits.put(TimeUnit.MONTH, TimeUnit.MONTH.getValue());
        timeUnits.put(TimeUnit.WEEK, TimeUnit.WEEK.getValue());
        timeUnits.put(TimeUnit.DAY, TimeUnit.DAY.getValue());

        return timeUnits.entrySet().stream()
                .filter(keyTime -> numberOfDays >= keyTime.getValue())
                .map(keyTime -> formatTimeWithPlural(numberOfDays / keyTime.getValue(), keyTime.getKey().getName(), agoName))
                .findFirst()
                .orElseGet(() -> {
                    if (duration.toHours() >= VALUE_ONE) {
                        return formatTimeWithPlural(duration.toHours(), TimeUnit.HOUR.getName(), agoName);
                    } else if (duration.toMinutes() >= VALUE_TWO) {
                        return formatTimeWithPlural(duration.toMinutes(), TimeUnit.MINUTE.getName(), agoName);
                    } else {
                        return TimeUnit.JUST_NOW.getName();
                    }
                });
    }
}
