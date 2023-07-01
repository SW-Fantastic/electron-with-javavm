package org.swdc.demo.table.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.swdc.demo.table.repo.StoredFolderRepository;
import org.swdc.demo.table.entity.StoredFolder;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StoredFolderService {

    private static final Logger logger = LoggerFactory.getLogger(StoredFolderService.class);

    @Autowired
    private StoredFolderRepository repository;

    public Set<StoredFolder> getRootFolders() {
        Set<StoredFolder> folders = repository.getRootFolders();
        if (folders == null) {
            folders = Collections.emptySet();
        }
        return folders.stream()
                .map(f -> f.unManaged(true))
                .collect(Collectors.toSet());
    }

    public Set<StoredFolder> getChildren(Long parent) {
        if (parent == null || parent < 0) {
            return Collections.emptySet();
        }
        Set<StoredFolder> folders = repository.getChildren(parent);
        if (folders == null) {
            return Collections.emptySet();
        }
        return folders.stream()
                .map(f -> f.unManaged(true))
                .collect(Collectors.toSet());
    }

    public StoredFolder createRootFolder(String folderName) {
        if (folderName == null || folderName.isBlank()) {
            logger.error("can not create a folder with exist empty name");
            return null;
        }
        StoredFolder created = new StoredFolder();
        created.setFolderName(folderName);
        return repository
                .save(created)
                .unManaged(true);
    }

    public StoredFolder updateFolder(Long id, String name) {
        if (id == null || id < 0) {
            logger.error("can not update a folder with invalid id");
            return null;
        }
        if (name.isBlank()) {
            logger.error("can not setup type name with a empty string");
            return null;
        }
        StoredFolder folder = repository.findById(id).orElse(null);
        if (folder == null) {
            logger.error("no such type with id :" + id);
            return null;
        }
        folder.setFolderName(name);
        return repository
                .save(folder)
                .unManaged(true);
    }

    public StoredFolder deleteFolder(Long id) {
        if (id == null || id < 0) {
            logger.error("can not delete a folder with invalid id");
            return null;
        }
        StoredFolder folder = repository.findById(id).orElse(null);
        if (folder == null) {
            logger.error("folder is not exist");
            return null;
        }
        StoredFolder result = folder.unManaged(false);
        repository.delete(folder);
        return result;
    }

    public StoredFolder createFolder(Long parent, String folderName) {
        if (folderName == null || folderName.isBlank()) {
            logger.error("can not create a folder with exist empty name");
            return null;
        }
        StoredFolder parentFolder = null;
        if (parent != null) {
            parentFolder = repository.findById(parent).orElse(null);
        }
        StoredFolder created = new StoredFolder();
        created.setFolderName(folderName);
        created.setParent(parentFolder);
        return repository
                .save(created)
                .unManaged(true);
    }

    public StoredFolder renameFolder(Long folderId, String newName) {
        if (folderId <= 0) {
            logger.error("can not rename a folder without folder id.");
            return null;
        }
        if (newName == null || newName.isBlank()) {
            logger.error("can not rename folder without folder name.");
            return null;
        }
        StoredFolder folder = repository.findById(folderId).orElse(null);
        if (folder == null) {
            logger.error("no such folder found: " + folderId);
            return null;
        }
        folder.setFolderName(newName);
        return repository.save(folder)
                .unManaged(true);
    }


    public StoredFolder moveFolder(Long oldParent, Long newParent, Long folderId) {
        return null;
    }

}
