package com.vehicle.registration;

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
import com.vehicle.registration.repository.PersonRepository;
import com.vehicle.registration.repository.PersonVehicleRepository;
import com.vehicle.registration.repository.VehicleRepository;
import com.vehicle.registration.service.VehicleRegistrationService;
import com.vehicle.registration.service.VehicleRegistrationServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

import static com.vehicle.registration.VehicleRegistrationTestFixture.buildPersonDTO;
import static com.vehicle.registration.VehicleRegistrationTestFixture.buildVehicleDTO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VehicleRegistrationServiceTest {

    private VehicleRegistrationService vehicleRegistrationService;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private PersonVehicleRepository personVehicleRepository;

    @Before
    public void setup() {
        vehicleRegistrationService = new VehicleRegistrationServiceImpl(personRepository,
                vehicleRepository, personVehicleRepository);
    }

    @Test
    public void testCreatePerson() {

        // Arrange
        arrangePersonRepository();
        PersonRequest personRequest = buildPersonDTO("John", "Bob");

        // Act
        CreatePersonResponse response = vehicleRegistrationService.createPerson(personRequest);

        // Assert
        assertNotNull(response);
        verify(personRepository).save(any(Person.class));
        assertEquals(ResponseStatus.SUCCESS, response.getStatus());
    }

    @Test
    public void testCreateVehicle() {

        // Arrange
        VehicleRequest vehicleRequest = buildVehicleDTO("XKR-64O");

        // Act
        CreateVehicleResponse response = vehicleRegistrationService.createVehicle(vehicleRequest);

        // Assert
        assertNotNull(response);
        verify(vehicleRepository).save(any(Vehicle.class));
        assertEquals(ResponseStatus.SUCCESS, response.getStatus());
    }

    @Test
    public void testLinkPersonToVehicle() {

        // Arrange
        when(personRepository.findByFirstNameAndLastName(anyString(), anyString()))
                .thenReturn(Person.builder().id(1L).build());
        PersonVehicleRequest personVehicleRequest = PersonVehicleRequest.builder()
                .firstName("Bob")
                .lastName("Alex")
                .registrationNumber("YER-76L")
                .build();

        // Act
        LinkPersonToVehicleResponse response = vehicleRegistrationService.linkPersonToVehicle(personVehicleRequest);

        // Assert
        assertNotNull(response);
        verify(personVehicleRepository).save(any(PersonVehicle.class));
        assertEquals(ResponseStatus.SUCCESS, response.getStatus());
    }

    @Test
    public void testUnLinkPersonFromVehicle() {

        // Arrange
        when(personRepository.findByFirstNameAndLastName(anyString(), anyString()))
                .thenReturn(Person.builder().id(1L).build());
        when(personVehicleRepository.findByPersonIdAndRegistrationNumber(anyLong(), anyString()))
                .thenReturn(PersonVehicle.builder().id(1L).build());
        PersonVehicleRequest personVehicleRequest = PersonVehicleRequest.builder()
                .firstName("Bob")
                .lastName("Alex")
                .registrationNumber("YER-76L")
                .build();

        // Act
        LinkPersonToVehicleResponse response = vehicleRegistrationService.unLinkPersonToVehicle(personVehicleRequest);

        // Assert
        assertNotNull(response);
        verify(personVehicleRepository).deletePersonVehicleLink(anyLong());
        assertEquals(ResponseStatus.SUCCESS, response.getStatus());
    }

    @Test
    public void testGetPersonVehicleRegistrations() {

        // Arrange
        when(personRepository.findByFirstNameAndLastName(anyString(), anyString()))
                .thenReturn(Person.builder().id(1L).personVehicle(buildPersonVehicles()).build());

        // Act
        PersonVehicleResponse response = vehicleRegistrationService.getPersonVehicleRegistrations("Bob", "Alex");

        // Assert
        assertNotNull(response);
        verify(personRepository).findByFirstNameAndLastName(anyString(), anyString());
        assertEquals(ResponseStatus.SUCCESS, response.getStatus());
    }

    private Set<PersonVehicle> buildPersonVehicles() {
        Set<PersonVehicle> vehicles = new HashSet<>();
        vehicles.add(PersonVehicle.builder().registrationNumber("FGC-56H").build());
        return vehicles;
    }

    private void arrangePersonRepository() {
        Person savedPerson = Person.builder()
                .firstName("John")
                .lastName("Bob")
                .build();
        when(personRepository.save(any(Person.class)))
                .thenReturn(savedPerson);
    }

}
