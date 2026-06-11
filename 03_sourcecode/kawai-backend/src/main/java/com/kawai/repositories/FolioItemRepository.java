package com.kawai.repositories;

import com.kawai.models.FolioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FolioItemRepository extends JpaRepository<FolioItem, Long> {
}
