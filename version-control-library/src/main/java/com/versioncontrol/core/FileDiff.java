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

        // Используем более точный алгоритм сравнения
        int i = 0, j = 0;
        int lineNumber = 1;

        while (i < oldLines.length || j < newLines.length) {
            String oldLine = i < oldLines.length ? oldLines[i] : null;
            String newLine = j < newLines.length ? newLines[j] : null;

            if (oldLine != null && newLine != null && oldLine.equals(newLine)) {
                // Неизмененная строка - пропускаем
                diff.addLineDiff(new LineDiff(lineNumber, oldLine, newLine, LineDiff.ChangeType.UNCHANGED));
                i++;
                j++;
                lineNumber++;
            } else if (oldLine != null && newLine != null && !oldLine.equals(newLine)) {
                // Измененная строка
                diff.addLineDiff(new LineDiff(lineNumber, oldLine, newLine, LineDiff.ChangeType.MODIFIED));
                i++;
                j++;
                lineNumber++;
            } else if (oldLine != null && newLine == null) {
                // Удаленная строка
                diff.addLineDiff(new LineDiff(lineNumber, oldLine, null, LineDiff.ChangeType.REMOVED));
                i++;
                lineNumber++;
            } else if (oldLine == null && newLine != null) {
                // Добавленная строка
                diff.addLineDiff(new LineDiff(lineNumber, null, newLine, LineDiff.ChangeType.ADDED));
                j++;
                lineNumber++;
            }
        }

        return diff;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ДЕТАЛЬНЫЕ РАЗЛИЧИЯ\n\n");

        boolean hasChanges = false;
        for (LineDiff lineDiff : lineDiffs) {
            if (lineDiff.getChangeType() != LineDiff.ChangeType.UNCHANGED) {
                sb.append(lineDiff.toString()).append("\n");
                hasChanges = true;
            }
        }

        if (!hasChanges) {
            sb.append("Нет различий между версиями.\n");
        }

        return sb.toString();
    }
}