package org.andrey.testproj.mappers;

import org.andrey.testproj.models.jpa.Currency;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

/**
 * Created by andrey on 30.08.2016.
 */
public class CurrenciesMapper extends DefaultLineMapper<Currency> {
    public CurrenciesMapper() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(new String[] { "currency", "ratio" });
        setLineTokenizer(tokenizer);
        setFieldSetMapper(new CurrencyFieldSetMapper());
    }

    private class CurrencyFieldSetMapper implements FieldSetMapper<Currency> {
        @Override
        public Currency mapFieldSet(FieldSet fieldSet) throws BindException {
            String currencyName = fieldSet.readString("currency");
            Double ratio = fieldSet.readDouble("ratio");
            Currency currency = new Currency();
            currency.setCurrency(currencyName);
            currency.setRatio(ratio);
            return currency;
        }
    }
}
