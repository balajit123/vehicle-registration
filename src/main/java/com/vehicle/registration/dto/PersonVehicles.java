package com.vehicle.registration.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class PersonVehicles {
    private String firstName;
    private String lastName;
    private Set<VehicleResponse> vehicles;
}
