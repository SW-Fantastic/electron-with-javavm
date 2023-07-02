package org.swdc.demo.table.repo;

import org.hibernate.annotations.SQLUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.swdc.demo.table.entity.StoredRowEntry;

@Repository
public interface StoredRowEntryRepository extends JpaRepository<StoredRowEntry,Long> {

    @Modifying
    @Query("DELETE FROM StoredRowEntry as ent WHERE ent.column.id = :colId")
    int deleteByColumns(@Param("colId")Long columnId);

}
