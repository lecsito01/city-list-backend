package com.example.citylist.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findByNameContainsIgnoreCase(String name);

    Page<City> findByNameContainsIgnoreCase(String name, Pageable pageable);


}
