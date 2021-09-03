package com.maciek.home.applicationservice.bootstrap;

import com.maciek.home.applicationservice.model.Application;
import com.maciek.home.applicationservice.repositories.ApplicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BootStrapData implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(BootStrapData.class);

    private final ApplicationRepository appRepository;

    //    @Autowired
    public BootStrapData(ApplicationRepository appRepository) {
        this.appRepository = appRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Start BootStrapData");
        Application companyApp = Application.builder()
                                         .name("New Company application")
                                         .content("Application for establishing a company")
                                         .build();

        logger.info("Created app {}", companyApp);

        appRepository.save(companyApp);
        logger.info("Number of created applications: {}", appRepository.count());

        Application idCardApp = Application.builder()
                                        .name("Renew Id-Card application")
                                        .content("Application for a new ID card")
                                        .build();

        logger.info("Created app {}", idCardApp);
        appRepository.save(idCardApp);
        logger.info("Number of created applications: {}", appRepository.count());

    }
}