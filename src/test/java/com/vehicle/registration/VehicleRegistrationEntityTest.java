package com.vehicle.registration;

import com.vehicle.registration.entity.Person;
import com.vehicle.registration.entity.PersonVehicle;
import com.vehicle.registration.entity.Vehicle;
import com.vehicle.registration.repository.PersonRepository;
import com.vehicle.registration.repository.PersonVehicleRepository;
import com.vehicle.registration.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertNotNull;

@SpringBootTest
public class VehicleRegistrationEntityTest {

    @Autowired
    PersonRepository personRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private PersonVehicleRepository personVehicleRepository;

    @Test
    public void testPersonEntity() {
        // Arrange
        final Person personToSave = buildPerson("Name");

        // Act
        Person personSaved = personRepository.save(personToSave);
        Person personRetrieved = personRepository.findByLastName("Name");

        // Assert
        assertNotNull(personSaved);
        assertNotNull(personRetrieved);
    }

    @Test
    public void testVehicleEntity() {
        // Arrange
        final Vehicle vehicleToSave = buildVehicle("ABC-12X");

        // Act
        Vehicle vehicleSaved = vehicleRepository.save(vehicleToSave);
        Vehicle vehicleRetrieved = vehicleRepository.findByRegistrationNumber("ABC-12X");

        // Assert
        assertNotNull(vehicleSaved);
        assertNotNull(vehicleRetrieved);
    }

    @Test
    public void testPersonVehicleEntity() {
        // Arrange
        final Person personToSave = buildPerson("John");
        final Vehicle vehicleToSave = buildVehicle("XYZ-12X");

        // Act
        Person personSaved = personRepository.save(personToSave);
        Vehicle vehicleSaved = vehicleRepository.save(vehicleToSave);
        final PersonVehicle toSave = buildPersonVehicle(personSaved.getId(), vehicleSaved.getRegistrationNumber());
        PersonVehicle saved = personVehicleRepository.save(toSave);
        PersonVehicle retrieved = personVehicleRepository.findByPersonIdAndRegistrationNumber(personSaved.getId(), vehicleSaved.getRegistrationNumber());

        // Assert
        assertNotNull(saved);
        assertNotNull(retrieved);
    }

    private PersonVehicle buildPersonVehicle(Long personId, String registrationNumber) {
        return PersonVehicle.builder()
                .personId(personId)
                .registrationNumber(registrationNumber)
                .build();
    }

    private Vehicle buildVehicle(String registrationNumber) {
        return Vehicle.builder()
                .registrationNumber(registrationNumber)
                .build();
    }

    private Person buildPerson(String lastName) {
        return Person.builder()
                .firstName("Test")
                .lastName(lastName)
                .build();
    }
}
