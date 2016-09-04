package org.andrey.testproj.mappers;

import org.andrey.testproj.models.jpa.DEntity;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

/**
 * Created by andrey on 31.08.2016.
 */
public class EntityMapper extends DefaultLineMapper<DEntity> {
    public EntityMapper() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(new String[] { "id", "price", "currency", "quantity", "matchingId" });
        setLineTokenizer(tokenizer);
        setFieldSetMapper(new EntityFieldSetMapper());
    }

    private class EntityFieldSetMapper implements FieldSetMapper<DEntity> {
        @Override
        public DEntity mapFieldSet(FieldSet fieldSet) throws BindException {
            Integer id = fieldSet.readInt("id");
            Double price = fieldSet.readDouble("price");
            String currency = fieldSet.readString("currency");
            Integer quantity = fieldSet.readInt("quantity");
            Integer matchingId = fieldSet.readInt("matchingId");

            DEntity entity = new DEntity();
            entity.setId(id);
            entity.setPrice(price);
            entity.setCurrency(currency);
            entity.setQuantity(quantity);
            entity.setMatchingId(matchingId);
            return entity;
        }
    }
}