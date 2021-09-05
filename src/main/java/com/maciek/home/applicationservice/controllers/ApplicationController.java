package com.maciek.home.applicationservice.controllers;

import com.maciek.home.applicationservice.model.Application;
import com.maciek.home.applicationservice.sevice.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.rmi.ServerException;
import java.util.List;

//@Slf4j
@RestController
@RequestMapping(value = "/applications")
public class ApplicationController {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    private final ApplicationService service;

    @Autowired
    public ApplicationController(ApplicationService service) {
        this.service = service;
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Application>> getAll() {
        logger.info("Getting all applications");
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = {"/name/{sort}", "/name/{sort}/{page}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Application>> allByName(@PathVariable String sort, @PathVariable(required = false) Integer page) {
        logger.info("Trying to get applications sorted by Name");
        page = page != null ? page : 0;
        List<Application> allByOrderByName = service.findAllOrderByName(sort, page);
        return new ResponseEntity<>(allByOrderByName, HttpStatus.OK);

    }

    @GetMapping(value = {"state/{sort}", "/state/{sort}/{page}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Application>> allByState(@PathVariable String sort, @PathVariable(required = false) Integer page) {
        logger.info("Trying to get applications sorted by State");
        page = page != null ? page : 0;
        List<Application> allByOrderByName = service.findAllOrderByState(sort, page);
        return new ResponseEntity<>(allByOrderByName, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Application> getById(@PathVariable long id) {
        logger.info("Trying to get application with id: {}", id);
        Application application = service.findById(id);
        return new ResponseEntity<>(application, HttpStatus.OK);
    }

    @PostMapping(value = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Application> create(@RequestBody Application newApplication) throws ServerException {
        if (newApplication != null) {
            logger.info("Creating new application: {}", newApplication);
            if (service.createNew(newApplication)) {
                return new ResponseEntity<>(newApplication, HttpStatus.OK);
            }
        }
        logger.warn("Cannot create new application, because body is null");
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<Application> updateById(@PathVariable long id, @RequestBody Application application) {
        logger.info("Trying to update name and content of application with id: {}", id);

        if (service.updateById(id, application)) {
            logger.info("Updated application: {}", application);
            return new ResponseEntity<>(application, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping(value = "/reject/{id}")
    public ResponseEntity<Application> rejectById(@PathVariable long id, @RequestBody Application application) {
        logger.info("Trying to reject name and content of application with id: {}", id);

        if (service.rejectById(id, application)) {
            logger.info("Application with id: {} is rejected", id);
            return new ResponseEntity<>(application, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping(value = "/verify/{id}")
    public ResponseEntity<Application> verifyById(@PathVariable long id) {
        logger.info("Verifying application with id: {}", id);
        if (service.verifyById(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.unprocessableEntity().build();
    }

    @DeleteMapping(value = "/remove/{id}")
    public ResponseEntity<Long> deleteById(@PathVariable long id) {
        logger.info("Trying to remove application with id: {}", id);
        if (service.deleteById(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(value = "/accept/{id}")
    public ResponseEntity<Application> acceptById(@PathVariable long id) {
        logger.info("Accepting application with id: {}", id);
        if (service.acceptById(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.unprocessableEntity().build();
    }

    @PutMapping(value = "/publish/{id}")
    public ResponseEntity<Application> publishById(@PathVariable long id) {
        logger.info("Publishing application with id: {}", id);
        if (service.publishById(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.unprocessableEntity().build();
    }

}