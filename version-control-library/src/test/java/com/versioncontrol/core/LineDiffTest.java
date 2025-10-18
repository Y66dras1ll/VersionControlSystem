package com.versioncontrol.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class LineDiffTest {

    @Test
    void testLineDiffCreation() {
        LineDiff lineDiff = new LineDiff(1, "старая строка", "новая строка", LineDiff.ChangeType.MODIFIED);

        assertEquals(1, lineDiff.getLineNumber());
        assertEquals("старая строка", lineDiff.getOldLine());
        assertEquals("новая строка", lineDiff.getNewLine());
        assertEquals(LineDiff.ChangeType.MODIFIED, lineDiff.getChangeType());
    }

    @Test
    void testWordDiffsCalculation() {
        LineDiff lineDiff = new LineDiff(1, "слово1 слово2", "слово1 измененное", LineDiff.ChangeType.MODIFIED);

        List<WordDiff> wordDiffs = lineDiff.getWordDiffs();
        assertEquals(2, wordDiffs.size());

        assertEquals(WordDiff.ChangeType.UNCHANGED, wordDiffs.get(0).getChangeType());
        assertEquals(WordDiff.ChangeType.MODIFIED, wordDiffs.get(1).getChangeType());
    }

    @Test
    void testLineDiffToStringModified() {
        LineDiff lineDiff = new LineDiff(1, "старая строка", "новая строка", LineDiff.ChangeType.MODIFIED);

        String result = lineDiff.toString();
        assertTrue(result.contains("СТРОКА 1: Изменена строка"));
        assertTrue(result.contains("Изменено: \"старая строка\" -> \"новая строка\""));
        assertTrue(result.contains("Различия в словах:"));
    }

    @Test
    void testLineDiffToStringAdded() {
        LineDiff lineDiff = new LineDiff(2, null, "добавленная строка", LineDiff.ChangeType.ADDED);

        String result = lineDiff.toString();
        assertTrue(result.contains("СТРОКА 2: Добавлена строка"));
        assertTrue(result.contains("Добавлено: \"добавленная строка\""));
    }

    @Test
    void testLineDiffToStringRemoved() {
        LineDiff lineDiff = new LineDiff(3, "удаленная строка", null, LineDiff.ChangeType.REMOVED);

        String result = lineDiff.toString();
        assertTrue(result.contains("СТРОКА 3: Удалена строка"));
        assertTrue(result.contains("Удалено: \"удаленная строка\""));
    }

    @Test
    void testLineDiffComplexWordChanges() {
        LineDiff lineDiff = new LineDiff(1, "один два три", "один четыре", LineDiff.ChangeType.MODIFIED);

        List<WordDiff> wordDiffs = lineDiff.getWordDiffs();
        assertEquals(3, wordDiffs.size());

        assertEquals(WordDiff.ChangeType.UNCHANGED, wordDiffs.get(0).getChangeType());
        assertEquals(WordDiff.ChangeType.MODIFIED, wordDiffs.get(1).getChangeType());
        assertEquals(WordDiff.ChangeType.REMOVED, wordDiffs.get(2).getChangeType());
    }
}