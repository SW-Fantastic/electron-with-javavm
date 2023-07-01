package org.swdc.demo.table.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
public class StoredColumnType {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(columnDefinition = "text")
    private String descriptor;

    @Getter
    @Setter
    private String editorId;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.DETACH,fetch = FetchType.LAZY)
    private Set<StoredColumn> referenceBy;

    public StoredColumnType unManaged(){
        StoredColumnType type = new StoredColumnType();
        type.setId(getId());
        type.setName(getName());
        type.setDescriptor(getDescriptor());
        type.setEditorId(getEditorId());
        return type;
    }

}
