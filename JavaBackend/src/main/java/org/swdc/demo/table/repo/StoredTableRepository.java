package org.swdc.demo.table.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.swdc.demo.table.entity.StoredTable;

@Repository
public interface StoredTableRepository extends JpaRepository<StoredTable,Long> {

    @Query("from StoredTable st WHERE st.folder.id = :folderId")
    Page<StoredTable> findByFolderId(@Param("folderId")Long folderId, Pageable pageable);

    @Query("from StoredTable WHERE tableName = :name")
    StoredTable findByName(@Param("name") String name);

}
