package com.vault.velocitylimits.domain.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * @author harshagangavarapu
 */
public class DateTimeUtil {
    // prevent initialization
    private DateTimeUtil() {
    }

    /**
     * Calculates the start of the business day for the given transaction load time
     *
     * @param loadTime
     * @return LocalDateTime
     */
    public static LocalDateTime getStartOfDay(LocalDateTime loadTime) {
        LocalDate localDate = loadTime.toLocalDate();
        return LocalDateTime.of(localDate, LocalTime.MIN.truncatedTo(ChronoUnit.SECONDS));
    }

    /**
     * Calculates the end of the business day for the given transaction load time
     *
     * @param loadTime
     * @return LocalDateTime
     */
    public static LocalDateTime getEndOfDay(LocalDateTime loadTime) {
        LocalDate localDate = loadTime.toLocalDate();
        return LocalDateTime.of(localDate, LocalTime.MAX.truncatedTo(ChronoUnit.SECONDS));
    }

    /**
     * Calculates the start of the business week for the given transaction load time
     *
     * @param loadTime
     * @return LocalDateTime
     */
    public static LocalDateTime getStartOfWeek(LocalDateTime loadTime) {
        LocalDate localDate = loadTime.toLocalDate();
        LocalDate monday = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return LocalDateTime.of(monday, LocalTime.MIN.truncatedTo(ChronoUnit.SECONDS));
    }

    /**
     * Calculates the end of the business week for the given transaction load time
     *
     * @param loadTime
     * @return LocalDateTime
     */
    public static LocalDateTime getEndOfWeek(LocalDateTime loadTime) {
        LocalDate localDate = loadTime.toLocalDate();
        LocalDate sunday = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return LocalDateTime.of(sunday, LocalTime.MAX.truncatedTo(ChronoUnit.SECONDS));
    }
}
