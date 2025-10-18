package com.versioncontrol.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Repository {
    private final String name;
    private final Path repositoryPath;
    private final Path versionsPath;
    private final Map<String, VersionedFile> files;

    public Repository(String name, String repositoryPath) {
        this.name = Objects.requireNonNull(name, "Имя репозитория не может быть null");
        this.repositoryPath = Paths.get(Objects.requireNonNull(repositoryPath, "Путь репозитория не может быть null"));
        this.versionsPath = this.repositoryPath.resolve(".versions");
        this.files = new HashMap<>();
        createRepositoryDirectory();
    }

    private void createRepositoryDirectory() {
        try {
            Files.createDirectories(repositoryPath);
            Files.createDirectories(versionsPath);
            System.out.println("Репозиторий создан по пути: " + repositoryPath.toAbsolutePath());
            System.out.println("Папка для версий создана: " + versionsPath.toAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException("Не удалось создать директорию репозитория", e);
        }
    }

    private String ensureTxtExtension(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя файла не может быть пустым");
        }

        String normalizedName = fileName.trim();
        if (!normalizedName.toLowerCase().endsWith(".txt")) {
            normalizedName += ".txt";
        }
        return normalizedName;
    }

    private void saveVersionToArchive(String fileName, int versionNumber, String content, String comment) {
        try {
            Path fileVersionsDir = versionsPath.resolve(fileName.replace(".txt", ""));
            Files.createDirectories(fileVersionsDir);

            Path versionFile = fileVersionsDir.resolve("v" + versionNumber + ".txt");
            Files.writeString(versionFile, content);

            // Сохраняем метаданные версии
            Path metaFile = fileVersionsDir.resolve("v" + versionNumber + ".meta");
            String metadata = String.format("Версия: %d\nКомментарий: %s\nВремя: %s",
                    versionNumber, comment, java.time.LocalDateTime.now());
            Files.writeString(metaFile, metadata);

        } catch (IOException e) {
            System.err.println("Не удалось сохранить версию в архив: " + e.getMessage());
        }
    }

    public void addFile(String fileName, String content, String comment) {
        Objects.requireNonNull(fileName, "Имя файла не может быть null");
        Objects.requireNonNull(content, "Содержимое не может быть null");
        Objects.requireNonNull(comment, "Комментарий не может быть null");

        String normalizedFileName = ensureTxtExtension(fileName);

        VersionedFile versionedFile = files.computeIfAbsent(normalizedFileName, VersionedFile::new);
        int newVersion = versionedFile.getVersionCount() + 1;
        versionedFile.addVersion(content, comment);

        // Сохраняем текущую версию в основную папку
        saveFileToDisk(normalizedFileName, content);
        // Сохраняем версию в архив
        saveVersionToArchive(normalizedFileName, newVersion, content, comment);
    }

    public void updateFile(String fileName, String newContent, String comment) {
        Objects.requireNonNull(fileName, "Имя файла не может быть null");
        Objects.requireNonNull(newContent, "Содержимое не может быть null");
        Objects.requireNonNull(comment, "Комментарий не может быть null");

        String normalizedFileName = ensureTxtExtension(fileName);

        if (!files.containsKey(normalizedFileName)) {
            throw new IllegalArgumentException("Файл не найден: " + normalizedFileName);
        }

        addFile(fileName, newContent, comment);
    }

    private void saveFileToDisk(String fileName, String content) {
        try {
            Path filePath = repositoryPath.resolve(fileName);
            Files.writeString(filePath, content);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось сохранить файл на диск: " + fileName, e);
        }
    }

    public String readFile(String fileName) {
        Objects.requireNonNull(fileName, "Имя файла не может быть null");

        String normalizedFileName = ensureTxtExtension(fileName);
        VersionedFile versionedFile = files.get(normalizedFileName);

        if (versionedFile == null) {
            throw new IllegalArgumentException("Файл не найден: " + normalizedFileName);
        }

        return versionedFile.getLatestVersion().getContent();
    }

    public FileVersion getFileVersion(String fileName, int versionNumber) {
        Objects.requireNonNull(fileName, "Имя файла не может быть null");

        String normalizedFileName = ensureTxtExtension(fileName);
        VersionedFile versionedFile = files.get(normalizedFileName);

        if (versionedFile == null) {
            throw new IllegalArgumentException("Файл не найден: " + normalizedFileName);
        }

        return versionedFile.getVersion(versionNumber);
    }

    public FileDiff compareFileVersions(String fileName, int version1, int version2) {
        Objects.requireNonNull(fileName, "Имя файла не может быть null");

        String normalizedFileName = ensureTxtExtension(fileName);
        VersionedFile versionedFile = files.get(normalizedFileName);

        if (versionedFile == null) {
            throw new IllegalArgumentException("Файл не найден: " + normalizedFileName);
        }

        return versionedFile.compareVersions(version1, version2);
    }

    public FileDiff compareWithPreviousVersion(String fileName) {
        Objects.requireNonNull(fileName, "Имя файла не может быть null");

        String normalizedFileName = ensureTxtExtension(fileName);
        VersionedFile versionedFile = files.get(normalizedFileName);

        if (versionedFile == null) {
            throw new IllegalArgumentException("Файл не найден: " + normalizedFileName);
        }

        return versionedFile.compareWithPrevious();
    }

    public void scanExistingTextFiles() {
        try {
            if (!Files.exists(repositoryPath)) {
                return;
            }

            Files.list(repositoryPath)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".txt"))
                    .forEach(filePath -> {
                        try {
                            String fileName = filePath.getFileName().toString();
                            String content = Files.readString(filePath);

                            VersionedFile versionedFile = files.computeIfAbsent(fileName, VersionedFile::new);

                            // Если файл существует на диске но не в памяти, добавляем его как версию 1
                            if (versionedFile.getVersionCount() == 0) {
                                versionedFile.addVersion(content, "Загружен из существующего файла");
                                saveVersionToArchive(fileName, 1, content, "Загружен из существующего файла");
                            }
                        } catch (Exception e) {
                            System.err.println("Не удалось загрузить файл: " + filePath + " - " + e.getMessage());
                        }
                    });
        } catch (Exception e) {
            System.err.println("Не удалось просканировать репозиторий: " + e.getMessage());
        }
    }

    // Getters
    public String getName() { return name; }
    public Path getRepositoryPath() { return repositoryPath; }
    public Path getVersionsPath() { return versionsPath; }
    public Map<String, VersionedFile> getFiles() { return new HashMap<>(files); }

    @Override
    public String toString() {
        return String.format("Репозиторий: %s [%s] (%d файлов)", name, repositoryPath, files.size());
    }
}