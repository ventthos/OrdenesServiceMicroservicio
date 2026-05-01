package com.ordenes.ordenservice.dto;

import com.ordenes.ordenservice.models.ProductItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryChangeDto {
    String orderId;
    List<ProductItem> oldProducts;
    List<ProductItem> newProducts;
}
