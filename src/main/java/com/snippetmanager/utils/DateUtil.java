package com.snippetmanager.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    
    private static final DateTimeFormatter DEFAULT_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DISPLAY_FORMATTER = 
        DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private DateUtil() {
    }
    
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DEFAULT_FORMATTER);
    }
    
    public static String formatForDisplay(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DISPLAY_FORMATTER);
    }
    
    public static String formatDateOnly(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATE_ONLY_FORMATTER);
    }
    
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DEFAULT_FORMATTER);
        } catch (Exception e) {
            try {
                return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception e2) {
                return null;
            }
        }
    }
    
    public static String getRelativeTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(dateTime, now).toMinutes();
        
        if (minutes < 1) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";
        } else if (minutes < 1440) {
            long hours = minutes / 60;
            return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
        } else if (minutes < 10080) {
            long days = minutes / 1440;
            return days + " day" + (days == 1 ? "" : "s") + " ago";
        } else {
            return formatForDisplay(dateTime);
        }
    }
}
