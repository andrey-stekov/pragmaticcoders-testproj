package org.andrey.testproj;

import org.andrey.testproj.mappers.CurrenciesMapper;
import org.andrey.testproj.mappers.EntityMapper;
import org.andrey.testproj.mappers.MatchingMapper;
import org.andrey.testproj.models.jpa.Currency;
import org.andrey.testproj.models.jpa.DEntity;
import org.andrey.testproj.models.jpa.Matching;
import org.andrey.testproj.models.TopProduct;
import org.andrey.testproj.writer.TopProductHeaderWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.batch.item.ItemProcessor;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.math.BigInteger;

/**
 * Created by andrey on 30.08.2016.
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {;
    private static final int LINES_TO_SKIP = 1;
    private static final int CHUNK_SIZE = 10;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private EntityManagerFactory emFactory;

    @Autowired
    private EntityManager em;

    @Bean
    public ItemReader<Currency> currenciesReader() {
        FlatFileItemReader<Currency> reader = new FlatFileItemReader<>();
        reader.setLinesToSkip(LINES_TO_SKIP);
        reader.setResource(new ClassPathResource("currencies.csv"));
        reader.setLineMapper(new CurrenciesMapper());
        return reader;
    }

    @Bean
    public ItemReader<Matching> matchingsReader() {
        FlatFileItemReader<Matching> reader = new FlatFileItemReader<>();
        reader.setLinesToSkip(LINES_TO_SKIP);
        reader.setResource(new ClassPathResource("matchings.csv"));
        reader.setLineMapper(new MatchingMapper());
        return reader;
    }

    @Bean
    public ItemReader<DEntity> entityReader() {
        FlatFileItemReader<DEntity> reader = new FlatFileItemReader<>();
        reader.setLinesToSkip(LINES_TO_SKIP);
        reader.setResource(new ClassPathResource("data.csv"));
        reader.setLineMapper(new EntityMapper());
        return reader;
    }

    @Bean
    public ItemReader<Matching> matchingsJPAReader() throws Exception {
        JpaPagingItemReader<Matching> reader = new JpaPagingItemReader<>();
        reader.setQueryString("select m from Matching m");
        reader.setEntityManagerFactory(emFactory);
        reader.setPageSize(CHUNK_SIZE);
        reader.afterPropertiesSet();
        reader.setSaveState(true);
        return reader;
    }

    @Bean
    public ItemProcessor<Matching, TopProduct> processor() {
        return matching -> {
            Query query = em.createNativeQuery(
                  " select "
                + "   sum(e.price * e.quantity * c.ratio), "
                + "   avg(e.price * e.quantity * c.ratio), "
                + "   ((select count(*) from entity) - :topPricedCount) "
                + " from entity e"
                + " join currency c on e.currency = c.currency"
                + " where e.id in ("
                + "   select id from entity e"
                + "   join currency c on e.currency = c.currency"
                + "   where matching_id = :matchingId"
                + "   order by e.price * e.quantity * c.ratio desc"
                + "   limit :topPricedCount"
                + " )"
            );
            Object[] result = (Object[]) query
                    .setParameter("matchingId", matching.getMatchingId())
                    .setParameter("topPricedCount", matching.getTopPricedCount())
                    .getSingleResult();
            int ignored = ((BigInteger) result[2]).intValue();
            ignored = ignored < 0 ? 0 : ignored;
            return TopProduct
                    .builder()
                    .matchingId(matching.getMatchingId())
                    .totalPrice((double) result[0])
                    .avgPrice((double) result[1])
                    .ignoredProductsCount(ignored)
                    .build();
        };
    }

    @Bean
    public FlatFileItemWriter<TopProduct> topProductWriter(){
        FlatFileItemWriter<TopProduct> writer = new FlatFileItemWriter<>();
        writer.setHeaderCallback(new TopProductHeaderWriter());
        DelimitedLineAggregator<TopProduct> aggregator = new DelimitedLineAggregator<>();
        BeanWrapperFieldExtractor<TopProduct> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[]{"matchingId", "totalPrice", "avgPrice", "ignoredProductsCount"});
        aggregator.setDelimiter(",");
        aggregator.setFieldExtractor(extractor);
        writer.setLineAggregator(aggregator);
        String exportFilePath = "top_products.csv";
        writer.setResource(new FileSystemResource(exportFilePath));
        return writer;
    }

    @Bean
    public ItemWriter<Currency> currencyWriter() {
        JpaItemWriter<Currency> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emFactory);
        return writer;
    }

    @Bean
    public ItemWriter<Matching> matchingWriter() {
        JpaItemWriter<Matching> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emFactory);
        return writer;
    }

    @Bean
    public ItemWriter<DEntity> entityWriter() {
        JpaItemWriter<DEntity> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(emFactory);
        return writer;
    }

    @Bean
    public Job importUserJob() throws Exception {
        return jobBuilderFactory.get("processTopProductsJob")
                .incrementer(new RunIdIncrementer())
                .flow(currencyImportStep())
                .next(matchingImportStep())
                .next(entityImportStep())
                .next(topProductsStep())
                .end()
                .build();
    }

    @Bean
    public Step currencyImportStep() {
        return stepBuilderFactory.get("currencyImportStep")
                .<Currency, Currency> chunk(CHUNK_SIZE)
                .reader(currenciesReader())
                .writer(currencyWriter())
                .build();
    }

    @Bean
    public Step matchingImportStep() {
        return stepBuilderFactory.get("matchingImportStep")
                .<Matching, Matching> chunk(CHUNK_SIZE)
                .reader(matchingsReader())
                .writer(matchingWriter())
                .build();
    }

    @Bean
    public Step entityImportStep() {
        return stepBuilderFactory.get("entityImportStep")
                .<DEntity, DEntity> chunk(CHUNK_SIZE)
                .reader(entityReader())
                .writer(entityWriter())
                .build();
    }

    @Bean
    public Step topProductsStep() throws Exception {
        return stepBuilderFactory.get("topProductsStep")
                .<Matching, TopProduct> chunk(CHUNK_SIZE)
                .reader(matchingsJPAReader())
                .processor(processor())
                .writer(topProductWriter())
                .build();
    }
}
