package com.inghubs.brokerage.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.brokerage.dto.AssetDto;
import com.inghubs.brokerage.dto.request.AddAssetRequest;
import com.inghubs.brokerage.service.AssetService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AssetControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private AssetService assetService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  @WithMockUser(
      username = "dogukan",
      roles = {"ADMIN"})
  void testGetAssets() throws Exception {
    Long customerId = 1L;
    AssetDto assetDto =
        AssetDto.builder()
            .customerId(customerId)
            .assetName("TestAsset")
            .size(100.0)
            .usableSize(100.0)
            .build();
    List<AssetDto> assetDtos = List.of(assetDto);

    when(assetService.listAssets(customerId)).thenReturn(assetDtos);

    mockMvc
        .perform(get("/v1/assets/{customerId}", customerId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].customerId").value(customerId))
        .andExpect(jsonPath("$[0].assetName").value("TestAsset"))
        .andExpect(jsonPath("$[0].size").value(100.0))
        .andExpect(jsonPath("$[0].usableSize").value(100.0));
  }

  @Test
  @WithMockUser(
      username = "dogukan",
      roles = {"ADMIN"})
  void testAddAsset() throws Exception {
    AddAssetRequest addAssetRequest = new AddAssetRequest(1L, "NewAsset", 50.0);
    AssetDto assetDto =
        AssetDto.builder().customerId(1L).assetName("NewAsset").size(50.0).usableSize(50.0).build();

    when(assetService.addAsset(any(AddAssetRequest.class))).thenReturn(assetDto);

    mockMvc
        .perform(
            post("/v1/assets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addAssetRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.customerId").value(1L))
        .andExpect(jsonPath("$.assetName").value("NewAsset"))
        .andExpect(jsonPath("$.size").value(50.0))
        .andExpect(jsonPath("$.usableSize").value(50.0));
  }
}
