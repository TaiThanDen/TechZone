package org.asm_java6.asm_java6.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Items", schema = "dbo")
@Getter
@Setter
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    private double price;
    private int qty;
    private String image;
    private String seller;
    private String category;
    private double discount;
    private int sold;
}
