package com.vehicle.registration.repository;

import com.vehicle.registration.entity.Person;
import com.vehicle.registration.entity.Vehicle;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends CrudRepository<Vehicle, Long> {
    Vehicle findByRegistrationNumber(String s);
}
