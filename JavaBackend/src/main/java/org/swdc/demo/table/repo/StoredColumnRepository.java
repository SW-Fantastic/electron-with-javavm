package org.swdc.demo.table.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swdc.demo.table.entity.StoredColumn;

@Repository
public interface StoredColumnRepository extends JpaRepository<StoredColumn, Long> {
}
