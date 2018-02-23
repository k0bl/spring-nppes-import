package com.k0bl.nppestomysql.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Taxonomy {

    private String code;
    private String grouping;
    private String classification;
    private String specialization;

}
