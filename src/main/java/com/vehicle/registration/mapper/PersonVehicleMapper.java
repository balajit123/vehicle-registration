package com.vehicle.registration.mapper;

import com.vehicle.registration.dto.PersonVehicleRequest;
import com.vehicle.registration.entity.PersonVehicle;

import java.util.function.Function;

public class PersonVehicleMapper implements Function<PersonVehicleRequest, PersonVehicle> {
    @Override
    public PersonVehicle apply(PersonVehicleRequest personVehicleRequest) {
        return PersonVehicle.builder()
                .registrationNumber(personVehicleRequest.getRegistrationNumber())
                .build();
    }
}
