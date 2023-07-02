package org.swdc.demo.table.web.dtos;

import lombok.Data;

import java.util.Set;

public class TableRequests {

    @Data
    public static class TableRequestColumn {

        private Long columnId;

        private String columnName;

        private Long columnTypeId;

        private int orderIndex;

    }

    @Data
    public static class TableRequest {

        private Long tableId;

        private String tableName;

        private Set<TableRequestColumn> columns;

    }

    @Data
    public static class TableColumnSearch {

        private String tableName;

        private String columnName;

        private String key;

    }

}
