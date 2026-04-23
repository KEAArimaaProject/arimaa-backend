package com.example.arimaabackend.config;

import jakarta.persistence.EntityManagerFactory;

import org.neo4j.driver.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;

/**
 * Migration uses both JPA reads and Neo4j writes. Declaring any {@code TransactionManager} causes
 * Spring Boot's {@code @ConditionalOnMissingBean(TransactionManager.class)} to skip auto-config for
 * the other store, so both managers must be registered explicitly here.
 */
@Configuration
@Profile("migration")
public class Neo4jTransactionManagerConfiguration {

    @Bean(name = "transactionManager")
    @Primary
    JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = "neo4jTransactionManager")
    Neo4jTransactionManager neo4jTransactionManager(Driver driver) {
        return new Neo4jTransactionManager(driver);
    }
}
