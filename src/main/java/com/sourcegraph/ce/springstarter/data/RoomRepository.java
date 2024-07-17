package com.sourcegraph.ce.springstarter.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Repository interface for {@code Room} entities. Extends
 * {@code CrudRepository} to provide basic CRUD operations.
 */
public interface RoomRepository extends CrudRepository<Room, Long> {
}
