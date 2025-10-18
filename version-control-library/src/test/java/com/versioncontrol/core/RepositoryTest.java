package com.versioncontrol.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

class RepositoryTest {

    @Test
    void testRepositoryCreation() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-test");
        Repository repo = new Repository("test-repo", tempDir.toString());

        assertEquals("test-repo", repo.getName());
        assertTrue(repo.getRepositoryPath().toString().contains("vcs-test"));
        assertTrue(Files.exists(repo.getVersionsPath()));
    }

    @Test
    void testAddFile() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-test");
        Repository repo = new Repository("test-repo", tempDir.toString());

        repo.addFile("test", "содержимое файла", "первая версия");

        // Проверяем что файл добавлен в систему версионности
        String content = repo.readFile("test");
        assertEquals("содержимое файла", content);

        // Проверяем что файл создан на диске
        Path filePath = tempDir.resolve("test.txt");
        assertTrue(Files.exists(filePath));

        // Проверяем что версия сохранена в архив
        Path versionDir = repo.getVersionsPath().resolve("test");
        assertTrue(Files.exists(versionDir));
    }

    @Test
    void testUpdateFile() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-test");
        Repository repo = new Repository("test-repo", tempDir.toString());

        repo.addFile("document", "первое содержимое", "версия 1");
        repo.updateFile("document", "обновленное содержимое", "версия 2");

        assertEquals("обновленное содержимое", repo.readFile("document"));
        assertEquals(2, repo.getFiles().get("document.txt").getVersionCount());
    }

    @Test
    void testCompareFileVersions() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-test");
        Repository repo = new Repository("test-repo", tempDir.toString());

        repo.addFile("file", "строка один", "версия 1");
        repo.updateFile("file", "строка два", "версия 2");

        FileDiff diff = repo.compareFileVersions("file", 1, 2);
        assertNotNull(diff);
        assertEquals(1, diff.getLineDiffs().size());
        assertEquals(LineDiff.ChangeType.MODIFIED, diff.getLineDiffs().get(0).getChangeType());
    }

    @Test
    void testCompareWithPreviousVersion() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-test");
        Repository repo = new Repository("test-repo", tempDir.toString());

        repo.addFile("file", "первая версия", "версия 1");
        repo.updateFile("file", "вторая версия", "версия 2");

        FileDiff diff = repo.compareWithPreviousVersion("file");
        assertNotNull(diff);

        // Должен сравнить версию 2 с версией 1
        assertEquals("первая версия", diff.getLineDiffs().get(0).getOldLine());
        assertEquals("вторая версия", diff.getLineDiffs().get(0).getNewLine());
    }

    @Test
    void testFileNotFound() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-test");
        Repository repo = new Repository("test-repo", tempDir.toString());

        assertThrows(IllegalArgumentException.class, () -> repo.readFile("nonexistent"));
        assertThrows(IllegalArgumentException.class, () -> repo.getFileVersion("nonexistent", 1));
        assertThrows(IllegalArgumentException.class, () -> repo.compareFileVersions("nonexistent", 1, 2));
    }

    @Test
    void testTxtExtensionAutoAdd() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-test");
        Repository repo = new Repository("test-repo", tempDir.toString());

        repo.addFile("test", "content", "comment");

        // Файл должен быть доступен как с расширением, так и без
        assertNotNull(repo.getFiles().get("test.txt"));
        assertEquals("content", repo.readFile("test"));
        assertEquals("content", repo.readFile("test.txt"));
    }

    @Test
    void testScanExistingTextFiles() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-test");

        // Создаем .txt файл до создания репозитория
        Path textFile = tempDir.resolve("existing.txt");
        Files.writeString(textFile, "текстовое содержимое");

        // Создаем не-текстовый файл
        Path nonTextFile = tempDir.resolve("data.dat");
        Files.writeString(nonTextFile, "binary data");

        Repository repo = new Repository("test-repo", tempDir.toString());
        repo.scanExistingTextFiles();

        // Должен найти только .txt файл
        assertEquals(1, repo.getFiles().size());
        assertNotNull(repo.getFiles().get("existing.txt"));
        assertNull(repo.getFiles().get("data.dat"));
    }
}