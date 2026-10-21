package com.tcet.pms.model;

/**
 * Enumeration representing the urgency/priority of tasks and projects.
 */
public enum Priority {
    LOW("low", "Low"),
    MEDIUM("medium", "Medium"),
    HIGH("high", "High");

    private final String code;
    private final String displayName;

    Priority(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Priority fromCode(String code) {
        if (code == null) return MEDIUM;
        String normalized = code.trim().toLowerCase();
        for (Priority p : values()) {
            if (p.code.equalsIgnoreCase(normalized) || p.name().equalsIgnoreCase(normalized)) {
                return p;
            }
        }
        return MEDIUM;
    }
}
