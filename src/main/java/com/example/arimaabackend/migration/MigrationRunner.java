package com.example.arimaabackend.migration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("migration")
public class MigrationRunner implements CommandLineRunner {

    private final UserMigrationService userMigrationService;

    public MigrationRunner(UserMigrationService userMigrationService) {
        this.userMigrationService = userMigrationService;
    }

    @Override
    public void run(String... args) {
        userMigrationService.migrateAllUsers();
    }
}

