package com.versioncontrol.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VersionControlManager {
    private final Map<String, Repository> repositories;

    public VersionControlManager() {
        this.repositories = new HashMap<>();
    }

    public Repository createRepository(String name, String repositoryPath) {
        Objects.requireNonNull(name, "Имя репозитория не может быть null");
        Objects.requireNonNull(repositoryPath, "Путь репозитория не может быть null");

        if (repositories.containsKey(name)) {
            throw new IllegalArgumentException("Репозиторий уже существует: " + name);
        }

        Repository repository = new Repository(name, repositoryPath);
        repositories.put(name, repository);


        repository.scanExistingTextFiles();

        return repository;
    }

    public Repository getRepository(String name) {
        Objects.requireNonNull(name, "Имя репозитория не может быть null");

        Repository repository = repositories.get(name);
        if (repository == null) {
            throw new IllegalArgumentException("Репозиторий не найден: " + name);
        }
        return repository;
    }

    public void deleteRepository(String name) {
        Objects.requireNonNull(name, "Имя репозитория не может быть null");

        if (!repositories.containsKey(name)) {
            throw new IllegalArgumentException("Репозиторий не найден: " + name);
        }

        repositories.remove(name);
    }

    public Map<String, Repository> getRepositories() {
        return new HashMap<>(repositories);
    }

    @Override
    public String toString() {
        return String.format("Менеджер контроля версий: %d репозиториев", repositories.size());
    }
}