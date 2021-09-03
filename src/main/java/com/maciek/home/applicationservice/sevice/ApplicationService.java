package com.maciek.home.applicationservice.sevice;

import com.maciek.home.applicationservice.model.Application;

import java.rmi.ServerException;
import java.util.List;

public interface ApplicationService {

    Application findById(Long id);

    List<Application> findAll();

    boolean createNew(Application newApplication) throws ServerException;

    boolean deleteById(Long id);

    boolean updateById(Long id, Application rejectedApplication);

    boolean rejectById(Long id, Application newApplication);

    boolean verifyById(Long id);

    boolean acceptById(Long id);

    boolean publishById(Long id);
}
