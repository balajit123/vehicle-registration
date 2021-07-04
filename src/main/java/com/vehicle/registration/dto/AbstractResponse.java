package com.vehicle.registration.dto;

import com.vehicle.registration.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
public class AbstractResponse {
    private ResponseStatus status;
    private String statusReason;
}
