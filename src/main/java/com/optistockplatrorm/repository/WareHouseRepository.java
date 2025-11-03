package com.optistockplatrorm.repository;

import com.optistockplatrorm.entity.Warehouse;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface WareHouseRepository extends CrudRepository<Warehouse, Long> {
    List<Warehouse> findAllById(Iterable<Long> ID);
}
