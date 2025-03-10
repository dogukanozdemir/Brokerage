package com.inghubs.brokerage.repository;

import com.inghubs.brokerage.entity.Asset;
import com.inghubs.brokerage.entity.AssetId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<Asset, AssetId> {

  List<Asset> findByIdCustomerId(Long customerId);
}
