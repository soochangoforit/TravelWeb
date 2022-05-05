package com.cloud.web.repository;

import com.cloud.web.domain.FoodCmt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodCmtRepository extends JpaRepository<FoodCmt,Long> {
}
