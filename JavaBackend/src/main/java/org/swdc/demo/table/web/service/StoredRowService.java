package org.swdc.demo.table.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.swdc.demo.table.entity.StoredColumn;
import org.swdc.demo.table.entity.StoredRow;
import org.swdc.demo.table.entity.StoredRowEntry;
import org.swdc.demo.table.entity.StoredTable;
import org.swdc.demo.table.repo.StoredColumnRepository;
import org.swdc.demo.table.repo.StoredRowEntryRepository;
import org.swdc.demo.table.repo.StoredRowRepository;
import org.swdc.demo.table.repo.StoredTableRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StoredRowService {

    @Autowired
    private StoredRowRepository repository;

    @Autowired
    private StoredRowEntryRepository entryRepository;

    @Autowired
    private StoredColumnRepository columnRepository;

    @Autowired
    private StoredTableRepository tableRepository;

    public StoredRow saveRow(Long tableId, Map<Long,String> columnDataMap) {

        if (tableId == null || tableId < 0) {
            return null;
        }
        StoredTable table = tableRepository.findById(tableId).orElse(null);
        if (table == null) {
            return null;
        }
        StoredRow row = new StoredRow();
        row.setOwner(table);
        row = repository.save(row);

        List<StoredRowEntry> entries = new ArrayList<>();
        for (Long column: columnDataMap.keySet()) {
            StoredColumn col = columnRepository.findById(column).orElse(null);
            if (col == null) {
                continue;
            }
            StoredRowEntry entry = new StoredRowEntry();
            entry.setColumn(col);
            entry.setData(columnDataMap.get(column));
            entry.setRow(row);
            entry = entryRepository.save(entry);
            entries.add(entry);
        }

        row.setRowEntries(entries);
        row = repository.save(row);
        return row.unManaged();
    }

    public StoredRow updateRow(Long rowId, Map<Long,String> content) {
        if (rowId == null || rowId < 0) {
            return null;
        }
        StoredRow row = repository.findById(rowId).orElse(null);
        if (row == null) {
            return null;
        }
        StoredTable table = row.getOwner();
        Map<Long,StoredColumn> columns = table.getColumns().stream()
                .collect(Collectors.toMap(StoredColumn::getId,s -> s));

        Map<Long,StoredRowEntry> entries = row.getRowEntries().stream()
                .collect(Collectors.toMap(e -> e.getColumn().getId(),e -> e));

        for (Long colId: content.keySet()) {
            if (content.get(colId) == null) {
                continue;
            }
            if (columns.containsKey(colId)) {
                StoredRowEntry entry = new StoredRowEntry();
                if (entries.containsKey(colId)) {
                    // exist
                    entry = entries.get(colId);
                    entry.setData(content.get(colId));
                    entryRepository.save(entry);
                } else {
                    // new one
                    entry.setRow(row);
                    entry.setData(content.get(colId));
                    entry.setColumn(columns.get(colId));
                    entryRepository.save(entry);
                }
            }
        }
        row.setRowEntries(new ArrayList<>(entries.values()));
        row = repository.save(row);
        return row.unManaged();
    }

    public Page<StoredRow> getRows(Long tableId, int pageNo) {
        if (tableId == null || tableId < 0) {
            return null;
        }
        Pageable pageable = Pageable
                .ofSize(20)
                .withPage(pageNo);
        Page<StoredRow> page = repository.getRows(tableId,pageable);
        if (page.getSize() == 0) {
            return null;
        }
        return new PageImpl<>(
                page.stream()
                        .map(StoredRow::unManaged)
                        .collect(Collectors.toList()),
                pageable,
                page.getTotalElements()
        );
    }

}
