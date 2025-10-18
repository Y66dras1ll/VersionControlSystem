package com.versioncontrol.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FileVersionTest {

    @Test
    void testFileVersionCreation() {
        FileVersion version = new FileVersion("содержимое", 1, "комментарий");

        assertEquals("содержимое", version.getContent());
        assertEquals(1, version.getVersionNumber());
        assertEquals("комментарий", version.getComment());
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
    void testFileVersionCompareWith() {
        FileVersion v1 = new FileVersion("строка один", 1, "первая версия");
        FileVersion v2 = new FileVersion("строка два", 2, "вторая версия");

        FileDiff diff = v1.compareWith(v2);
        assertNotNull(diff);
        assertEquals(1, diff.getLineDiffs().size());
        assertEquals(LineDiff.ChangeType.MODIFIED, diff.getLineDiffs().get(0).getChangeType());
    }

    @Test
    void testFileVersionToString() {
        FileVersion version = new FileVersion("содержимое", 1, "тестовый комментарий");
        String result = version.toString();

        assertTrue(result.contains("Версия 1"));
        assertTrue(result.contains("тестовый комментарий"));
    }
}