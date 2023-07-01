package org.swdc.demo.table.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.swdc.demo.table.entity.StoredColumnType;
import org.swdc.demo.table.web.Result;
import org.swdc.demo.table.web.service.StoredColumnTypeService;

import java.util.Set;

@RestController
@RequestMapping("/columns")
public class TableColumnTypeController {

    @Autowired
    private StoredColumnTypeService columnTypeService;

    @GetMapping("/all")
    public Result<Set<StoredColumnType>> getAllTypes() {
        return Result.success(
                columnTypeService.getAllColumnTypes()
        );
    }

    @PostMapping("/add")
    public Result<StoredColumnType> addColumnType(@RequestBody StoredColumnType type) {
        if (
                type == null ||
                type.getName() == null || type.getName().isBlank() ||
                type.getDescriptor() == null || type.getDescriptor().isBlank() ||
                type.getEditorId() == null || type.getEditorId().isBlank()
        ) {
            return Result.failed("invalid parameter");
        }
        StoredColumnType target = columnTypeService.addColumnType(
                type.getName(),
                type.getDescriptor(),
                type.getEditorId()
        );
        if (target == null) {
            return Result.failed("invalid parameter");
        }
        return Result.success(target);
    }

    @GetMapping("/load/{typeId}")
    public Result<StoredColumnType> getColumnType(@PathVariable("typeId")Long typeId) {
        if (typeId == null || typeId < 0) {
            return Result.failed("invalid parameter");
        }
        StoredColumnType type = columnTypeService.getColumnType(typeId);
        if (type == null) {
            return Result.failed("invalid parameter");
        }
        return Result.success(type);
    }

    @PostMapping("/update/{typeId}")
    public Result<StoredColumnType> updateColumnType(@PathVariable("typeId")Long typeId, @RequestBody StoredColumnType type) {
        if (typeId == null || typeId < 0 || type == null || type.getEditorId() == null || type.getEditorId().isBlank()) {
            return Result.failed("invalid parameter");
        }
        StoredColumnType result = columnTypeService.updateColumnType(
                typeId,
                type.getName(),
                type.getDescriptor(),
                type.getEditorId()
        );
        if (result == null) {
            return Result.failed("invalid parameter");
        }
        return Result.success(result);
    }

    @PostMapping("/trash/{typeId}")
    public Result<StoredColumnType> trashColumnType(@PathVariable("typeId")Long typeId) {
        if (typeId == null || typeId < 0) {
            return Result.failed("invalid parameter");
        }
        try {
            StoredColumnType result = columnTypeService.deleteColumnType(typeId);
            if (result == null) {
                return Result.failed("invalid parameter");
            }
            return Result.success(result);
        } catch (Exception e) {
            return Result.failed("failed to trash a type");
        }
    }
}
