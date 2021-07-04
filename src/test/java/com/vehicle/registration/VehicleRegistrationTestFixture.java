package com.vehicle.registration;

import com.vehicle.registration.dto.PersonRequest;
import com.vehicle.registration.dto.PersonVehicleRequest;
import com.vehicle.registration.dto.VehicleRequest;

public class VehicleRegistrationTestFixture {

    public static PersonRequest buildPersonDTO(String firstName, String lastName) {
        return PersonRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }

    public static VehicleRequest buildVehicleDTO(String registrationNumber) {
        return VehicleRequest.builder()
                .registrationNumber(registrationNumber)
                .build();
    }

    public static PersonVehicleRequest buildPersonVehicleDTO(
            String firstName, String lastName, String registrationNumber) {
        return PersonVehicleRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .registrationNumber(registrationNumber)
                .build();
    }
}
