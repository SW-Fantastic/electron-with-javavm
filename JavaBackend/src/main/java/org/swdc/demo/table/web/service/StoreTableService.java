package org.swdc.demo.table.web.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.swdc.demo.table.entity.StoredColumn;
import org.swdc.demo.table.entity.StoredColumnType;
import org.swdc.demo.table.entity.StoredFolder;
import org.swdc.demo.table.entity.StoredTable;
import org.swdc.demo.table.repo.StoredColumnRepository;
import org.swdc.demo.table.repo.StoredColumnTypeRepository;
import org.swdc.demo.table.repo.StoredFolderRepository;
import org.swdc.demo.table.repo.StoredTableRepository;
import org.swdc.demo.table.web.dtos.TableRequests;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
    private StoredFolderRepository folderRepository;

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
        for (StoredColumn column: columns) {
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
