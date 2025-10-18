package com.versioncontrol.core;

public class WordDiff {
    private final String oldWord;
    private final String newWord;
    private final int position;
    private final ChangeType changeType;

    public enum ChangeType {
        UNCHANGED, MODIFIED, ADDED, REMOVED
    }

    public WordDiff(String oldWord, String newWord, int position, ChangeType changeType) {
        this.oldWord = oldWord;
        this.newWord = newWord;
        this.position = position;
        this.changeType = changeType;
    }

    public String getOldWord() { return oldWord; }
    public String getNewWord() { return newWord; }
    public int getPosition() { return position; }
    public ChangeType getChangeType() { return changeType; }

    @Override
    public String toString() {
        switch (changeType) {
            case MODIFIED:
                return String.format("      Слово %d: \"%s\" -> \"%s\"", position,
                        oldWord != null ? oldWord : "",
                        newWord != null ? newWord : "");
            case ADDED:
                return String.format("      Слово %d: добавлено \"%s\"", position,
                        newWord != null ? newWord : "");
            case REMOVED:
                return String.format("      Слово %d: удалено \"%s\"", position,
                        oldWord != null ? oldWord : "");
            default:
                return String.format("      Слово %d: \"%s\"", position,
                        oldWord != null ? oldWord : "");
        }
    }
}