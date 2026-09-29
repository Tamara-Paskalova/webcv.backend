package com.tamara.webcv.media;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MediaRepository<T extends MediaEntity> extends JpaRepository<T, Long> {
    @Query(value = "SELECT * FROM #{#entityName} WHERE media_metadata ->> 'type' = :type", nativeQuery = true)
    List<T> findAllByMetadataType(@Param("type") String type);
}
