package com.inghubs.brokerage.service;

import com.inghubs.brokerage.common.AuthenticationUtil;
import com.inghubs.brokerage.dto.AssetDto;
import com.inghubs.brokerage.dto.request.AddAssetRequest;
import com.inghubs.brokerage.entity.Asset;
import com.inghubs.brokerage.entity.AssetId;
import com.inghubs.brokerage.repository.AssetRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetService {

  private final AssetRepository assetRepository;
  private final AuthenticationUtil authenticationUtil;

  public List<AssetDto> listAssets(Long customerId) {
    authenticationUtil.checkPermission(customerId);

    return assetRepository.findByIdCustomerId(customerId).stream().map(this::mapToDto).toList();
  }

  public AssetDto addAsset(AddAssetRequest addAssetRequest) {
    authenticationUtil.checkPermission(addAssetRequest.customerId());
    Asset asset =
        assetRepository
            .findById(new AssetId(addAssetRequest.customerId(), addAssetRequest.assetName()))
            .orElseGet(
                () ->
                    Asset.builder()
                        .id(new AssetId(addAssetRequest.customerId(), addAssetRequest.assetName()))
                        .size(0.0)
                        .usableSize(0.0)
                        .build());
    asset.setSize(asset.getSize() + addAssetRequest.size());
    asset.setUsableSize(asset.getUsableSize() + addAssetRequest.size());
    assetRepository.save(asset);
    return mapToDto(asset);
  }

  private AssetDto mapToDto(Asset asset) {
    return AssetDto.builder()
        .customerId(asset.getId().getCustomerId())
        .assetName(asset.getId().getAssetName())
        .size(asset.getSize())
        .usableSize(asset.getUsableSize())
        .build();
  }
}
