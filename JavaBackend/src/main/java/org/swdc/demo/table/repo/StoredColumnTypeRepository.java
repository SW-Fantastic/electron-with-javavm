package org.swdc.demo.table.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.swdc.demo.table.entity.StoredColumnType;

@Repository
public interface StoredColumnTypeRepository extends JpaRepository<StoredColumnType, Long> {

    @Query("from StoredColumnType where name = :name")
    StoredColumnType getColumnTypeByName(@Param("name") String name);

}
