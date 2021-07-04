package com.vehicle.registration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonVehicleRequest {
    String firstName;
    String lastName;
    String registrationNumber;
}
