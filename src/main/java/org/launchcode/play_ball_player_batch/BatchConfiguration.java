package org.launchcode.play_ball_player_batch;

import org.launchcode.play_ball_player_batch.fieldSetMappers.PlayerFieldSetMapper;
import org.launchcode.play_ball_player_batch.listeners.JobCompletionNotificationListener;
import org.launchcode.play_ball_player_batch.models.Player;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing              // adds many critical beans that support jobs
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Value("classpath*:2015/*.ROS")
    private Resource[] inputFiles;

    @Bean
    public MultiResourceItemReader<Player> multiResourceItemreader() {
        MultiResourceItemReader<Player> reader = new MultiResourceItemReader<>();
        reader.setDelegate(reader());
        reader.setResources(inputFiles);
        return reader;
    }

    @Bean
    // create an ItemReader that reads in a flat file
    // https://www.programcreek.com/java-api-examples/?api=org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
    public FlatFileItemReader<Player> reader() {
        DefaultLineMapper<Player> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer(","));
        lineMapper.setFieldSetMapper(new PlayerFieldSetMapper());

        return new FlatFileItemReaderBuilder<Player>()
                .name("playerItemReader")
                .lineMapper(lineMapper)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Player> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Player>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO player (id, last_name, first_name, bats, pitches, team_id, year) VALUES " +
                        "(:id, :lastName, :firstName, :bats, :pitches, :teamId, 2015);")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<Player> writer) {
        return stepBuilderFactory.get("step1")
                .<Player, Player> chunk(10)
                .reader(multiResourceItemreader())
                //.processor(processor())
                .writer(writer)
                .build();
    }

    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }
}
