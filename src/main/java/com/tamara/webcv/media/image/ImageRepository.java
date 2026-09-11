package com.tamara.webcv.media.image;

import com.tamara.webcv.media.MediaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends MediaRepository<Image> {

    @Query("SELECT i FROM Image i WHERE NOT EXISTS (SELECT p FROM Project p WHERE p.image = i)")
    List<Image> findStandaloneImages();
}
