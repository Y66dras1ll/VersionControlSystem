package com.versioncontrol.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

class VersionControlManagerTest {

    @Test
    void testVersionControlManagerCreation() {
        VersionControlManager vcm = new VersionControlManager();
        assertNotNull(vcm.getRepositories());
        assertEquals(0, vcm.getRepositories().size());
    }

    @Test
    void testCreateRepository() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-test");
        VersionControlManager vcm = new VersionControlManager();

        Repository repo = vcm.createRepository("repo1", tempDir.toString());

        assertEquals("repo1", repo.getName());
        assertEquals(1, vcm.getRepositories().size());
        assertEquals(repo, vcm.getRepository("repo1"));
    }

    @Test
    void testCreateDuplicateRepository() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-test");
        VersionControlManager vcm = new VersionControlManager();

        vcm.createRepository("repo1", tempDir.toString());
        assertThrows(IllegalArgumentException.class, () ->
                vcm.createRepository("repo1", tempDir.toString()));
    }

    @Test
    void testGetNonExistentRepository() {
        VersionControlManager vcm = new VersionControlManager();
        assertThrows(IllegalArgumentException.class, () -> vcm.getRepository("nonexistent"));
    }

    @Test
    void testDeleteRepository() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-test");
        VersionControlManager vcm = new VersionControlManager();

        vcm.createRepository("repo1", tempDir.toString());
        assertEquals(1, vcm.getRepositories().size());

        vcm.deleteRepository("repo1");
        assertEquals(0, vcm.getRepositories().size());
    }

    @Test
    void testDeleteNonExistentRepository() {
        VersionControlManager vcm = new VersionControlManager();
        assertThrows(IllegalArgumentException.class, () -> vcm.deleteRepository("nonexistent"));
    }

    @Test
    void testMultipleRepositories() throws IOException {
        Path tempDir1 = Files.createTempDirectory("vcs-test1");
        Path tempDir2 = Files.createTempDirectory("vcs-test2");
        VersionControlManager vcm = new VersionControlManager();

        Repository repo1 = vcm.createRepository("repo1", tempDir1.toString());
        Repository repo2 = vcm.createRepository("repo2", tempDir2.toString());

        assertEquals(2, vcm.getRepositories().size());
        assertEquals(repo1, vcm.getRepository("repo1"));
        assertEquals(repo2, vcm.getRepository("repo2"));
    }

    @Test
    void testGetRepositories() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-test");
        VersionControlManager vcm = new VersionControlManager();

        vcm.createRepository("repo1", tempDir.toString());
        vcm.createRepository("repo2", tempDir.toString());

        var repos = vcm.getRepositories();
        assertEquals(2, repos.size());
        assertTrue(repos.containsKey("repo1"));
        assertTrue(repos.containsKey("repo2"));
    }

    @Test
    void testToString() throws IOException {
        Path tempDir = Files.createTempDirectory("vcs-test");
        VersionControlManager vcm = new VersionControlManager();

        vcm.createRepository("repo1", tempDir.toString());

        String result = vcm.toString();
        assertTrue(result.contains("Менеджер контроля версий"));
        assertTrue(result.contains("1 репозиториев"));
    }
}