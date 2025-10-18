package com.versioncontrol.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

class IntegrationTest {

    @Test
    void testCompleteWorkflow() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-integration-test");
        VersionControlManager vcm = new VersionControlManager();

        // 1. Создаем репозиторий
        Repository repo = vcm.createRepository("workflow-test", tempDir.toString());
        assertEquals("workflow-test", repo.getName());

        // 2. Добавляем файл
        repo.addFile("document", "первая строка\nвторая строка", "начальная версия");
        assertEquals("первая строка\nвторая строка", repo.readFile("document"));
        assertEquals(1, repo.getFiles().get("document.txt").getVersionCount());

        // 3. Обновляем файл
        repo.updateFile("document", "первая строка\nизмененная вторая строка\nтретья строка", "добавили третью строку");
        assertEquals("первая строка\nизмененная вторая строка\nтретья строка", repo.readFile("document"));
        assertEquals(2, repo.getFiles().get("document.txt").getVersionCount());

        // 4. Сравниваем версии
        FileDiff diff = repo.compareFileVersions("document", 1, 2);
        assertNotNull(diff);

        // Должны быть изменения во второй строке и добавление третьей
        assertEquals(3, diff.getLineDiffs().size());
        assertEquals(LineDiff.ChangeType.UNCHANGED, diff.getLineDiffs().get(0).getChangeType());
        assertEquals(LineDiff.ChangeType.MODIFIED, diff.getLineDiffs().get(1).getChangeType());
        assertEquals(LineDiff.ChangeType.ADDED, diff.getLineDiffs().get(2).getChangeType());

        // 5. Сравниваем с предыдущей версией
        FileDiff prevDiff = repo.compareWithPreviousVersion("document");
        assertNotNull(prevDiff);

        // 6. Проверяем что файлы созданы на диске
        assertTrue(Files.exists(tempDir.resolve("document.txt")));
        assertTrue(Files.exists(repo.getVersionsPath().resolve("document")));
    }

    @Test
    void testComplexWordComparison() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-complex-test");
        Repository repo = new Repository("complex-test", tempDir.toString());

        String version1 = "21123 123 12312 12312\n213 123 1 23\n23\n123\n123\n132";
        String version2 = "567 345235 12123\n232\n2324";

        repo.addFile("complex", version1, "версия 1");
        repo.updateFile("complex", version2, "версия 2");

        FileDiff diff = repo.compareFileVersions("complex", 1, 2);
        String diffString = diff.toString();

        // Проверяем детальное сравнение слов
        assertTrue(diffString.contains("Различия в словах:"));
        assertTrue(diffString.contains("Слово 1: \"21123\" -> \"567\""));
        assertTrue(diffString.contains("Слово 2: \"123\" -> \"345235\""));
        assertTrue(diffString.contains("Слово 3: \"12312\" -> \"12123\""));
        assertTrue(diffString.contains("удалено \"12312\""));
    }

    @Test
    void testMultipleFilesInRepository() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-multi-test");
        Repository repo = new Repository("multi-test", tempDir.toString());

        // Добавляем несколько файлов
        repo.addFile("file1", "содержимое file1", "версия 1 file1");
        repo.addFile("file2", "содержимое file2", "версия 1 file2");
        repo.addFile("file3", "содержимое file3", "версия 1 file3");

        assertEquals(3, repo.getFiles().size());
        assertTrue(repo.getFiles().containsKey("file1.txt"));
        assertTrue(repo.getFiles().containsKey("file2.txt"));
        assertTrue(repo.getFiles().containsKey("file3.txt"));

        // Обновляем один из файлов
        repo.updateFile("file2", "обновленное содержимое file2", "версия 2 file2");

        assertEquals(2, repo.getFiles().get("file2.txt").getVersionCount());
        assertEquals(1, repo.getFiles().get("file1.txt").getVersionCount());
        assertEquals(1, repo.getFiles().get("file3.txt").getVersionCount());
    }
}