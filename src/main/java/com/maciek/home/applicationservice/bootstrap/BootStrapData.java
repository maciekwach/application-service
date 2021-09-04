package com.maciek.home.applicationservice.bootstrap;

import com.maciek.home.applicationservice.model.Application;
import com.maciek.home.applicationservice.model.State;
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
        generateInitialApplications(7);

        logger.info("Number of created applications: {}", appRepository.count());

    }

    private void generateInitialApplications(int max) {
        int iterator = 0;
        while (iterator < max) {
            Application application = Application.builder()
                    .name("New Company application")
                    .content("Application for establishing a company")
                    .build();

            if(iterator > 3){
                application.setState(State.VERIFIED);
            }
            logger.debug("Created app {}", application);
            appRepository.save(application);

            application = Application.builder()
                    .name("Renew Id-Card application")
                    .content("Application for a new ID card")
                    .build();

            logger.debug("Created app {}", application);
            appRepository.save(application);
            iterator += 1;
        }
    }
}