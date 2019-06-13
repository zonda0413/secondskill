package com.todd.demo.repository;

import com.todd.demo.entity.RecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordDao extends JpaRepository<RecordEntity, Integer> {

}
