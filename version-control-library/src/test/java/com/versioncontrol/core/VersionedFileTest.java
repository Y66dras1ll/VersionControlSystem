package com.versioncontrol.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VersionedFileTest {

    @Test
    void testVersionedFileCreation() {
        VersionedFile file = new VersionedFile("test.txt");

        assertEquals("test.txt", file.getFileName());
        assertEquals(0, file.getVersionCount());
    }

    @Test
    void testAddAndGetVersions() {
        VersionedFile file = new VersionedFile("test.txt");

        file.addVersion("версия1", "первая версия");
        file.addVersion("версия2", "вторая версия");

        assertEquals(2, file.getVersionCount());

        FileVersion v1 = file.getVersion(1);
        FileVersion v2 = file.getVersion(2);
        FileVersion latest = file.getLatestVersion();

        assertEquals("версия1", v1.getContent());
        assertEquals("версия2", v2.getContent());
        assertEquals("версия2", latest.getContent());
        assertEquals(2, latest.getVersionNumber());
    }

    @Test
    void testGetAllVersions() {
        VersionedFile file = new VersionedFile("test.txt");

        file.addVersion("версия1", "первая");
        file.addVersion("версия2", "вторая");
        file.addVersion("версия3", "третья");

        var versions = file.getAllVersions();
        assertEquals(3, versions.size());
        assertEquals("версия1", versions.get(0).getContent());
        assertEquals("версия2", versions.get(1).getContent());
        assertEquals("версия3", versions.get(2).getContent());
    }

    @Test
    void testCompareVersions() {
        VersionedFile file = new VersionedFile("test.txt");

        file.addVersion("первая строка", "версия 1");
        file.addVersion("вторая строка", "версия 2");

        FileDiff diff = file.compareVersions(1, 2);
        assertNotNull(diff);
        assertEquals(1, diff.getLineDiffs().size());

        LineDiff lineDiff = diff.getLineDiffs().get(0);
        assertEquals(LineDiff.ChangeType.MODIFIED, lineDiff.getChangeType());
        assertEquals("первая строка", lineDiff.getOldLine());
        assertEquals("вторая строка", lineDiff.getNewLine());
    }

    @Test
    void testCompareVersionsCorrectOrder() {
        VersionedFile file = new VersionedFile("test.txt");

        file.addVersion("старое содержимое", "версия 1");
        file.addVersion("новое содержимое", "версия 2");

        // Сравниваем версию 1 с версией 2
        FileDiff diff = file.compareVersions(1, 2);
        assertNotNull(diff);
        assertEquals(1, diff.getLineDiffs().size());

        LineDiff lineDiff = diff.getLineDiffs().get(0);
        assertEquals("старое содержимое", lineDiff.getOldLine());
        assertEquals("новое содержимое", lineDiff.getNewLine());
    }

    @Test
    void testCompareWithPrevious() {
        VersionedFile file = new VersionedFile("test.txt");

        file.addVersion("старое содержимое", "версия 1");
        file.addVersion("новое содержимое", "версия 2");

        FileDiff diff = file.compareWithPrevious();
        assertNotNull(diff);
        assertEquals(1, diff.getLineDiffs().size());

        // Должен сравнить версию 1 с версией 2
        LineDiff lineDiff = diff.getLineDiffs().get(0);
        assertEquals("старое содержимое", lineDiff.getOldLine());
        assertEquals("новое содержимое", lineDiff.getNewLine());
    }

    @Test
    void testCompareIdenticalVersions() {
        VersionedFile file = new VersionedFile("test.txt");

        file.addVersion("одинаковое содержимое", "версия 1");
        file.addVersion("одинаковое содержимое", "версия 2");

        FileDiff diff = file.compareVersions(1, 2);
        assertNotNull(diff);

        // Должна быть одна строка UNCHANGED
        assertEquals(1, diff.getLineDiffs().size());
        assertEquals(LineDiff.ChangeType.UNCHANGED, diff.getLineDiffs().get(0).getChangeType());
    }

    @Test
    void testCompareWithPreviousNoPreviousVersion() {
        VersionedFile file = new VersionedFile("test.txt");
        file.addVersion("содержимое", "версия 1");

        assertThrows(IllegalStateException.class, () -> file.compareWithPrevious());
    }

    @Test
    void testGetLatestVersionWithNoVersions() {
        VersionedFile file = new VersionedFile("test.txt");

        assertThrows(IllegalStateException.class, () -> file.getLatestVersion());
    }

    @Test
    void testInvalidVersionNumber() {
        VersionedFile file = new VersionedFile("test.txt");
        file.addVersion("содержимое", "версия 1");

        assertThrows(IllegalArgumentException.class, () -> file.getVersion(0));
        assertThrows(IllegalArgumentException.class, () -> file.getVersion(2));
        assertThrows(IllegalArgumentException.class, () -> file.getVersion(-1));

        // Корректный случай
        assertDoesNotThrow(() -> file.getVersion(1));
    }

    @Test
    void testCompareInvalidVersionNumbers() {
        VersionedFile file = new VersionedFile("test.txt");
        file.addVersion("версия1", "версия 1");
        file.addVersion("версия2", "версия 2");

        assertThrows(IllegalArgumentException.class, () -> file.compareVersions(0, 1));
        assertThrows(IllegalArgumentException.class, () -> file.compareVersions(1, 0));
        assertThrows(IllegalArgumentException.class, () -> file.compareVersions(3, 1));
        assertThrows(IllegalArgumentException.class, () -> file.compareVersions(1, 3));
    }

    @Test
    void testToString() {
        VersionedFile file = new VersionedFile("document.txt");
        file.addVersion("содержимое", "версия 1");

        String result = file.toString();
        assertTrue(result.contains("Файл: document.txt"));
        assertTrue(result.contains("1 версий"));
    }
}