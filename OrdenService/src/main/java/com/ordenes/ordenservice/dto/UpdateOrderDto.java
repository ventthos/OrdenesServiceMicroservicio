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
public class UpdateOrderDto {
    private String orderCode;
    private String orderDate;
    private Double totalAmount;
    private String status;
    private String userId;
    private List<ProductItem> products;
}
