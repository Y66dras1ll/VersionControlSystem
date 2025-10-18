package com.versioncontrol.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VersionedFileTest {

    @Test
    void testCompareWithPrevious() {
        VersionedFile file = new VersionedFile("test.txt");

        file.addVersion("старое содержимое", "версия 1");
        file.addVersion("новое содержимое", "версия 2");

        FileDiff diff = file.compareWithPrevious();
        assertNotNull(diff);
        assertEquals(1, diff.getLineDiffs().size());

        // Проверяем что сравнивается версия 2 с версией 1
        assertEquals("старое содержимое", diff.getLineDiffs().get(0).getOldLine());
        assertEquals("новое содержимое", diff.getLineDiffs().get(0).getNewLine());
    }

    @Test
    void testCompareVersionsWithValidNumbers() {
        VersionedFile file = new VersionedFile("test.txt");

        file.addVersion("версия1", "версия 1");
        file.addVersion("версия2", "версия 2");
        file.addVersion("версия3", "версия 3");

        // Корректные сравнения
        assertDoesNotThrow(() -> file.compareVersions(1, 2));
        assertDoesNotThrow(() -> file.compareVersions(2, 3));
        assertDoesNotThrow(() -> file.compareVersions(1, 3));
    }

    @Test
    void testCompareVersionsWithInvalidNumbers() {
        VersionedFile file = new VersionedFile("test.txt");

        file.addVersion("версия1", "версия 1");
        file.addVersion("версия2", "версия 2");

        // Некорректные сравнения
        assertThrows(IllegalArgumentException.class, () -> file.compareVersions(0, 1));
        assertThrows(IllegalArgumentException.class, () -> file.compareVersions(1, 0));
        assertThrows(IllegalArgumentException.class, () -> file.compareVersions(3, 1));
        assertThrows(IllegalArgumentException.class, () -> file.compareVersions(1, 3));
    }
}