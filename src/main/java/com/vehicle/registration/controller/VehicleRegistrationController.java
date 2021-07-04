package com.vehicle.registration.controller;

import com.vehicle.registration.ResponseStatus;
import com.vehicle.registration.dto.CreatePersonResponse;
import com.vehicle.registration.dto.CreateVehicleResponse;
import com.vehicle.registration.dto.LinkPersonToVehicleResponse;
import com.vehicle.registration.dto.PersonRequest;
import com.vehicle.registration.dto.PersonVehicleRequest;
import com.vehicle.registration.dto.PersonVehicleResponse;
import com.vehicle.registration.dto.VehicleRequest;
import com.vehicle.registration.service.VehicleRegistrationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Api("Vehicle registration api")
@Controller
@RequestMapping("/vehicle-registration")
@Slf4j
@RequiredArgsConstructor
public class VehicleRegistrationController {

    private final VehicleRegistrationService vehicleRegistrationService;

    @ApiOperation("Create a person")
    @PostMapping("/create-person")
    public ResponseEntity<CreatePersonResponse> createPerson(
            @RequestBody PersonRequest personRequest) {

        if(!isValidPersonRequest(personRequest)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        CreatePersonResponse response = vehicleRegistrationService.createPerson(personRequest);

        ResponseEntity<CreatePersonResponse> responseEntity = ResponseStatus.SUCCESS == response.getStatus()
                ? new ResponseEntity<>(response, HttpStatus.CREATED)
                : new ResponseEntity<>(response, HttpStatus.OK);

        return responseEntity;
    }

    @ApiOperation("Create a vehicle")
    @PostMapping("/create-vehicle")
    public ResponseEntity<CreateVehicleResponse> createVehicle(
            @RequestBody VehicleRequest vehicleRequest) {

        if(!isValidVehicleRequest(vehicleRequest)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        CreateVehicleResponse response = vehicleRegistrationService.createVehicle(vehicleRequest);

        ResponseEntity<CreateVehicleResponse> responseEntity = ResponseStatus.SUCCESS == response.getStatus()
                ? new ResponseEntity<>(response, HttpStatus.CREATED)
                : new ResponseEntity<>(response, HttpStatus.OK);

        return responseEntity;
    }

    @ApiOperation("Link a person to a vehicle")
    @PatchMapping("/link-person-to-vehicle")
    public ResponseEntity<LinkPersonToVehicleResponse> linkPersonToVehicle(
            @RequestBody PersonVehicleRequest personVehicleRequest) {

        if(!isValidPersonVehicleRequest(personVehicleRequest)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        LinkPersonToVehicleResponse response = vehicleRegistrationService
                .linkPersonToVehicle(personVehicleRequest);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation("Unlink a person from a vehicle")
    @DeleteMapping("/unlink-person-from-vehicle")
    public ResponseEntity<LinkPersonToVehicleResponse> unLinkPersonFromVehicle(
            @RequestBody PersonVehicleRequest personVehicleRequest) {

        if(!isValidPersonVehicleRequest(personVehicleRequest)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        LinkPersonToVehicleResponse response = vehicleRegistrationService
                .unLinkPersonToVehicle(personVehicleRequest);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation("Get person vehicle registrations")
    @GetMapping("/get-person-vehicle-registrations")
    public ResponseEntity<PersonVehicleResponse> getPersonVehicleRegistrations(
            @RequestParam String firstName, @RequestParam String lastName) {

        if(!isValidGetPersonVehicleRequest(firstName, lastName)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        PersonVehicleResponse response = vehicleRegistrationService
                .getPersonVehicleRegistrations(firstName, lastName);
        HttpStatus httpStatus = response.getStatus() == ResponseStatus.ERROR ? HttpStatus.NOT_FOUND : HttpStatus.OK;

        return new ResponseEntity<>(response, httpStatus);
    }

    private boolean isValidGetPersonVehicleRequest(String firstName, String lastName) {
        if (!StringUtils.hasLength(firstName)
                || !StringUtils.hasLength(lastName)) {
            return false;
        }
        return true;
    }

    private boolean isValidPersonRequest(PersonRequest personRequest) {
        if (!StringUtils.hasLength(personRequest.getFirstName())
                || !StringUtils.hasLength(personRequest.getLastName())) {
            return false;
        }
        return true;
    }

    private boolean isValidVehicleRequest(VehicleRequest vehicleRequest) {
        if (!StringUtils.hasLength(vehicleRequest.getRegistrationNumber())) {
            return false;
        }
        return true;
    }

    private boolean isValidPersonVehicleRequest(PersonVehicleRequest personVehicleRequest) {
        if (!StringUtils.hasLength(personVehicleRequest.getFirstName())
                || !StringUtils.hasLength(personVehicleRequest.getLastName())
                ||!StringUtils.hasLength(personVehicleRequest.getRegistrationNumber())) {
            return false;
        }
        return true;
    }
}
