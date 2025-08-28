package com.example.carsharingonline.config;

import org.testcontainers.containers.MySQLContainer;

public class CustomMySqlContainer extends MySQLContainer<CustomMySqlContainer> {
    private static final String DB_IMAGE = "mysql:8";

    private static CustomMySqlContainer mySqlContainer;

    public CustomMySqlContainer() {
        super(DB_IMAGE);
        this.withCommand("mysqld", "--character-set-server=utf8mb4",
                "--collation-server=utf8mb4_unicode_ci", "--time-zone=Europe/Kiev");
    }

    public static synchronized CustomMySqlContainer getInstance() {
        if (mySqlContainer == null) {
            mySqlContainer = new CustomMySqlContainer();
        }
        return mySqlContainer;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("TEST_DB_URL", mySqlContainer.getJdbcUrl());
        System.setProperty("TEST_DB_USER", mySqlContainer.getUsername());
        System.setProperty("TEST_DB_PASSWORD", mySqlContainer.getPassword());
    }

    @Override
    public void stop() {
    }
}
