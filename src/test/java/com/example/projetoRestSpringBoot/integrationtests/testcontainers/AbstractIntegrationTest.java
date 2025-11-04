package com.example.projetoRestSpringBoot.integrationtests.testcontainers;


import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.lifecycle.Startables;

import java.util.Map;
import java.util.stream.Stream;

@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
public class AbstractIntegrationTest {


    static class Initializer implements ApplicationContextInitializer <ConfigurableApplicationContext> {


        static MySQLContainer <?> mysql = new MySQLContainer<>("mysql:9.1.0");

        private static void startContainers() {
            Startables.deepStart(Stream.of(mysql)).join();
        }




        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            startContainers();
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            MapPropertySource testContainers = new MapPropertySource("testContainers",
                    (Map)CreateConnectionConfiguration());
            
            environment.getPropertySources().addFirst(testContainers);
        }

        private static Map<String, String> CreateConnectionConfiguration() {
            return Map.of(
            "spring.datasource.url", mysql.getJdbcUrl(),
            "spring.datasource.username", mysql.getUsername(),
            "spring.datasource.password", mysql.getPassword());
        }


    }
}
