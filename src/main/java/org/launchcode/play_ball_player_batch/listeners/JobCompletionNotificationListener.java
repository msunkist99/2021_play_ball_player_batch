package org.launchcode.play_ball_player_batch.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

//            List<String> players = jdbcTemplate.queryForList(
//                    "SELECT id FROM player",
//                    String.class);
//            for (String playerId : players) {
//                log.info("Found player - " + playerId);
//            }

            int count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM player WHERE year = 1982",
                    Integer.class);
            log.info("1982 player count - " + count);

            count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM player WHERE year = 2015",
                    Integer.class);
            log.info("2015 player count - " + count);


        }
    }
}
