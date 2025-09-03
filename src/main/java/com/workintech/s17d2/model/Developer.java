package com.workintech.s17d2.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Developer {
    private Integer id;
    private String name;
    private Double salary;
    private Experience experience;
}