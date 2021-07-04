package com.vehicle.registration.repository;

import com.vehicle.registration.entity.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {
    Person findByLastName(String name);
    Person findByFirstNameAndLastName(String firstName, String lastName);
}
