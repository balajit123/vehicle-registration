package com.vehicle.registration.mapper;

import com.vehicle.registration.dto.VehicleRequest;
import com.vehicle.registration.entity.Vehicle;

import java.util.function.Function;

public class VehicleMapper implements Function<VehicleRequest, Vehicle> {

    @Override
    public Vehicle apply(VehicleRequest vehicleRequest) {
        return Vehicle.builder()
                .registrationNumber(vehicleRequest.getRegistrationNumber())
                .build();
    }
}
