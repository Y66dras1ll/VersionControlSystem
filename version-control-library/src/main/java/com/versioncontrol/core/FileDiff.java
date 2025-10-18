package com.versioncontrol.core;

import java.util.ArrayList;
import java.util.List;

public class FileDiff {
    private final List<LineDiff> lineDiffs;

    public FileDiff() {
        this.lineDiffs = new ArrayList<>();
    }

    public void addLineDiff(LineDiff lineDiff) {
        lineDiffs.add(lineDiff);
    }

    public List<LineDiff> getLineDiffs() {
        return new ArrayList<>(lineDiffs);
    }

    public static FileDiff compare(String oldContent, String newContent) {
        FileDiff diff = new FileDiff();

        String[] oldLines = oldContent.split("\n", -1);
        String[] newLines = newContent.split("\n", -1);

        int maxLines = Math.max(oldLines.length, newLines.length);

        for (int i = 0; i < maxLines; i++) {
            String oldLine = i < oldLines.length ? oldLines[i] : null;
            String newLine = i < newLines.length ? newLines[i] : null;
            int lineNumber = i + 1;

            if (oldLine == null && newLine != null) {
                // Добавленная строка
                diff.addLineDiff(new LineDiff(lineNumber, null, newLine, LineDiff.ChangeType.ADDED));
            } else if (oldLine != null && newLine == null) {
                // Удаленная строка
                diff.addLineDiff(new LineDiff(lineNumber, oldLine, null, LineDiff.ChangeType.REMOVED));
            } else if (oldLine != null && newLine != null) {
                if (oldLine.equals(newLine)) {
                    // Неизмененная строка
                    diff.addLineDiff(new LineDiff(lineNumber, oldLine, newLine, LineDiff.ChangeType.UNCHANGED));
                } else {
                    // Измененная строка
                    diff.addLineDiff(new LineDiff(lineNumber, oldLine, newLine, LineDiff.ChangeType.MODIFIED));
                }
            }
        }

        return diff;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ДЕТАЛЬНЫЕ РАЗЛИЧИЯ\n\n");

        for (LineDiff lineDiff : lineDiffs) {
            if (lineDiff.getChangeType() != LineDiff.ChangeType.UNCHANGED) {
                sb.append(lineDiff.toString()).append("\n");
            }
        }

        return sb.toString();
    }
}