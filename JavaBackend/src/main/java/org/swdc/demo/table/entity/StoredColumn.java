package org.swdc.demo.table.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
public class StoredColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String name;

    @Setter
    @Getter
    @JoinColumn(name = "type_id")
    @ManyToOne(cascade = CascadeType.DETACH)
    private StoredColumnType type;

    @Getter
    @Setter
    @JoinColumn(name = "table_id")
    @ManyToOne(cascade = CascadeType.DETACH)
    private StoredTable table;

    private int orderIndex;

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public StoredColumn unManaged() {
        StoredColumn column = new StoredColumn();
        column.setId(getId());
        column.setName(getName());
        column.setType(getType());
        column.setOrderIndex(getOrderIndex());
        return column;
    }

}
