package org.andrey.testproj.writer;

import org.springframework.batch.item.file.FlatFileHeaderCallback;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by andrey on 01.09.2016.
 */
public class TopProductHeaderWriter implements FlatFileHeaderCallback {
    public static final String HEADER = "matching_id,total_price,avg_price,ignored_products_count";

    @Override
    public void writeHeader(Writer writer) throws IOException {
        writer.write(HEADER);
    }
}