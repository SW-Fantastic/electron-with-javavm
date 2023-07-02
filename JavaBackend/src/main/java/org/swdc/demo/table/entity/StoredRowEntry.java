package org.swdc.demo.table.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
public class StoredRowEntry {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @ManyToOne(cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private StoredColumn column;

    @Getter
    @Setter
    @Column(columnDefinition = "text")
    private String data;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private StoredRow row;

    public StoredRowEntry unManaged() {
        StoredRowEntry entry = new StoredRowEntry();
        entry.setData(getData());
        entry.setId(getId());
        entry.setColumn(getColumn().unManaged());
        return entry;
    }

}
