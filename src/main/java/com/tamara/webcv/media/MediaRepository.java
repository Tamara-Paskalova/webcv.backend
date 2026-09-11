package com.tamara.webcv.media;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository<T extends MediaEntity> extends JpaRepository<T, Long> {
}
