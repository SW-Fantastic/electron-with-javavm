package org.swdc.demo.table.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
public class StoredRow {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @ManyToOne(cascade = CascadeType.DETACH)
    private StoredTable owner;

    @Getter
    @Setter
    @OneToMany(cascade = {
            CascadeType.REMOVE
    },fetch = FetchType.EAGER, mappedBy = "row")
    private List<StoredRowEntry> rowEntries;

    public StoredRow unManaged(){
        StoredRow row = new StoredRow();
        row.setId(getId());
        row.setRowEntries(getRowEntries()
                .stream()
                .filter(Objects::nonNull)
                .map(StoredRowEntry::unManaged)
                .collect(Collectors.toList())
        );
        row.setOwner(getOwner().unManaged());
        return row;
    }

}
