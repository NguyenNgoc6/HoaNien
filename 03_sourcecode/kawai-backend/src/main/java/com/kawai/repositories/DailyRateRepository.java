package com.kawai.repositories;

import com.kawai.models.DailyRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyRateRepository extends JpaRepository<DailyRate, Long> {
}
