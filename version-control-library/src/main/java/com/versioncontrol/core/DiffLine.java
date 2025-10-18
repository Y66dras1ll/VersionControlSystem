package com.versioncontrol.core;

public class DiffLine {
    public enum ChangeType {
        UNCHANGED, ADDED, REMOVED, MODIFIED
    }

    private final String content;
    private final ChangeType changeType;
    private final int lineNumber;

    public DiffLine(String content, ChangeType changeType, int lineNumber) {
        this.content = content;
        this.changeType = changeType;
        this.lineNumber = lineNumber;
    }

    public String getContent() { return content; }
    public ChangeType getChangeType() { return changeType; }
    public int getLineNumber() { return lineNumber; }

    @Override
    public String toString() {
        String prefix = "";
        switch (changeType) {
            case ADDED: prefix = "+ "; break;
            case REMOVED: prefix = "- "; break;
            case MODIFIED: prefix = "~ "; break;
            default: prefix = "  "; break;
        }
        return String.format("%s%3d: %s", prefix, lineNumber, content);
    }
}