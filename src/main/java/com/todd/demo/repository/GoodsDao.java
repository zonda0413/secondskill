package com.todd.demo.repository;

import com.todd.demo.entity.GoodsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsDao extends JpaRepository<GoodsEntity, Integer> {
    @Query("select t.count from GoodsEntity t")
    Integer findCount();

    @Query("from GoodsEntity t where t.name=:name")
    GoodsEntity findByName(@Param("name") String name);
}
