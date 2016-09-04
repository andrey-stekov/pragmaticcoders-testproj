package org.andrey.testproj.mappers;

import org.andrey.testproj.models.jpa.Matching;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

/**
 * Created by andrey on 31.08.2016.
 */
public class MatchingMapper extends DefaultLineMapper<Matching> {
    public MatchingMapper() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(new String[] { "matchingId", "topPricedCount" });
        setLineTokenizer(tokenizer);
        setFieldSetMapper(new MatchingFieldSetMapper());
    }

    private class MatchingFieldSetMapper implements FieldSetMapper<Matching> {
        @Override
        public Matching mapFieldSet(FieldSet fieldSet) throws BindException {
            Integer matchingId = fieldSet.readInt("matchingId");
            Integer topPricedCount = fieldSet.readInt("topPricedCount");

            Matching matching = new Matching();
            matching.setMatchingId(matchingId);
            matching.setTopPricedCount(topPricedCount);
            return matching;
        }
    }
}