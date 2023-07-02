package org.swdc.demo.table.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.swdc.demo.table.entity.StoredRow;
import org.swdc.demo.table.entity.StoredTable;
import org.swdc.demo.table.web.Result;
import org.swdc.demo.table.web.dtos.TableRequests;
import org.swdc.demo.table.web.service.StoreTableService;
import org.swdc.demo.table.web.service.StoredRowService;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/tables")
public class TableController {

    @Autowired
    private StoreTableService tableService;

    @Autowired
    private StoredRowService storedRowService;

    @GetMapping("/load/{folderId}/page/{pageNo}")
    public Result<Page<StoredTable>> getAllTables(@PathVariable("folderId") Long folderId, @PathVariable("pageNo") Integer pageNo) {
        if (folderId == null || folderId < 0) {
            return Result.failed("invalid parameter");
        }
        return Result.success(
                tableService.getTables(folderId,pageNo)
        );
    }

    @PostMapping("/create/{folderId}")
    public Result<StoredTable> createTable(@PathVariable("folderId") Long folderId, @RequestBody TableRequests.TableRequest tableRequest) {
        if (tableRequest == null || tableRequest.getTableId() != null || tableRequest.getColumns().size() == 0) {
            return Result.failed("invalid parameter");
        }
        if (folderId == null || folderId < 0) {
            return Result.failed("invalid parameter");
        }
        StoredTable table = tableService.save(folderId,tableRequest);
        if (table == null) {
            return Result.failed("invalid parameter");
        }
        return Result.success(
                table
        );
    }

    @PostMapping("/update")
    public Result<StoredTable> updateTable(@RequestBody TableRequests.TableRequest request) {
        if (request == null ||request.getTableId() == null || request.getTableId() < 0 || request.getColumns().size() == 0) {
            return Result.failed("invalid parameter");
        }
        StoredTable table = tableService.updateTable(request);
        if (table == null) {
            return Result.failed("invalid parameter");
        }
        return Result.success(table);
    }

    @GetMapping("/{tableId}/load")
    public Result<StoredTable> getTable(@PathVariable("tableId") Long tableId) {
        if (tableId == null || tableId < 0) {
            return Result.failed("invalid parameter");
        }
        StoredTable table = tableService.getTable(tableId);
        if (table == null) {
            return Result.failed("no such table");
        }
        return Result.success(table);
    }

    @GetMapping("/{tableId}/{page}/rows")
    public Result<Page<StoredRow>> getTableRows(@PathVariable("tableId") Long tableId, @PathVariable("page") Integer page) {
        if (tableId == null || page == null || tableId < 0 || page < 0) {
            return Result.failed("invalid parameter");
        }
        Page<StoredRow> rows = storedRowService.getRows(tableId,page);
        if (rows == null) {
            return Result.failed("failed to load data");
        }
        return Result.success(rows);
    }


    @PostMapping("/{tableId}/rows")
    public Result<StoredRow> addRow(@PathVariable("tableId") Long tableId, @RequestBody Map<Long,String> content) {
        if (tableId == null || content == null || tableId < 0 || content.size() == 0) {
            return Result.failed("invalid parameter");
        }
        StoredRow row = storedRowService.saveRow(tableId,content);
        if (row == null) {
            return Result.failed("failed to save data");
        }
        return Result.success(row);
    }


    @PostMapping("/{rowId}/update")
    public Result<StoredRow> updateRow(@PathVariable("rowId") Long rowId, @RequestBody Map<Long,String> content) {
        if (content == null || rowId == null || rowId < 0 || content.size() == 0) {
            return Result.failed("invalid parameter");
        }
        StoredRow row = storedRowService.updateRow(rowId,content);
        if (row != null) {
            return Result.success(row);
        }
        return Result.failed("can not save the data");
    }

}
