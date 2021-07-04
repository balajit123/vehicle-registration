package com.vehicle.registration.mapper;

import com.vehicle.registration.dto.PersonVehicleRequest;
import com.vehicle.registration.dto.PersonVehicles;
import com.vehicle.registration.dto.VehicleResponse;
import com.vehicle.registration.entity.Person;
import com.vehicle.registration.entity.PersonVehicle;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class PersonVehiclesResponseMapper implements Function<Person, PersonVehicles> {
    
    @Override
    public PersonVehicles apply(Person person) {
        return PersonVehicles.builder()
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .vehicles(buildVehicles(person))
                .build();
    }

    private Set<VehicleResponse> buildVehicles(Person person) {
        Set<VehicleResponse> vehicles = new HashSet<>();
        person.getPersonVehicle().forEach(personVehicle -> {
            vehicles.add(VehicleResponse.builder()
                    .registrationNumber(personVehicle.getRegistrationNumber())
                    .build());
        });
        return vehicles;
    }
}
