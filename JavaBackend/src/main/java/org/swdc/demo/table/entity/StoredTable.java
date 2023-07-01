package org.swdc.demo.table.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class StoredTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String tableName;

    @Getter
    @Setter
    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private Set<StoredColumn> columns;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.DETACH)
    private StoredFolder folder;

    @Getter
    @Setter
    @Column(columnDefinition = "text")
    private String desc;

    public StoredTable unManaged() {
        StoredTable table = new StoredTable();
        table.setId(this.getId());
        table.setTableName(this.getTableName());
        table.setColumns(this.getColumns().stream().map(StoredColumn::unManaged).collect(Collectors.toSet()));
        table.setFolder(this.getFolder().unManaged(false));
        table.setDesc(this.getDesc());
        return table;
    }

}
