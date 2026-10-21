package com.tcet.pms.model;

/**
 * Enumeration representing the progression state of a task in the Kanban board.
 * Type-safe replacement for arbitrary string constants.
 */
public enum Status {
    TODO("todo", "To Do"),
    IN_PROGRESS("inprogress", "In Progress"),
    COMPLETED("completed", "Completed");

    private final String code;
    private final String displayName;

    Status(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Status fromCode(String code) {
        if (code == null) return TODO;
        String normalized = code.trim().toLowerCase().replace("-", "").replace("_", "");
        for (Status s : values()) {
            if (s.code.equalsIgnoreCase(normalized) || s.name().replace("_", "").equalsIgnoreCase(normalized)) {
                return s;
            }
        }
        return TODO;
    }
}
