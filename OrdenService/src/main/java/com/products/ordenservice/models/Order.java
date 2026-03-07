package com.products.ordenservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "ordenes") // Nombre de la colección en Mongo
public class Order {
    @Id
    private String id; // ID interno de MongoDB

    private String orderCode; // Nota: Mantengo el typo 'ordeCode' de tu imagen
    private String orderDate;
    private Double totalAmount;
    private String status;
    private String userId;

    // Relación anidada
    private List<ProductItem> products;
}