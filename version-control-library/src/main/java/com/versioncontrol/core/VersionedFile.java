package com.versioncontrol.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VersionedFile {
    private final String fileName;
    private final List<FileVersion> versions;

    public VersionedFile(String fileName) {
        this.fileName = Objects.requireNonNull(fileName, "Имя файла не может быть null");
        this.versions = new ArrayList<>();
    }

    public void addVersion(String content, String comment) {
        int newVersion = versions.size() + 1;
        FileVersion version = new FileVersion(content, newVersion, comment);
        versions.add(version);
    }

    public FileVersion getVersion(int versionNumber) {
        if (versionNumber < 1 || versionNumber > versions.size()) {
            throw new IllegalArgumentException("Неверный номер версии: " + versionNumber);
        }
        return versions.get(versionNumber - 1);
    }

    public FileVersion getLatestVersion() {
        if (versions.isEmpty()) {
            throw new IllegalStateException("Нет доступных версий для файла: " + fileName);
        }
        return versions.get(versions.size() - 1);
    }

    public FileVersion getPreviousVersion() {
        if (versions.size() < 2) {
            throw new IllegalStateException("Нет предыдущей версии для файла: " + fileName);
        }
        return versions.get(versions.size() - 2);
    }

    public List<FileVersion> getAllVersions() {
        return new ArrayList<>(versions);
    }

    public FileDiff compareVersions(int version1, int version2) {
        // Проверяем корректность номеров версий
        if (version1 < 1 || version1 > versions.size()) {
            throw new IllegalArgumentException("Неверный номер первой версии: " + version1);
        }
        if (version2 < 1 || version2 > versions.size()) {
            throw new IllegalArgumentException("Неверный номер второй версии: " + version2);
        }

        FileVersion v1 = getVersion(version1);
        FileVersion v2 = getVersion(version2);
        return v1.compareWith(v2);
    }

    public FileDiff compareWithPrevious() {
        if (versions.size() < 2) {
            throw new IllegalStateException("Нет предыдущей версии для сравнения");
        }
        // Сравниваем последнюю версию с предыдущей
        return compareVersions(versions.size(), versions.size() - 1);
    }

    public String getFileName() { return fileName; }
    public int getVersionCount() { return versions.size(); }

    @Override
    public String toString() {
        return String.format("Файл: %s (%d версий)", fileName, versions.size());
    }
}