package com.sales.functional.entities;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Customer {
    private String email;
    private String gender;
    private Integer age;
    private Integer satisfaction;
}
