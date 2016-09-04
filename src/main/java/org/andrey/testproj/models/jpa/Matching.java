package org.andrey.testproj.models.jpa;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by andrey on 31.08.2016.
 */
@Data
@Entity
public class Matching {
    @Id
    private int matchingId;
    private int topPricedCount;
}
