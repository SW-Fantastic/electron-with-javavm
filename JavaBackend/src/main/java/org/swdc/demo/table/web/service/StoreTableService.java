package org.swdc.demo.table.web.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.swdc.demo.table.entity.*;
import org.swdc.demo.table.repo.*;
import org.swdc.demo.table.web.dtos.TableRequests;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StoreTableService {

    @Autowired
    private StoredTableRepository repository;

    @Autowired
    private StoredColumnRepository columnRepository;

    @Autowired
    private StoredColumnTypeRepository typeRepository;

    @Autowired
    private StoredRowEntryRepository entryRepository;

    @Autowired
    private StoredFolderRepository folderRepository;

    private Logger logger = LoggerFactory.getLogger(StoreTableService.class);

    public Page<StoredTable> getTables(Long folderId, Integer pageNo) {
        if (folderId == null || folderId < 0) {
            return null;
        }
        Pageable pageable = Pageable
                .ofSize(20)
                .withPage(pageNo);
        Page<StoredTable> page = repository.findByFolderId(folderId,pageable);
        if (page.getSize() == 0) {
            return null;
        }
        return new PageImpl<>(
                page.stream()
                        .map(StoredTable::unManaged)
                        .collect(Collectors.toList()),
                pageable,
                page.getTotalElements()
        );
    }

    @Transactional
    public StoredTable updateTable(TableRequests.TableRequest content) {
        if (
                content.getTableId() == null ||
                content.getTableId() < 0 ||
                content.getTableName() == null ||
                content.getTableName().isBlank() ||
                content.getColumns() == null ||
                content.getColumns().size() == 0
        ) {
            return null;
        }

        StoredTable table = repository.findById(content.getTableId()).orElse(null);
        if (table == null) {
            return null;
        }
        Set<StoredColumn> columns = table.getColumns();
        Set<StoredColumn> storedColumns = new HashSet<>();
        if (columns == null) {
            columns = new HashSet<>();
            table.setColumns(columns);
        }

        Map<Long,StoredColumn> existsMap = columns
                .stream()
                .collect(Collectors.toMap(StoredColumn::getId, c -> c));

        for (TableRequests.TableRequestColumn column: content.getColumns()) {
            if (column.getColumnTypeId() == null || column.getColumnTypeId() < 0) {
                continue;
            }
            StoredColumn target = new StoredColumn();
            StoredColumnType type = typeRepository.findById(column.getColumnTypeId()).orElse(null);
            if (column.getColumnId() == null || column.getColumnId() < 0) {
                // new column
                target.setName(column.getColumnName());
                target.setTable(table);
                target.setType(type);
                target.setOrderIndex(column.getOrderIndex());
                storedColumns.add(columnRepository.save(target));
            } else {
                target = existsMap.remove(column.getColumnId());
                if (target == null) {
                    continue;
                }
                target.setOrderIndex(column.getOrderIndex());
                target.setName(column.getColumnName());
                target.setTable(table);
                target.setType(type);
                storedColumns.add(columnRepository.save(target));
            }
        }
        for (StoredColumn column: existsMap.values()) {
            int removed = entryRepository.deleteByColumns(column.getId());
            logger.warn("there are " + removed + " entries has removed.");
            columnRepository.delete(column);
        }
        table.setColumns(storedColumns);
        table = repository.save(table);
        return table.unManaged();
    }

    @Transactional
    public StoredTable save(Long storedType, TableRequests.TableRequest table) {
        if (
                table.getTableId() != null ||
                table.getTableName() == null||
                table.getTableName().isBlank() ||
                table.getColumns() == null ||
                table.getColumns().size() == 0
        ) {
            return null;
        }

        StoredTable exists = repository.findByName(table.getTableName());
        if (exists != null) {
            return null;
        }

        if (storedType == null || storedType < 0) {
            return null;
        }

        StoredFolder folder = folderRepository.findById(storedType).orElse(null);
        if (folder == null) {
            return null;
        }

        Set<StoredColumn> cols = new HashSet<>();

        Set<TableRequests.TableRequestColumn> columnList = table.getColumns();
        for (TableRequests.TableRequestColumn col : columnList) {
            if (col.getColumnTypeId() == null || col.getColumnTypeId() < 0) {
                continue;
            }
            StoredColumnType type = typeRepository.findById(col.getColumnTypeId()).orElse(null);
            if (type == null) {
                continue;
            }

            StoredColumn item = new StoredColumn();
            item.setName(col.getColumnName());
            item.setType(type);
            item.setOrderIndex(col.getOrderIndex());
            item = columnRepository.save(item);
            cols.add(item);
        }

        StoredTable target = new StoredTable();
        target.setTableName(table.getTableName());
        target.setFolder(folder);
        target.setColumns(cols);
        target = repository.save(target);
        return target.unManaged();
    }

    public List<StoredRowEntry> getEntries(String tableName, String columnName, String key) {
        if (tableName == null || tableName.isBlank() || columnName == null || columnName.isBlank()){
            return Collections.emptyList();
        }
        StoredTable table = repository.findByName(tableName);
        if (table == null) {
            return null;
        }
        StoredColumn target = null;
        for (StoredColumn column: table.getColumns()) {
            if (column.getName().equals(columnName)) {
                target = column;
                break;
            }
        }
        if (target == null) {
            return Collections.emptyList();
        }
        List<StoredRowEntry> entries = entryRepository.searchByValue(target.getId(),key);
        if (entries == null) {
            return Collections.emptyList();
        }
        return entries
                .stream()
                .map(StoredRowEntry::unManaged)
                .collect(Collectors.toList());
    }

    public StoredTable getTable(Long id) {
        if (id == null || id < 0) {
            return null;
        }
        StoredTable table = repository.findById(id).orElse(null);
        if (table == null) {
            return null;
        }
        return table.unManaged();
    }

}
