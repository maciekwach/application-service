package com.maciek.home.applicationservice.repositories;

import com.maciek.home.applicationservice.model.Application;
import org.springframework.data.repository.CrudRepository;

public interface ApplicationRepository extends CrudRepository<Application, Long> {

}
