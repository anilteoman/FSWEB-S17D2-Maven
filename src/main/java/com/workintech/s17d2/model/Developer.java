package com.workintech.s17d2.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor // tüm alanları set eden constructor
@NoArgsConstructor
public class Developer {
    private Integer id;
    private String name;
    private Double salary;      // Net maaş tutulacak (vergiden sonra)
    private Experience experience;
}