package com.versioncontrol.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class FileVersion {
    private final String content;
    private final int versionNumber;
    private final LocalDateTime timestamp;
    private final String comment;

    public FileVersion(String content, int versionNumber, String comment) {
        this.content = Objects.requireNonNull(content, "Содержимое не может быть null");
        this.versionNumber = versionNumber;
        this.timestamp = LocalDateTime.now();
        this.comment = Objects.requireNonNull(comment, "Комментарий не может быть null");
    }

    // Getters
    public String getContent() { return content; }
    public int getVersionNumber() { return versionNumber; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getComment() { return comment; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return String.format("Версия %d (%s): %s",
                versionNumber, timestamp.format(formatter), comment);
    }
}