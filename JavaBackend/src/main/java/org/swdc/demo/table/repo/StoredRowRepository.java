package org.swdc.demo.table.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.swdc.demo.table.entity.StoredRow;

@Repository
public interface StoredRowRepository extends JpaRepository<StoredRow,Long> {

    @Query("FROM StoredRow as theRow WHERE theRow.owner.id = :tableId ORDER BY theRow.id DESC")
    Page<StoredRow> getRows(@Param("tableId") Long tableId, Pageable pageable);

}
