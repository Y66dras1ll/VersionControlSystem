package com.versioncontrol.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WordDiffTest {

    @Test
    void testWordDiffCreation() {
        WordDiff wordDiff = new WordDiff("старое", "новое", 1, WordDiff.ChangeType.MODIFIED);

        assertEquals("старое", wordDiff.getOldWord());
        assertEquals("новое", wordDiff.getNewWord());
        assertEquals(1, wordDiff.getPosition());
        assertEquals(WordDiff.ChangeType.MODIFIED, wordDiff.getChangeType());
    }

    @Test
    void testWordDiffToStringModified() {
        WordDiff wordDiff = new WordDiff("старое", "новое", 1, WordDiff.ChangeType.MODIFIED);
        String expected = "      Слово 1: \"старое\" -> \"новое\"";
        assertEquals(expected, wordDiff.toString());
    }

    @Test
    void testWordDiffToStringAdded() {
        WordDiff wordDiff = new WordDiff(null, "новое", 2, WordDiff.ChangeType.ADDED);
        String expected = "      Слово 2: добавлено \"новое\"";
        assertEquals(expected, wordDiff.toString());
    }

    @Test
    void testWordDiffToStringRemoved() {
        WordDiff wordDiff = new WordDiff("старое", null, 3, WordDiff.ChangeType.REMOVED);
        String expected = "      Слово 3: удалено \"старое\"";
        assertEquals(expected, wordDiff.toString());
    }

    @Test
    void testWordDiffToStringUnchanged() {
        WordDiff wordDiff = new WordDiff("слово", "слово", 1, WordDiff.ChangeType.UNCHANGED);
        String expected = "      Слово 1: \"слово\"";
        assertEquals(expected, wordDiff.toString());
    }
}