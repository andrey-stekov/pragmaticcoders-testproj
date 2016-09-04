package org.andrey.testproj.models.jpa;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by andrey on 29.08.2016.
 */
@Data
@Entity(name = "currency")
public class Currency {
    @Id
    private String currency;
    private double ratio;
}
