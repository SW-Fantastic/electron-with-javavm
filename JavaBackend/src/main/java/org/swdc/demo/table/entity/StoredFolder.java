package org.swdc.demo.table.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class StoredFolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String folderName;

    @Getter
    @Setter
    @JoinColumn(name = "parent_id")
    @ManyToOne(cascade = CascadeType.DETACH)
    @JsonIgnore
    private StoredFolder parent;

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.REMOVE,mappedBy = "parent")
    private Set<StoredFolder> children;

    public StoredFolder unManaged(boolean withChildren) {
        StoredFolder target = new StoredFolder();
        target.setFolderName(getFolderName());
        target.setId(getId());
        Set<StoredFolder> children = getChildren();
        if (children != null && withChildren) {
            target.setChildren(children.stream()
                    .map(f -> f.unManaged(true))
                    .peek(s -> s.setParent(target))
                    .collect(Collectors.toSet())
            );
        } else {
            target.setChildren(Collections.emptySet());
        }
        return target;
    }


}
