package com.vehicle.registration.repository;

import com.vehicle.registration.entity.PersonVehicle;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonVehicleRepository extends CrudRepository<PersonVehicle, Long> {
    PersonVehicle findByPersonIdAndRegistrationNumber(Long personId, String registrationNumber);

    @Modifying
    @Query(value = "delete from vr.person_vehicle p " +
            "where p.id = :id ", nativeQuery = true)
    int deletePersonVehicleLink(@Param("id") Long id);
}
