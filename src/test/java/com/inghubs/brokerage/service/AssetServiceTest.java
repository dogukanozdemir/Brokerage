package com.inghubs.brokerage.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import com.inghubs.brokerage.common.AuthenticationUtil;
import com.inghubs.brokerage.dto.AssetDto;
import com.inghubs.brokerage.dto.request.AddAssetRequest;
import com.inghubs.brokerage.entity.Asset;
import com.inghubs.brokerage.entity.AssetId;
import com.inghubs.brokerage.repository.AssetRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

  @Mock private AssetRepository assetRepository;

  @Mock private AuthenticationUtil authenticationUtil;

  @InjectMocks private AssetService assetService;

  @Test
  void testListAssets() {
    Long customerId = 1L;
    AssetId assetId = new AssetId(customerId, "Asset1");
    Asset asset = Asset.builder().id(assetId).size(100.0).usableSize(100.0).build();
    List<Asset> assetList = List.of(asset);

    when(assetRepository.findByIdCustomerId(customerId)).thenReturn(assetList);

    List<AssetDto> assetDtos = assetService.listAssets(customerId);

    verify(authenticationUtil, times(1)).checkPermission(customerId);
    assertNotNull(assetDtos);
    assertEquals(1, assetDtos.size());
    AssetDto result = assetDtos.getFirst();
    assertEquals(customerId, result.customerId());
    assertEquals("Asset1", result.assetName());
    assertEquals(100.0, result.size());
    assertEquals(100.0, result.usableSize());
  }

  @Test
  void testAddAsset_NewAsset() {
    Long customerId = 1L;
    String assetName = "NewAsset";
    double addSize = 50.0;
    AddAssetRequest request = new AddAssetRequest(customerId, assetName, addSize);
    AssetId assetId = new AssetId(customerId, assetName);

    when(assetRepository.findById(assetId)).thenReturn(Optional.empty());

    Asset newAsset = Asset.builder().id(assetId).size(0.0).usableSize(0.0).build();

    newAsset.setSize(newAsset.getSize() + addSize);
    newAsset.setUsableSize(newAsset.getUsableSize() + addSize);

    when(assetRepository.save(any(Asset.class))).thenReturn(newAsset);

    AssetDto result = assetService.addAsset(request);

    verify(authenticationUtil, times(1)).checkPermission(customerId);
    verify(assetRepository, times(1)).findById(assetId);
    verify(assetRepository, times(1)).save(any(Asset.class));

    assertNotNull(result);
    assertEquals(customerId, result.customerId());
    assertEquals(assetName, result.assetName());
    assertEquals(addSize, result.size());
    assertEquals(addSize, result.usableSize());
  }

  @Test
  void testAddAsset_ExistingAsset() {
    Long customerId = 1L;
    String assetName = "ExistingAsset";
    double initialSize = 100.0;
    double addSize = 50.0;
    AddAssetRequest request = new AddAssetRequest(customerId, assetName, addSize);
    AssetId assetId = new AssetId(customerId, assetName);

    Asset existingAsset =
        Asset.builder().id(assetId).size(initialSize).usableSize(initialSize).build();

    when(assetRepository.findById(assetId)).thenReturn(Optional.of(existingAsset));
    when(assetRepository.save(existingAsset)).thenReturn(existingAsset);

    AssetDto result = assetService.addAsset(request);

    verify(authenticationUtil, times(1)).checkPermission(customerId);
    verify(assetRepository, times(1)).findById(assetId);
    verify(assetRepository, times(1)).save(existingAsset);

    assertNotNull(result);
    assertEquals(customerId, result.customerId());
    assertEquals(assetName, result.assetName());
    assertEquals(initialSize + addSize, result.size());
    assertEquals(initialSize + addSize, result.usableSize());
  }
}
