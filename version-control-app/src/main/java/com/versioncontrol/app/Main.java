package com.versioncontrol.app;

import com.versioncontrol.core.VersionControlManager;
import com.versioncontrol.core.Repository;
import com.versioncontrol.core.VersionedFile;
import com.versioncontrol.core.FileVersion;
import com.versioncontrol.core.FileDiff;

import java.util.Scanner;
import java.util.Map;

public class Main {
    private static VersionControlManager vcm;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Система контроля версий текстовых файлов ===");
        System.out.println("Все файлы автоматически сохраняются в формате .txt");
        System.out.println("Все версии сохраняются в папке .versions внутри репозитория");
        vcm = new VersionControlManager();

        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    createRepository();
                    break;
                case "2":
                    listRepositories();
                    break;
                case "3":
                    addFileToRepository();
                    break;
                case "4":
                    editFile();
                    break;
                case "5":
                    viewFileVersions();
                    break;
                case "6":
                    readFile();
                    break;
                case "7":
                    compareFileVersions();
                    break;
                case "8":
                    compareWithPrevious();
                    break;
                case "9":
                    scanRepositoryFiles();
                    break;
                case "10":
                    deleteRepository();
                    break;
                case "0":
                    System.out.println("Завершение.");
                    return;
                default:
                    System.out.println("Неверный выбор. Пожалуйста, попробуйте снова.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- Главное меню ---");
        System.out.println("1. Создать репозиторий");
        System.out.println("2. Показать список репозиториев");
        System.out.println("3. Добавить текстовый файл в репозиторий");
        System.out.println("4. Изменить файл (создать новую версию)");
        System.out.println("5. Просмотреть версии файла");
        System.out.println("6. Прочитать файл");
        System.out.println("7. Сравнить две версии файла");
        System.out.println("8. Сравнить с предыдущей версией");
        System.out.println("9. Сканировать .txt файлы в репозитории");
        System.out.println("10. Удалить репозиторий");
        System.out.println("0. Выход");
        System.out.print("Введите ваш выбор: ");
    }

    private static void createRepository() {
        System.out.print("Введите имя репозитория: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Ошибка: имя репозитория не может быть пустым");
            return;
        }

        System.out.print("Введите путь для создания репозитория: ");
        String path = scanner.nextLine().trim();

        if (path.isEmpty()) {
            System.out.println("Ошибка: путь репозитория не может быть пустым");
            return;
        }

        try {
            Repository repo = vcm.createRepository(name, path);
            System.out.println("✓ Репозиторий создан: " + repo);
            System.out.println("✓ Папка для версий: " + repo.getVersionsPath());
            System.out.println("✓ Автоматически просканированы существующие .txt файлы");
        } catch (Exception e) {
            System.out.println("✗ Ошибка: " + e.getMessage());
        }
    }

    private static void listRepositories() {
        Map<String, Repository> repos = vcm.getRepositories();

        if (repos.isEmpty()) {
            System.out.println("Репозитории не найдены.");
            return;
        }

        System.out.println("\n--- Список репозиториев ---");
        for (Repository repo : repos.values()) {
            System.out.println("📁 " + repo);
            if (repo.getFiles().isEmpty()) {
                System.out.println("   └─ (нет файлов)");
            } else {
                for (VersionedFile file : repo.getFiles().values()) {
                    System.out.println("   📄 " + file);
                }
            }
            System.out.println();
        }
    }

    private static void addFileToRepository() {
        System.out.print("Введите имя репозитория: ");
        String repoName = scanner.nextLine().trim();

        try {
            Repository repo = vcm.getRepository(repoName);

            System.out.print("Введите имя файла (автоматически добавится .txt): ");
            String fileName = scanner.nextLine().trim();

            if (fileName.isEmpty()) {
                System.out.println("Ошибка: имя файла не может быть пустым");
                return;
            }

            System.out.println("Введите содержимое файла (введите 'КОНЕЦ' на отдельной строке для завершения):");
            StringBuilder content = new StringBuilder();
            String line;
            while (!(line = scanner.nextLine()).equals("КОНЕЦ")) {
                content.append(line).append("\n");
            }

            if (content.length() > 0) {
                content.setLength(content.length() - 1);
            }

            System.out.print("Введите комментарий к версии: ");
            String comment = scanner.nextLine().trim();

            if (comment.isEmpty()) {
                comment = "Без комментария";
            }

            repo.addFile(fileName, content.toString(), comment);
            System.out.println("✓ Текстовый файл успешно добавлен.");

        } catch (Exception e) {
            System.out.println("✗ Ошибка: " + e.getMessage());
        }
    }

    private static void editFile() {
        System.out.print("Введите имя репозитория: ");
        String repoName = scanner.nextLine().trim();

        try {
            Repository repo = vcm.getRepository(repoName);

            System.out.print("Введите имя файла для изменения: ");
            String fileName = scanner.nextLine().trim();

            if (fileName.isEmpty()) {
                System.out.println("Ошибка: имя файла не может быть пустым");
                return;
            }


            String normalizedFileName = fileName.endsWith(".txt") ? fileName : fileName + ".txt";
            if (!repo.getFiles().containsKey(normalizedFileName)) {
                System.out.println("✗ Файл не найден: " + fileName);
                return;
            }


            VersionedFile file = repo.getFiles().get(normalizedFileName);
            String currentContent = repo.readFile(fileName);

            System.out.println("\n--- Текущее содержимое файла " + fileName + " ---");
            System.out.println("Всего версий: " + file.getVersionCount());
            System.out.println("Текущая версия: " + file.getLatestVersion().getVersionNumber());
            System.out.println("\nСодержимое:");
            System.out.println(currentContent);
            System.out.println("----------------------------------------");

            System.out.println("\nВведите новое содержимое файла (введите 'КОНЕЦ' на отдельной строке для завершения):");
            StringBuilder newContent = new StringBuilder();
            String line;
            while (!(line = scanner.nextLine()).equals("КОНЕЦ")) {
                newContent.append(line).append("\n");
            }

            if (newContent.length() > 0) {
                newContent.setLength(newContent.length() - 1);
            }

            System.out.print("Введите комментарий к изменению: ");
            String comment = scanner.nextLine().trim();

            if (comment.isEmpty()) {
                comment = "Изменение файла";
            }

            repo.updateFile(fileName, newContent.toString(), comment);
            System.out.println("✓ Файл успешно обновлен. Создана новая версия.");


            System.out.print("Показать различия с предыдущей версией? (да/нет): ");
            String showDiff = scanner.nextLine().trim().toLowerCase();
            if (showDiff.equals("да") || showDiff.equals("д")) {
                try {
                    FileDiff diff = repo.compareWithPreviousVersion(fileName);
                    System.out.println("\n" + diff.toString());
                } catch (Exception e) {
                    System.out.println("Не удалось показать различия: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.out.println("✗ Ошибка: " + e.getMessage());
        }
    }

    private static void viewFileVersions() {
        System.out.print("Введите имя репозитория: ");
        String repoName = scanner.nextLine().trim();

        try {
            Repository repo = vcm.getRepository(repoName);

            System.out.print("Введите имя файла: ");
            String fileName = scanner.nextLine().trim();

            if (fileName.isEmpty()) {
                System.out.println("Ошибка: имя файла не может быть пустым");
                return;
            }

            String normalizedFileName = fileName.endsWith(".txt") ? fileName : fileName + ".txt";
            VersionedFile file = repo.getFiles().get(normalizedFileName);

            if (file == null) {
                System.out.println("✗ Файл не найден: " + fileName);
                return;
            }

            System.out.println("\n--- Версии файла " + file.getFileName() + " ---");
            System.out.println("Всего версий: " + file.getVersionCount());
            System.out.println();

            if (file.getVersionCount() == 0) {
                System.out.println("(нет версий)");
            } else {
                int versionNumber = 1;
                for (FileVersion version : file.getAllVersions()) {
                    System.out.println("🔹 " + version);
                    versionNumber++;
                }
            }

        } catch (Exception e) {
            System.out.println("✗ Ошибка: " + e.getMessage());
        }
    }

    private static void readFile() {
        System.out.print("Введите имя репозитория: ");
        String repoName = scanner.nextLine().trim();

        try {
            Repository repo = vcm.getRepository(repoName);

            System.out.print("Введите имя файла: ");
            String fileName = scanner.nextLine().trim();

            if (fileName.isEmpty()) {
                System.out.println("Ошибка: имя файла не может быть пустым");
                return;
            }

            String normalizedFileName = fileName.endsWith(".txt") ? fileName : fileName + ".txt";
            if (!repo.getFiles().containsKey(normalizedFileName)) {
                System.out.println("✗ Файл не найден: " + fileName);
                return;
            }

            VersionedFile file = repo.getFiles().get(normalizedFileName);
            String content = repo.readFile(fileName);

            System.out.println("\n--- Содержимое файла " + fileName + " ---");
            System.out.println("Текущая версия: " + file.getLatestVersion().getVersionNumber());
            System.out.println("Комментарий: " + file.getLatestVersion().getComment());
            System.out.println("----------------------------------------");
            System.out.println(content);

        } catch (Exception e) {
            System.out.println("✗ Ошибка: " + e.getMessage());
        }
    }

    private static void compareFileVersions() {
        System.out.print("Введите имя репозитория: ");
        String repoName = scanner.nextLine().trim();

        try {
            Repository repo = vcm.getRepository(repoName);

            System.out.print("Введите имя файла: ");
            String fileName = scanner.nextLine().trim();

            if (fileName.isEmpty()) {
                System.out.println("Ошибка: имя файла не может быть пустым");
                return;
            }


            String normalizedFileName = fileName.endsWith(".txt") ? fileName : fileName + ".txt";
            if (!repo.getFiles().containsKey(normalizedFileName)) {
                System.out.println("✗ Файл не найден: " + fileName);
                return;
            }

            VersionedFile file = repo.getFiles().get(normalizedFileName);
            int versionCount = file.getVersionCount();

            if (versionCount < 2) {
                System.out.println("✗ Для сравнения нужно как минимум 2 версии файла. Сейчас версий: " + versionCount);
                return;
            }

            System.out.println("Доступные версии: от 1 до " + versionCount);


            System.out.print("Введите номер СТАРОЙ версии: ");
            int oldVersion;
            try {
                oldVersion = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("✗ Ошибка: введите корректный номер версии (целое число)");
                return;
            }


            System.out.print("Введите номер НОВОЙ версии: ");
            int newVersion;
            try {
                newVersion = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("✗ Ошибка: введите корректный номер версии (целое число)");
                return;
            }


            if (oldVersion < 1 || oldVersion > versionCount) {
                System.out.println("✗ Неверный номер старой версии. Должен быть от 1 до " + versionCount);
                return;
            }

            if (newVersion < 1 || newVersion > versionCount) {
                System.out.println("✗ Неверный номер новой версии. Должен быть от 1 до " + versionCount);
                return;
            }

            if (oldVersion == newVersion) {
                System.out.println("✗ Версии одинаковые, сравнение не имеет смысла.");
                return;
            }


            FileVersion oldVer = file.getVersion(oldVersion);
            FileVersion newVer = file.getVersion(newVersion);

            System.out.println("\n--- Сравнение версий ---");
            System.out.println("СТАРАЯ версия " + oldVersion + ": " + oldVer.getComment());
            System.out.println("НОВАЯ версия " + newVersion + ": " + newVer.getComment());
            System.out.println();

            FileDiff diff = repo.compareFileVersions(fileName, oldVersion, newVersion);
            System.out.println(diff.toString());

        } catch (NumberFormatException e) {
            System.out.println("✗ Ошибка: введите корректные номера версий (целые числа)");
        } catch (Exception e) {
            System.out.println("✗ Ошибка: " + e.getMessage());
        }
    }

    private static void compareWithPrevious() {
        System.out.print("Введите имя репозитория: ");
        String repoName = scanner.nextLine().trim();

        try {
            Repository repo = vcm.getRepository(repoName);

            System.out.print("Введите имя файла: ");
            String fileName = scanner.nextLine().trim();

            if (fileName.isEmpty()) {
                System.out.println("Ошибка: имя файла не может быть пустым");
                return;
            }


            String normalizedFileName = fileName.endsWith(".txt") ? fileName : fileName + ".txt";
            if (!repo.getFiles().containsKey(normalizedFileName)) {
                System.out.println("✗ Файл не найден: " + fileName);
                return;
            }

            VersionedFile file = repo.getFiles().get(normalizedFileName);

            if (file.getVersionCount() < 2) {
                System.out.println("✗ Для сравнения с предыдущей версией нужно как минимум 2 версии файла. Сейчас версий: " + file.getVersionCount());
                return;
            }


            FileVersion previousVer = file.getVersion(file.getVersionCount() - 1);
            FileVersion currentVer = file.getLatestVersion();

            System.out.println("\n--- Сравнение с предыдущей версией ---");
            System.out.println("ПРЕДЫДУЩАЯ версия " + (file.getVersionCount() - 1) + ": " + previousVer.getComment());
            System.out.println("ТЕКУЩАЯ версия " + file.getVersionCount() + ": " + currentVer.getComment());
            System.out.println();

            FileDiff diff = repo.compareWithPreviousVersion(fileName);
            System.out.println(diff.toString());

        } catch (Exception e) {
            System.out.println("✗ Ошибка: " + e.getMessage());
        }
    }

    private static void scanRepositoryFiles() {
        System.out.print("Введите имя репозитория: ");
        String repoName = scanner.nextLine().trim();

        try {
            Repository repo = vcm.getRepository(repoName);
            int initialFileCount = repo.getFiles().size();

            repo.scanExistingTextFiles();

            int newFileCount = repo.getFiles().size();
            int addedFiles = newFileCount - initialFileCount;

            System.out.println("✓ Репозиторий просканирован на наличие .txt файлов.");
            if (addedFiles > 0) {
                System.out.println("✓ Добавлено файлов: " + addedFiles);
            } else {
                System.out.println("✓ Новых .txt файлов не обнаружено.");
            }

        } catch (Exception e) {
            System.out.println("✗ Ошибка: " + e.getMessage());
        }
    }

    private static void deleteRepository() {
        System.out.print("Введите имя репозитория для удаления: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Ошибка: имя репозитория не может быть пустым");
            return;
        }

        try {

            System.out.print("Вы уверены, что хотите удалить репозиторий '" + name + "'? (да/нет): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (confirmation.equals("да") || confirmation.equals("д")) {
                vcm.deleteRepository(name);
                System.out.println("✓ Репозиторий удален: " + name);
            } else {
                System.out.println("Удаление отменено.");
            }

        } catch (Exception e) {
            System.out.println("✗ Ошибка: " + e.getMessage());
        }
    }
}