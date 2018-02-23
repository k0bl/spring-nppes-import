package com.k0bl.nppestomysql.jobs;

import com.k0bl.nppestomysql.listener.JobCompletionNotificationListener;
import com.k0bl.nppestomysql.model.Taxonomy;
import com.k0bl.nppestomysql.processor.TaxonomyItemProcessor;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private DataSource dataSource;


    // tag::readerwriterprocessor[]
    @Bean
    public FlatFileItemReader<Taxonomy> reader() {
        FlatFileItemReader<Taxonomy> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("taxonomies.csv"));
        reader.setLineMapper(new DefaultLineMapper<Taxonomy>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[]{"code", "grouping", "classification", "specialization"});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Taxonomy>() {{
                setTargetType(Taxonomy.class);
            }});
        }});
        return reader;
    }

    @Bean
    public TaxonomyItemProcessor processor() {
        return new TaxonomyItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Taxonomy> writer() {
        JdbcBatchItemWriter<Taxonomy> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(
                new BeanPropertyItemSqlParameterSourceProvider<Taxonomy>());
        writer.setSql("INSERT INTO taxonomies (code, grouping, classification, specialization) VALUES (:code, :grouping, :classification, :specialization)");
        writer.setDataSource(dataSource);
        return writer;
    }
    // end::readerwriterprocessor[]


    // tag::jobstep[]
    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get("importUserJob").incrementer(new RunIdIncrementer())
                .listener(listener).flow(step1()).end().build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1").<Taxonomy, Taxonomy>chunk(10).reader(reader())
                .processor(processor()).writer(writer()).build();
    }
    // end::jobstep[]
}
