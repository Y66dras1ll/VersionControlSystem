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
    void testLineDiffUnchanged() {
        LineDiff lineDiff = new LineDiff(1, "та же строка", "та же строка", LineDiff.ChangeType.UNCHANGED);

        assertEquals(LineDiff.ChangeType.UNCHANGED, lineDiff.getChangeType());
        assertEquals(0, lineDiff.getWordDiffs().size());
    }

    @Test
    void testLineDiffAdded() {
        LineDiff lineDiff = new LineDiff(2, null, "добавленная строка", LineDiff.ChangeType.ADDED);

        assertEquals(LineDiff.ChangeType.ADDED, lineDiff.getChangeType());
        assertNull(lineDiff.getOldLine());
        assertEquals("добавленная строка", lineDiff.getNewLine());
        assertEquals(0, lineDiff.getWordDiffs().size());
    }

    @Test
    void testLineDiffRemoved() {
        LineDiff lineDiff = new LineDiff(3, "удаленная строка", null, LineDiff.ChangeType.REMOVED);

        assertEquals(LineDiff.ChangeType.REMOVED, lineDiff.getChangeType());
        assertEquals("удаленная строка", lineDiff.getOldLine());
        assertNull(lineDiff.getNewLine());
        assertEquals(0, lineDiff.getWordDiffs().size());
    }

    @Test
    void testWordDiffsCalculation() {
        LineDiff lineDiff = new LineDiff(1, "слово1 слово2", "слово1 измененное", LineDiff.ChangeType.MODIFIED);

        List<WordDiff> wordDiffs = lineDiff.getWordDiffs();
        assertEquals(2, wordDiffs.size());

        assertEquals(WordDiff.ChangeType.UNCHANGED, wordDiffs.get(0).getChangeType());
        assertEquals("слово1", wordDiffs.get(0).getOldWord());

        assertEquals(WordDiff.ChangeType.MODIFIED, wordDiffs.get(1).getChangeType());
        assertEquals("слово2", wordDiffs.get(1).getOldWord());
        assertEquals("измененное", wordDiffs.get(1).getNewWord());
    }

    @Test
    void testWordDiffsWithAddedWords() {
        LineDiff lineDiff = new LineDiff(1, "короткая", "короткая и длинная", LineDiff.ChangeType.MODIFIED);

        List<WordDiff> wordDiffs = lineDiff.getWordDiffs();
        assertEquals(3, wordDiffs.size());

        assertEquals(WordDiff.ChangeType.UNCHANGED, wordDiffs.get(0).getChangeType());
        assertEquals(WordDiff.ChangeType.ADDED, wordDiffs.get(1).getChangeType());
        assertEquals(WordDiff.ChangeType.ADDED, wordDiffs.get(2).getChangeType());
    }

    @Test
    void testWordDiffsWithRemovedWords() {
        LineDiff lineDiff = new LineDiff(1, "много слов здесь", "много", LineDiff.ChangeType.MODIFIED);

        List<WordDiff> wordDiffs = lineDiff.getWordDiffs();
        assertEquals(3, wordDiffs.size());

        assertEquals(WordDiff.ChangeType.UNCHANGED, wordDiffs.get(0).getChangeType());
        assertEquals(WordDiff.ChangeType.REMOVED, wordDiffs.get(1).getChangeType());
        assertEquals(WordDiff.ChangeType.REMOVED, wordDiffs.get(2).getChangeType());
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
    void testLineDiffToStringUnchanged() {
        LineDiff lineDiff = new LineDiff(1, "неизмененная", "неизмененная", LineDiff.ChangeType.UNCHANGED);

        String result = lineDiff.toString();
        assertTrue(result.isEmpty());
    }
}