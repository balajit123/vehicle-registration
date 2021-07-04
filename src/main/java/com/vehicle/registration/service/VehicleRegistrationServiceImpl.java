package com.vehicle.registration.service;

import com.vehicle.registration.ResponseStatus;
import com.vehicle.registration.dto.CreatePersonResponse;
import com.vehicle.registration.dto.CreateVehicleResponse;
import com.vehicle.registration.dto.LinkPersonToVehicleResponse;
import com.vehicle.registration.dto.PersonRequest;
import com.vehicle.registration.dto.PersonVehicleRequest;
import com.vehicle.registration.dto.PersonVehicleResponse;
import com.vehicle.registration.dto.VehicleRequest;
import com.vehicle.registration.entity.Person;
import com.vehicle.registration.entity.PersonVehicle;
import com.vehicle.registration.entity.Vehicle;
import com.vehicle.registration.mapper.PersonMapper;
import com.vehicle.registration.mapper.PersonVehicleMapper;
import com.vehicle.registration.mapper.PersonVehiclesResponseMapper;
import com.vehicle.registration.mapper.VehicleMapper;
import com.vehicle.registration.repository.PersonRepository;
import com.vehicle.registration.repository.PersonVehicleRepository;
import com.vehicle.registration.repository.VehicleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
public class VehicleRegistrationServiceImpl implements VehicleRegistrationService {

    private final PersonRepository personRepository;
    private final VehicleRepository vehicleRepository;
    private final PersonVehicleRepository personVehicleRepository;

    public VehicleRegistrationServiceImpl(@Autowired PersonRepository personRepository,
                                          @Autowired VehicleRepository vehicleRepository,
                                          @Autowired PersonVehicleRepository personVehicleRepository) {
        this.personRepository = personRepository;
        this.vehicleRepository = vehicleRepository;
        this.personVehicleRepository = personVehicleRepository;
    }

    @Override
    public CreatePersonResponse createPerson(PersonRequest personRequest) {
        final CreatePersonResponse response = CreatePersonResponse.builder().build();
        Person person = new PersonMapper().apply(personRequest);
        try {
            personRepository.save(person);
            response.setStatus(ResponseStatus.SUCCESS);
            response.setStatusReason("Person created successfully");
            return response;
        } catch (DataIntegrityViolationException e) {
            log.error("Error creating the person", e);
            response.setStatus(ResponseStatus.ERROR);
            response.setStatusReason("Duplicate Person");
            return response;
        }
    }

    @Override
    public CreateVehicleResponse createVehicle(VehicleRequest vehicleRequest) {
        final CreateVehicleResponse response = CreateVehicleResponse.builder().build();
        Vehicle vehicle = new VehicleMapper().apply(vehicleRequest);
        try {
            vehicleRepository.save(vehicle);
            response.setStatus(ResponseStatus.SUCCESS);
            response.setStatusReason("Vehicle created successfully");
            return response;
        } catch (DataIntegrityViolationException e) {
            log.error("Error creating the vehicle", e);
            response.setStatus(ResponseStatus.ERROR);
            response.setStatusReason("Duplicate Vehicle");
            return response;
        }
    }

    @Override
    public LinkPersonToVehicleResponse linkPersonToVehicle(PersonVehicleRequest personVehicleRequest) {
        Person person = personRepository.findByFirstNameAndLastName(personVehicleRequest.getFirstName(), personVehicleRequest.getLastName());
        Vehicle vehicle = vehicleRepository.findByRegistrationNumber(personVehicleRequest.getRegistrationNumber());
        final LinkPersonToVehicleResponse response = LinkPersonToVehicleResponse.builder().build();
        if (null != person && null != vehicle) {
            PersonVehicle personVehicle = new PersonVehicleMapper().apply(personVehicleRequest);
            personVehicle.setPersonId(person.getId());
            try {
                personVehicleRepository.save(personVehicle);
                response.setStatus(ResponseStatus.SUCCESS);
                response.setStatusReason("Person vehicle link created successfully");
            } catch (DataIntegrityViolationException e) {
                log.error("Error linking person to vehicle", e);
                response.setStatus(ResponseStatus.ERROR);
                response.setStatusReason("Duplicate person vehicle link");
            }
        } else {
            response.setStatus(ResponseStatus.ERROR);
            String reason = null == person ? "Person not found" : "Vehicle not found";
            response.setStatusReason(reason);
            log.error("Error linking person: Reason: {}",reason);
        }
        return response;
    }

    @Override
    @Transactional
    public LinkPersonToVehicleResponse unLinkPersonToVehicle(PersonVehicleRequest personVehicleRequest) {
        Person person = personRepository.findByFirstNameAndLastName(personVehicleRequest.getFirstName(), personVehicleRequest.getLastName());
        LinkPersonToVehicleResponse response = LinkPersonToVehicleResponse.builder().build();
        try {
            if (null != person) {
                PersonVehicle personVehicle = personVehicleRepository
                        .findByPersonIdAndRegistrationNumber(person.getId(), personVehicleRequest.getRegistrationNumber());
                if (null != personVehicle) {
                    personVehicleRepository.deletePersonVehicleLink(personVehicle.getId());
                    response.setStatus(ResponseStatus.SUCCESS);
                    response.setStatusReason("Person vehicle unlink successful");
                } else {
                    log.error("Error unlinking person from vehicle: Reason: Person vehicle link not found");
                    response.setStatus(ResponseStatus.ERROR);
                    response.setStatusReason("Person vehicle link not found");
                }
            } else {
                log.error("Error unlinking person from vehicle: Reason: Person not found");
                response.setStatus(ResponseStatus.ERROR);
                response.setStatusReason("Person not found");
            }
        } catch (DataIntegrityViolationException e) {
            log.error("Error unlinking person from vehicle", e);
            response.setStatus(ResponseStatus.ERROR);
            response.setStatusReason("Person vehicle unlink was unsuccessful");
            return response;
        }
        return response;
    }

    @Override
    public PersonVehicleResponse getPersonVehicleRegistrations(String firstName, String lastName) {
        PersonVehicleResponse response = PersonVehicleResponse.builder().build();
        try {
            Person person = personRepository.findByFirstNameAndLastName(firstName, lastName);
            if(null == person || null == person.getPersonVehicle()
                    || person.getPersonVehicle().isEmpty()) {
                log.error("Error getting person vehicle registration: Reason: Person-vehicle registration not found");
                response.setStatus(ResponseStatus.ERROR);
                response.setStatusReason("Person-vehicle registration not found");
            } else {
                response.setPersonVehicles(new PersonVehiclesResponseMapper().apply(person));
                response.setStatus(ResponseStatus.SUCCESS);
                response.setStatusReason("Found person-vehicle registration(s)");
            }
            return response;
        } catch (DataIntegrityViolationException e) {
            log.error("Error getting person vehicle registration" ,e);
            response.setStatus(ResponseStatus.ERROR);
            response.setStatusReason("Person vehicle unlink was unsuccessful");
            return response;
        }
    }
}
