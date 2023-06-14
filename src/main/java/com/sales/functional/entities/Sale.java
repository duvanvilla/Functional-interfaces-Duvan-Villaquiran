package com.sales.functional.entities;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;


@NoArgsConstructor
@Getter
@Setter
public class Sale {
    private Date saleDate;
    private List<Product> items;
    private String location;
    private Boolean couponUsed;
    private String purchasedMethod;
    private Customer customer;
    private Double total;

    @Override
    public String toString() {
        return "Sale{" +
                "\nsaleDate=" + saleDate +
                ", \nitems=" + items +
                ", \nlocation='" + location + '\'' +
                ", \ncouponUsed=" + couponUsed +
                ", \npurchasedMethod='" + purchasedMethod + '\'' +
                ", \ncustomer=" + customer +
                ", \ntotal=" + total +
                '}';
    }
}
