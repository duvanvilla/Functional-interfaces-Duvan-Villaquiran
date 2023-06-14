package com.sales.functional.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Product {
    private String name;
    private Double price;
    private List<String> tags;
    private Integer quantity;
}
