package com.vehicle.registration.service;

import com.vehicle.registration.dto.CreatePersonResponse;
import com.vehicle.registration.dto.CreateVehicleResponse;
import com.vehicle.registration.dto.LinkPersonToVehicleResponse;
import com.vehicle.registration.dto.PersonRequest;
import com.vehicle.registration.dto.PersonVehicleRequest;
import com.vehicle.registration.dto.PersonVehicleResponse;
import com.vehicle.registration.dto.VehicleRequest;

public interface VehicleRegistrationService {

    CreatePersonResponse createPerson(PersonRequest personRequest);

    CreateVehicleResponse createVehicle(VehicleRequest vehicleRequest);

    LinkPersonToVehicleResponse linkPersonToVehicle(PersonVehicleRequest personVehicleRequest);

    LinkPersonToVehicleResponse unLinkPersonToVehicle(PersonVehicleRequest personVehicleRequest);

    PersonVehicleResponse getPersonVehicleRegistrations(String firstName, String lastName);
}
