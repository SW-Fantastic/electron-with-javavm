package org.swdc.demo.table.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.swdc.demo.table.web.service.StoredFolderService;
import org.swdc.demo.table.entity.StoredFolder;
import org.swdc.demo.table.web.Result;
import org.swdc.demo.table.web.Status;

import java.util.Collections;
import java.util.Set;

@RestController
@RequestMapping("/folders")
public class TableFolderController {

    @Autowired
    private StoredFolderService folderService;

    @GetMapping("/roots")
    public Result<Set<StoredFolder>> getRootFolders(){
        return Result.success(folderService.getRootFolders());
    }

    @GetMapping("/roots/{parent}")
    public Result<Set<StoredFolder>> getChildren(@PathVariable("parent")Long parent) {
        if (parent == null || parent < 0) {
            return Result.failed("invalid parameter");
        }
        return Result.success(
                folderService.getChildren(parent)
        );
    }

    @PostMapping("/createRoot")
    public Result<StoredFolder> createRootFolder(@RequestBody StoredFolder folder) {
        StoredFolder target = folderService.createRootFolder(folder.getFolderName());
        if (target == null) {
            return Result.failed("invalid Parameter");
        }
        return Result.success(target);
    }

    @PostMapping("/updateFolder/{folderId}")
    public Result<StoredFolder> updateFolder(@PathVariable("folderId") Long folderId, @RequestBody StoredFolder folder) {
        if (folderId == null || folderId <= 0){
            return Result.failed("invalid parameter");
        }
        StoredFolder target = folderService.updateFolder(
                folderId,
                folder.getFolderName()
        );
        if (target == null) {
            return Result.failed("invalid parameter");
        }
        return Result.success(target);
    }


    @PostMapping("/deleteFolder/{folderId}")
    public Result<StoredFolder> deleteType(@PathVariable("folderId")Long id) {
        if (id == null || id < 0) {
            return Result.failed("invalid parameter");
        }
        StoredFolder folder = folderService.deleteFolder(id);
        if (folder == null) {
            return Result.failed("invalid parameter");
        }
        return Result.success(folder);
    }

    @PostMapping("/create/{parentId}")
    public Result<StoredFolder> createFolder(
            @PathVariable("parentId") Long parentId,
            @RequestBody StoredFolder folder
    ) {
        StoredFolder rst = folderService.createFolder(parentId == -1 ? null : parentId,folder.getFolderName());
        if (rst == null) {
            return Result.failed("invalid parameter");
        }
        return Result.success(rst);
    }


}
