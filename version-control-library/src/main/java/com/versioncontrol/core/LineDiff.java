package com.versioncontrol.core;

import java.util.ArrayList;
import java.util.List;

public class LineDiff {
    private final int lineNumber;
    private final String oldLine;
    private final String newLine;
    private final ChangeType changeType;
    private final List<WordDiff> wordDiffs;

    public enum ChangeType {
        UNCHANGED, MODIFIED, ADDED, REMOVED
    }

    public LineDiff(int lineNumber, String oldLine, String newLine, ChangeType changeType) {
        this.lineNumber = lineNumber;
        this.oldLine = oldLine;
        this.newLine = newLine;
        this.changeType = changeType;
        this.wordDiffs = new ArrayList<>();

        if (changeType == ChangeType.MODIFIED) {
            calculateWordDiffs();
        }
    }

    private void calculateWordDiffs() {
        String[] oldWords = oldLine.split("\\s+");
        String[] newWords = newLine.split("\\s+");

        int maxWords = Math.max(oldWords.length, newWords.length);

        for (int i = 0; i < maxWords; i++) {
            String oldWord = i < oldWords.length ? oldWords[i] : null;
            String newWord = i < newWords.length ? newWords[i] : null;

            if (oldWord == null && newWord != null) {
                wordDiffs.add(new WordDiff(null, newWord, i + 1, WordDiff.ChangeType.ADDED));
            } else if (oldWord != null && newWord == null) {
                wordDiffs.add(new WordDiff(oldWord, null, i + 1, WordDiff.ChangeType.REMOVED));
            } else if (oldWord != null && newWord != null) {
                if (oldWord.equals(newWord)) {
                    wordDiffs.add(new WordDiff(oldWord, newWord, i + 1, WordDiff.ChangeType.UNCHANGED));
                } else {
                    wordDiffs.add(new WordDiff(oldWord, newWord, i + 1, WordDiff.ChangeType.MODIFIED));
                }
            }
        }
    }

    public int getLineNumber() { return lineNumber; }
    public String getOldLine() { return oldLine; }
    public String getNewLine() { return newLine; }
    public ChangeType getChangeType() { return changeType; }
    public List<WordDiff> getWordDiffs() { return new ArrayList<>(wordDiffs); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        switch (changeType) {
            case MODIFIED:
                sb.append(String.format(" СТРОКА %d: Изменена строка\n", lineNumber));
                sb.append(String.format("   Изменено: \"%s\" -> \"%s\"\n", oldLine, newLine));
                if (!wordDiffs.isEmpty()) {
                    sb.append("   Различия в словах:\n");
                    for (WordDiff wordDiff : wordDiffs) {
                        if (wordDiff.getChangeType() != WordDiff.ChangeType.UNCHANGED) {
                            sb.append(wordDiff.toString()).append("\n");
                        }
                    }
                }
                break;
            case ADDED:
                sb.append(String.format(" СТРОКА %d: Добавлена строка\n", lineNumber));
                sb.append(String.format("   Добавлено: \"%s\"\n", newLine));
                break;
            case REMOVED:
                sb.append(String.format(" СТРОКА %d: Удалена строка\n", lineNumber));
                sb.append(String.format("   Удалено: \"%s\"\n", oldLine));
                break;
            default:
                break;
        }

        return sb.toString();
    }
}