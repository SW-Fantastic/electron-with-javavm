package org.swdc.demo.table.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.swdc.demo.table.entity.StoredFolder;

import java.util.Set;

@Repository
public interface StoredFolderRepository extends JpaRepository<StoredFolder,Long> {

    @Query("from StoredFolder where parent is null")
    Set<StoredFolder> getRootFolders();

    @Query("from StoredFolder where parent.id = :parentId")
    Set<StoredFolder> getChildren(@Param("parentId") Long parentId);

}
