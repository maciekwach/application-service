package com.maciek.home.applicationservice.repositories;

import com.maciek.home.applicationservice.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {



    // Enabling static ORDER BY for a query
    List<Application> findAllByOrderByStateAsc(Pageable pageable);
    List<Application> findAllByOrderByStateDesc(Pageable pageable);


    // Enabling static ORDER BY for a query
    List<Application> findAllByOrderByNameAsc(Pageable pageable);
    List<Application> findAllByOrderByNameDesc(Pageable pageable);

}
