package com.kawai.repositories;

import com.kawai.models.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface FoodItemRepository extends JpaRepository<MenuItem, Long> {
    @Query("SELECT DISTINCT m.category FROM MenuItem m")
    List<String> findDistinctCategories();
}
