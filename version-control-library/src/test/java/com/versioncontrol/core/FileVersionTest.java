package com.versioncontrol.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FileVersionTest {

    @Test
    void testFileVersionCreation() {
        FileVersion version = new FileVersion("содержимое файла", 1, "комментарий к версии");

        assertEquals("содержимое файла", version.getContent());
        assertEquals(1, version.getVersionNumber());
        assertEquals("комментарий к версии", version.getComment());
        assertNotNull(version.getTimestamp());
    }

    @Test
    void testFileVersionNullChecks() {
        assertThrows(NullPointerException.class,
                () -> new FileVersion(null, 1, "комментарий"));
        assertThrows(NullPointerException.class,
                () -> new FileVersion("содержимое", 1, null));
    }

    @Test
    void testFileVersionToString() {
        FileVersion version = new FileVersion("тестовое содержимое", 1, "тестовый комментарий");
        String result = version.toString();

        assertTrue(result.contains("Версия 1"));
        assertTrue(result.contains("тестовый комментарий"));
        assertTrue(result.matches(".*\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2}:\\d{2}.*"));
    }

    @Test
    void testMultipleFileVersions() {
        FileVersion v1 = new FileVersion("версия 1", 1, "первая");
        FileVersion v2 = new FileVersion("версия 2", 2, "вторая");
        FileVersion v3 = new FileVersion("версия 3", 3, "третья");

        assertEquals(1, v1.getVersionNumber());
        assertEquals(2, v2.getVersionNumber());
        assertEquals(3, v3.getVersionNumber());

        assertEquals("версия 1", v1.getContent());
        assertEquals("версия 2", v2.getContent());
        assertEquals("версия 3", v3.getContent());
    }
}