package com.maciek.home.applicationservice.sevice;

import com.maciek.home.applicationservice.model.Application;
import com.maciek.home.applicationservice.model.State;
import com.maciek.home.applicationservice.repositories.ApplicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.rmi.ServerException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.maciek.home.applicationservice.model.State.ACCEPTED;
import static com.maciek.home.applicationservice.model.State.CREATED;
import static com.maciek.home.applicationservice.model.State.PUBLISHED;
import static com.maciek.home.applicationservice.model.State.REJECTED;
import static com.maciek.home.applicationservice.model.State.VERIFIED;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    public ApplicationServiceImpl(ApplicationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Application findById(Long id) {

        Optional<Application> application = repository.findById(id);

        if (application.isPresent()) {
            return application.get();
        }
        //TODO: Create custom exception !
        throw new RuntimeException("Cannot find application with id: " + id);
    }

    @Override
    public List<Application> findAll() {

        return StreamSupport.stream(repository.findAll().spliterator(), false).collect(Collectors.toList());
    }


    @Override
    public boolean createNew(Application newApplication) throws ServerException {

        logger.info("Trying to post element with empty Name and Content");
        if (newApplication != null) {
            repository.save(newApplication);
            logger.info("New application submitted: {}", newApplication);
            return true;
        } else {
            logger.warn("Cannot create new application, because body is null");
            throw new ServerException("New application is null");
        }
    }

    @Override
    public boolean deleteById(Long id) {
        Optional<Application> optionalApplication = repository.findById(id);
        if (optionalApplication.isPresent()) {
            State appState = optionalApplication.get().getState();
            if (appState.equals(CREATED)) {
                repository.deleteById(id);
                return true;
            } else {
                logger.warn("Cannot remove application with id: {}, because it's in state: {}", id, appState);
            }
        }
        return false;
    }


    @Override
    public boolean updateById(Long id, Application newApplication) {
        Optional<Application> optionalApplication = repository.findById(id);
        if (optionalApplication.isPresent()) {
            Application application = optionalApplication.get();
            State appState = application.getState();
            if (appState.equals(CREATED) || appState.equals(VERIFIED)) {
                if (!newApplication.getContent().isEmpty()) {
                    application.setContent(newApplication.getContent());
                }
                if (!newApplication.getName().isEmpty()) {
                    application.setName(newApplication.getName());
                }
                repository.save(application);
                return true;
            } else {
                logger.warn("Cannot update application with id: {}, because it's in state: {}", id, appState);
            }
        }
        return false;
    }

    @Override
    public boolean rejectById(Long id, Application newApplication) {
        Optional<Application> optionalApplication = repository.findById(id);
        if (optionalApplication.isPresent()) {
            if (newApplication != null && newApplication.getRejectionReason() != null && !newApplication.getRejectionReason().isBlank()) {


                Application application = optionalApplication.get();
                State appState = application.getState();
                if (appState.equals(VERIFIED) || appState.equals(ACCEPTED)) {
                    application.setState(REJECTED);
                    application.setRejectionReason(newApplication.getRejectionReason());
                    repository.save(application);
                    return true;
                } else {
                    logger.warn("Cannot reject application with id: {}, because it's in state: {}", id, appState);
                    return false;
                }
            } else {
                logger.warn("Cannot reject application with id: {}, because Request body is empty", id);
            }
        }
        return false;
    }

    @Override
    public boolean verifyById(Long id) {
        Optional<Application> optionalApplication = repository.findById(id);
        if (optionalApplication.isPresent()) {
            Application application = optionalApplication.get();
            State appState = application.getState();
            if (appState.equals(CREATED)) {
                application.setState(VERIFIED);
                repository.save(application);
                return true;
            } else {
                logger.warn("Cannot verify application with id: {}, because it's in state: {}", id, appState);
            }
        }
        return false;
    }

    @Override
    public boolean acceptById(Long id) {
        Optional<Application> optionalApplication = repository.findById(id);
        if (optionalApplication.isPresent()) {
            Application application = optionalApplication.get();
            State appState = application.getState();
            if (appState.equals(VERIFIED)) {
                application.setState(ACCEPTED);
                repository.save(application);
                return true;
            } else {
                logger.warn("Cannot accept application with id: {}, because it's not in state {}", id, VERIFIED);
            }
        }
        return false;
    }

    @Override
    public boolean publishById(Long id) {
        Optional<Application> optionalApplication = repository.findById(id);
        if (optionalApplication.isPresent()) {
            Application application = optionalApplication.get();
            State appState = application.getState();
            if (appState.equals(ACCEPTED)) {
                application.setState(PUBLISHED);
                repository.save(application);
                return true;
            } else {
                logger.warn("Cannot publish application with id: {}, because it's not in state {}", id, ACCEPTED);
            }
        }
        return false;    }

}
