package com.inghubs.brokerage.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.*;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssetId implements Serializable {
  private Long customerId;
  private String assetName;
}
