package com.versioncontrol.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FileDiffTest {

    @Test
    void testFileDiffCreation() {
        FileDiff fileDiff = new FileDiff();
        assertNotNull(fileDiff.getLineDiffs());
        assertEquals(0, fileDiff.getLineDiffs().size());
    }

    @Test
    void testFileDiffCompareIdentical() {
        String content = "строка один\nстрока два\nстрока три";
        FileDiff diff = FileDiff.compare(content, content);

        // Должны быть только UNCHANGED строки, но они не выводятся в toString
        assertEquals(3, diff.getLineDiffs().size());

        for (LineDiff lineDiff : diff.getLineDiffs()) {
            assertEquals(LineDiff.ChangeType.UNCHANGED, lineDiff.getChangeType());
        }

        // При выводе UNCHANGED строки не показываются
        String result = diff.toString();
        assertTrue(result.contains("ДЕТАЛЬНЫЕ РАЗЛИЧИЯ"));
    }

    @Test
    void testFileDiffCompareDifferent() {
        String oldContent = "строка один\nстрока два\nстрока три";
        String newContent = "строка один\nизмененная\nстрока три";

        FileDiff diff = FileDiff.compare(oldContent, newContent);

        assertEquals(3, diff.getLineDiffs().size());
        assertEquals(LineDiff.ChangeType.UNCHANGED, diff.getLineDiffs().get(0).getChangeType());
        assertEquals(LineDiff.ChangeType.MODIFIED, diff.getLineDiffs().get(1).getChangeType());
        assertEquals(LineDiff.ChangeType.UNCHANGED, diff.getLineDiffs().get(2).getChangeType());
    }

    @Test
    void testFileDiffCompareAddedLines() {
        String oldContent = "строка один\nстрока два";
        String newContent = "строка один\nновая строка\nстрока два\nеще одна";

        FileDiff diff = FileDiff.compare(oldContent, newContent);

        assertEquals(4, diff.getLineDiffs().size());
        assertEquals(LineDiff.ChangeType.UNCHANGED, diff.getLineDiffs().get(0).getChangeType());
        assertEquals(LineDiff.ChangeType.ADDED, diff.getLineDiffs().get(1).getChangeType());
        assertEquals(LineDiff.ChangeType.UNCHANGED, diff.getLineDiffs().get(2).getChangeType());
        assertEquals(LineDiff.ChangeType.ADDED, diff.getLineDiffs().get(3).getChangeType());
    }

    @Test
    void testFileDiffCompareRemovedLines() {
        String oldContent = "строка один\nудаляемая\nстрока два\nтоже удалить";
        String newContent = "строка один\nстрока два";

        FileDiff diff = FileDiff.compare(oldContent, newContent);

        assertEquals(4, diff.getLineDiffs().size());
        assertEquals(LineDiff.ChangeType.UNCHANGED, diff.getLineDiffs().get(0).getChangeType());
        assertEquals(LineDiff.ChangeType.REMOVED, diff.getLineDiffs().get(1).getChangeType());
        assertEquals(LineDiff.ChangeType.UNCHANGED, diff.getLineDiffs().get(2).getChangeType());
        assertEquals(LineDiff.ChangeType.REMOVED, diff.getLineDiffs().get(3).getChangeType());
    }

    @Test
    void testFileDiffToString() {
        String oldContent = "старая\nудалить";
        String newContent = "новая\nдобавить";

        FileDiff diff = FileDiff.compare(oldContent, newContent);
        String result = diff.toString();

        assertTrue(result.contains("ДЕТАЛЬНЫЕ РАЗЛИЧИЯ"));
        assertTrue(result.contains("СТРОКА 1: Изменена строка"));
        assertTrue(result.contains("СТРОКА 2: Удалена строка"));
        assertTrue(result.contains("СТРОКА 2: Добавлена строка"));
    }
}