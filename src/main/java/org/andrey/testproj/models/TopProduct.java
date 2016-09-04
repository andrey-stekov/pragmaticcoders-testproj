package org.andrey.testproj.models;

import lombok.Builder;
import lombok.Data;

/**
 * Created by andrey on 01.09.2016.
 */
@Data
@Builder
public class TopProduct {
    private int matchingId;
    private double totalPrice;
    private double avgPrice;
    private int ignoredProductsCount;
}
