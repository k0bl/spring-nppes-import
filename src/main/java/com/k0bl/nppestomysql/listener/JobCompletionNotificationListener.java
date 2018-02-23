package com.k0bl.nppestomysql.listener;

import com.k0bl.nppestomysql.model.Taxonomy;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private final JdbcTemplate jdbcTemplate;

    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

            List<Taxonomy> results = jdbcTemplate
                    .query("SELECT code, grouping, classification, specialization FROM taxonomies", new RowMapper<Taxonomy>() {
                        @Override
                        public Taxonomy mapRow(ResultSet rs, int row) throws SQLException {
                            return new Taxonomy(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
                        }
                    });

            for (Taxonomy Taxonomy : results) {
                log.info("Found <" + Taxonomy + "> in the database.");
            }

        }
    }
}