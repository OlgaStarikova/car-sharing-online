package com.example.carsharingonline.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@SQLDelete(sql = "UPDATE cars SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String model;
    @Column(nullable = false)
    private String brand;
    @Column(nullable = false, unique = true, columnDefinition =
            "enum('SEDAN', 'SUV', 'HATCHBACK', 'UNIVERSAL')")
    @Enumerated(EnumType.STRING)
    private CarBodyType carBodyType = CarBodyType.SEDAN;
    @Column(nullable = false)
    private int inventory = 1;
    @Column(nullable = false)
    private BigDecimal daylyFee;
    @Column(nullable = false)
    private boolean isDeleted = false;

    public enum CarBodyType {
        SEDAN,
        SUV,
        HATCHBACK,
        UNIVERSAL
    }

}
