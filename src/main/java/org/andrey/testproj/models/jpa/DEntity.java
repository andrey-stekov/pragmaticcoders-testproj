package org.andrey.testproj.models.jpa;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by andrey on 31.08.2016.
 */
@Data
@Entity
@Table(name = "entity",
       indexes = {
               @Index(name = "currency_inndex", columnList = "currency", unique = false),
               @Index(name = "matching_index", columnList = "matchingId", unique = false)
       })
public class DEntity {
    @Id
    private int id;
    private double price;
    @Column(nullable = false, length = 10)
    private String currency;
    private int quantity;
    private int matchingId;
}
