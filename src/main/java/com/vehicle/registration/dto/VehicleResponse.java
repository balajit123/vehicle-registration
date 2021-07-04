package com.vehicle.registration.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleResponse {
    private String registrationNumber;
}
