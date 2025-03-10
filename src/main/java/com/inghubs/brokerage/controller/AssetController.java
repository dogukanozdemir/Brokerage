package com.inghubs.brokerage.controller;

import com.inghubs.brokerage.dto.AssetDto;
import com.inghubs.brokerage.dto.request.AddAssetRequest;
import com.inghubs.brokerage.service.AssetService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/assets")
@RequiredArgsConstructor
public class AssetController {

  private final AssetService assetService;

  @GetMapping("/{customerId}")
  public ResponseEntity<List<AssetDto>> getAssets(@PathVariable Long customerId) {
    return ResponseEntity.ok(assetService.listAssets(customerId));
  }

  @PostMapping
  public ResponseEntity<AssetDto> addAsset(@RequestBody @Valid AddAssetRequest dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(assetService.addAsset(dto));
  }
}
