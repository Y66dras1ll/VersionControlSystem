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
    void testAddFileWithTxtExtension() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-test");
        Repository repo = new Repository("test-repo", tempDir.toString());

        repo.addFile("document.txt", "содержимое", "версия 1");

        // Файл должен быть доступен с расширением .txt
        assertNotNull(repo.getFiles().get("document.txt"));
        assertEquals("содержимое", repo.readFile("document.txt"));
        assertEquals("содержимое", repo.readFile("document"));
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
    void testUpdateNonExistentFile() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-test");
        Repository repo = new Repository("test-repo", tempDir.toString());

        assertThrows(IllegalArgumentException.class, () ->
                repo.updateFile("nonexistent", "content", "comment"));
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

        LineDiff lineDiff = diff.getLineDiffs().get(0);
        assertEquals(LineDiff.ChangeType.MODIFIED, lineDiff.getChangeType());
        assertEquals("строка один", lineDiff.getOldLine());
        assertEquals("строка два", lineDiff.getNewLine());
    }

    @Test
    void testCompareInvalidFileVersions() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-test");
        Repository repo = new Repository("test-repo", tempDir.toString());

        repo.addFile("file", "content", "version 1");

        // Тестируем неверные номера версий
        assertThrows(IllegalArgumentException.class, () ->
                repo.compareFileVersions("file", 0, 1));
        assertThrows(IllegalArgumentException.class, () ->
                repo.compareFileVersions("file", 1, 3));
        assertThrows(IllegalArgumentException.class, () ->
                repo.compareFileVersions("file", 2, 1));
    }

    @Test
    void testCompareWithPreviousVersion() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-test");
        Repository repo = new Repository("test-repo", tempDir.toString());

        repo.addFile("file", "первая версия", "версия 1");
        repo.updateFile("file", "вторая версия", "версия 2");

        FileDiff diff = repo.compareWithPreviousVersion("file");
        assertNotNull(diff);

        // Должен сравнить версию 1 с версией 2
        LineDiff lineDiff = diff.getLineDiffs().get(0);
        assertEquals("первая версия", lineDiff.getOldLine());
        assertEquals("вторая версия", lineDiff.getNewLine());
    }

    @Test
    void testCompareWithPreviousVersionNoPrevious() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-test");
        Repository repo = new Repository("test-repo", tempDir.toString());

        repo.addFile("file", "content", "version 1");

        assertThrows(IllegalStateException.class, () ->
                repo.compareWithPreviousVersion("file"));
    }

    @Test
    void testFileNotFound() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-test");
        Repository repo = new Repository("test-repo", tempDir.toString());

        assertThrows(IllegalArgumentException.class, () -> repo.readFile("nonexistent"));
        assertThrows(IllegalArgumentException.class, () -> repo.getFileVersion("nonexistent", 1));
        assertThrows(IllegalArgumentException.class, () -> repo.compareFileVersions("nonexistent", 1, 2));
        assertThrows(IllegalArgumentException.class, () -> repo.compareWithPreviousVersion("nonexistent"));
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

    @Test
    void testGetFileVersion() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-test");
        Repository repo = new Repository("test-repo", tempDir.toString());

        repo.addFile("file", "версия 1", "комментарий 1");
        repo.updateFile("file", "версия 2", "комментарий 2");

        FileVersion v1 = repo.getFileVersion("file", 1);
        FileVersion v2 = repo.getFileVersion("file", 2);

        assertEquals("версия 1", v1.getContent());
        assertEquals("версия 2", v2.getContent());
        assertEquals("комментарий 1", v1.getComment());
        assertEquals("комментарий 2", v2.getComment());
    }

    @Test
    void testToString() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-test");
        Repository repo = new Repository("test-repo", tempDir.toString());

        String result = repo.toString();
        assertTrue(result.contains("Репозиторий: test-repo"));
        assertTrue(result.contains("0 файлов"));

        repo.addFile("file", "content", "comment");

        String result2 = repo.toString();
        assertTrue(result2.contains("1 файлов"));
    }
}