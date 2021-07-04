package com.vehicle.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicle.registration.dto.PersonRequest;
import com.vehicle.registration.dto.VehicleRequest;
import com.vehicle.registration.entity.Person;
import com.vehicle.registration.entity.PersonVehicle;
import com.vehicle.registration.entity.Vehicle;
import com.vehicle.registration.repository.PersonRepository;
import com.vehicle.registration.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class VehicleRegistrationControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    @Test
    public void testCreatePerson() throws Exception{

        // Arrange
        final String firstName = "John";
        final String lastName = "Bob";

        // Act
        mvc.perform(post("/vehicle-registration/create-person")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(VehicleRegistrationTestFixture.buildPersonDTO(firstName, lastName))))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn();

        // Assert
        Person savedResult = personRepository.findByLastName("Bob");
        assertNotNull(savedResult);
        assertEquals("Bob", savedResult.getLastName());
    }

    @Test
    public void shouldReturnErrorResponse_whenDuplicatePerson() throws Exception{

        // Arrange
        final String firstName = "Foo";
        final String lastName = "Bar";

        // Act
        createPerson(VehicleRegistrationTestFixture.buildPersonDTO(firstName, lastName));

        // Assert
        // Create the same person again, it should return the error response
        mvc.perform(post("/vehicle-registration/create-person")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(VehicleRegistrationTestFixture.buildPersonDTO(firstName, lastName))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(duplicatePersonErrorResponse()))
                .andReturn();
    }

    @Test
    public void testCreateVehicle() throws Exception{

        // Arrange
        RequestBuilder requestBuilder = post("/vehicle-registration/create-vehicle")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(VehicleRegistrationTestFixture.buildVehicleDTO("ABC-78X")));

        // Act
        mvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isCreated()).andReturn();

        // Assert
        Vehicle savedResult = vehicleRepository.findByRegistrationNumber("ABC-81X");
        assertNotNull(savedResult);
        assertEquals("ABC-81X", savedResult.getRegistrationNumber());
    }

    @Test
    public void shouldReturnErrorResponse_whenDuplicateVehicle() throws Exception{

        // Arrange
        String registrationNumber = "ABC-81X";
        final VehicleRequest vehicleRequest = VehicleRegistrationTestFixture.buildVehicleDTO(registrationNumber);

        // Act
        createVehicle(vehicleRequest);

        // Assert
        // Create the same vehicle again, it should return a error response.
        mvc.perform(post("/vehicle-registration/create-vehicle")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(vehicleRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(duplicateVehicleErrorResponse()))
                .andReturn();
    }

    @Test
    public void linkPersonToVehicle() throws Exception {

        // Arrange
        final String firstName = "Mary";
        final String lastName = "Ann";
        final String registrationNumber = "XYZ-92S";
        // Create a person
        createPerson(VehicleRegistrationTestFixture.buildPersonDTO(firstName, lastName));
        // Create a vehicle
        createVehicle(VehicleRegistrationTestFixture.buildVehicleDTO(registrationNumber));

        // Act
        // Link the person to vehicle
        mvc.perform(patch("/vehicle-registration/link-person-to-vehicle")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(VehicleRegistrationTestFixture
                        .buildPersonVehicleDTO(firstName, lastName, registrationNumber))))
                .andExpect(status().isOk()).andReturn();

        // Assert
        Person person = personRepository.findByFirstNameAndLastName(firstName, lastName);
        Set<PersonVehicle> savedResult = person.getPersonVehicle();
        assertNotNull(savedResult);
        assertFalse(savedResult.isEmpty());
        assertNotNull(savedResult
                .stream()
                .filter(personVehicle -> personVehicle.getRegistrationNumber().equals(registrationNumber)).findAny().get());
    }

    @Test
    public void unLinkPersonFromVehicle() throws Exception {

        // Arrange
        final String firstName = "Alex";
        final String lastName = "Paul";
        final String registrationNumber = "XYZ-56S";
        // Create a person
        createPerson(VehicleRegistrationTestFixture.buildPersonDTO(firstName, lastName));
        // Create a vehicle
        createVehicle(VehicleRegistrationTestFixture.buildVehicleDTO(registrationNumber));
        // Link the person to vehicle
        mvc.perform(patch("/vehicle-registration/link-person-to-vehicle")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(VehicleRegistrationTestFixture
                        .buildPersonVehicleDTO(firstName, lastName, registrationNumber))))
                .andReturn();

        // Act
        mvc.perform(delete("/vehicle-registration/unlink-person-from-vehicle")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(VehicleRegistrationTestFixture
                        .buildPersonVehicleDTO(firstName, lastName, registrationNumber))))
                .andExpect(status().isOk())
                .andExpect(content().json(successfulUnlinkResponse()))
                .andReturn();


        // Assert
        Person person = personRepository.findByFirstNameAndLastName(firstName, lastName);
        Set<PersonVehicle> savedResult = person.getPersonVehicle();
        assertTrue(savedResult
                .stream()
                .filter(personVehicle -> personVehicle.getRegistrationNumber().equals(registrationNumber)).findAny().isEmpty());
    }

    @Test
    public void linkOnePersonToMultipleVehicles() throws Exception {

        // Arrange
        final String firstName = "Mary";
        final String lastName = "Ann";
        final String firstRegistrationNumber = "JDK-78L";
        final String secondRegistrationNumber = "ABC-65H";
        // Create a person
        createPerson(VehicleRegistrationTestFixture.buildPersonDTO(firstName, lastName));
        // Create the first vehicle
        createVehicle(VehicleRegistrationTestFixture.buildVehicleDTO(firstRegistrationNumber));
        // Create the second vehicle
        createVehicle(VehicleRegistrationTestFixture.buildVehicleDTO(secondRegistrationNumber));

        // Act
        // Link the person to the first vehicle
        mvc.perform(patch("/vehicle-registration/link-person-to-vehicle")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(VehicleRegistrationTestFixture
                        .buildPersonVehicleDTO(firstName, lastName, firstRegistrationNumber))))
                .andExpect(status().isOk()).andReturn();

        // Link the person to the second vehicle
        mvc.perform(patch("/vehicle-registration/link-person-to-vehicle")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(VehicleRegistrationTestFixture
                        .buildPersonVehicleDTO(firstName, lastName, secondRegistrationNumber))))
                .andExpect(status().isOk()).andReturn();

        // Assert
        Person person = personRepository.findByFirstNameAndLastName(firstName, lastName);
        Set<PersonVehicle> savedResult = person.getPersonVehicle();
        assertNotNull(savedResult);
        assertFalse(savedResult.isEmpty());
        assertEquals(2, savedResult.size());
        assertNotNull(savedResult
                .stream()
                .filter(personVehicle -> personVehicle.getRegistrationNumber().equals(firstRegistrationNumber)).findAny().get());
        assertNotNull(savedResult
                .stream()
                .filter(personVehicle -> personVehicle.getRegistrationNumber().equals(secondRegistrationNumber)).findAny().get());
    }

    @Test
    public void testOneVehicleMustBeRegisteredToOnlyOnePerson() throws Exception {

        // Arrange
        final String person1FirstName = "Amy";
        final String person1LastName = "Jackson";

        final String person2FirstName = "Ruth";
        final String person2LastName = "Mathew";


        final String registrationNumber = "FGH-84F";

        // Create the first person
        createPerson(VehicleRegistrationTestFixture.buildPersonDTO(person1FirstName, person1LastName));
        // Create the second person
        createPerson(VehicleRegistrationTestFixture.buildPersonDTO(person2FirstName, person2LastName));
        // Create the vehicle
        createVehicle(VehicleRegistrationTestFixture.buildVehicleDTO(registrationNumber));


        // Act
        // Link the first person to the vehicle
        mvc.perform(patch("/vehicle-registration/link-person-to-vehicle")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(VehicleRegistrationTestFixture
                        .buildPersonVehicleDTO(person1FirstName, person1LastName, registrationNumber))))
                .andExpect(status().isOk()).andReturn();

        // Try to link the second person to the same vehicle, it should return the error response.
        mvc.perform(patch("/vehicle-registration/link-person-to-vehicle")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(VehicleRegistrationTestFixture
                        .buildPersonVehicleDTO(person2FirstName, person2LastName, registrationNumber))))
                .andExpect(status().isOk())
                .andExpect(content().json(duplicatePersonVehicleLinkErrorResponse()))
                .andReturn();

        // Assert
        Person person = personRepository.findByFirstNameAndLastName(person1FirstName, person1LastName);
        Set<PersonVehicle> savedResult = person.getPersonVehicle();
        assertNotNull(savedResult);
        assertFalse(savedResult.isEmpty());
        assertEquals(1, savedResult.size());
        assertNotNull(savedResult
                .stream()
                .filter(personVehicle -> personVehicle.getRegistrationNumber().equals(registrationNumber)).findAny().get());
    }

    @Test
    public void testCreatePersonWithInvalidInput() throws Exception {

        // Arrange
        final String firstName = null;
        final String lastName = null;

        // Act & Assert
        mvc.perform(post("/vehicle-registration/create-person")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(VehicleRegistrationTestFixture.buildPersonDTO(firstName, lastName))))
                .andDo(print())
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void testCreateVehicleWithInvalidInput() throws Exception {

        // Arrange
        RequestBuilder requestBuilder = post("/vehicle-registration/create-vehicle")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(VehicleRegistrationTestFixture.buildVehicleDTO(null)));

        // Act
        mvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void linkPersonToVehicleWithInvalidInput() throws Exception {

        // Arrange
        final String firstName = "Mary";
        final String lastName = "Ann";
        final String registrationNumber = "XYZ-92S";
        // Create a person
        createPerson(VehicleRegistrationTestFixture.buildPersonDTO(firstName, lastName));
        // Create a vehicle
        createVehicle(VehicleRegistrationTestFixture.buildVehicleDTO(registrationNumber));

        // Act & Assert
        mvc.perform(patch("/vehicle-registration/link-person-to-vehicle")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(VehicleRegistrationTestFixture
                        .buildPersonVehicleDTO(null, null, null))))
                .andExpect(status().isBadRequest()).andReturn();

    }

    @Test
    public void unLinkPersonFromVehicleWithInvalidInput() throws Exception {

        // Arrange
        final String firstName = "Alex";
        final String lastName = "Paul";
        final String registrationNumber = "XYZ-56S";
        // Create a person
        createPerson(VehicleRegistrationTestFixture.buildPersonDTO(firstName, lastName));
        // Create a vehicle
        createVehicle(VehicleRegistrationTestFixture.buildVehicleDTO(registrationNumber));
        // Link the person to vehicle
        mvc.perform(patch("/vehicle-registration/link-person-to-vehicle")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(VehicleRegistrationTestFixture
                        .buildPersonVehicleDTO(firstName, lastName, registrationNumber))))
                .andReturn();

        // Act & Assert
        mvc.perform(delete("/vehicle-registration/unlink-person-from-vehicle")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(VehicleRegistrationTestFixture
                        .buildPersonVehicleDTO(null, null, null))))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void testGetPersonVehicleRegistrations() throws Exception {

        // Arrange
        final String firstName = "Paul";
        final String lastName = "Alex";
        final String registrationNumber = "XHJZ-56S";
        // Create a person
        createPerson(VehicleRegistrationTestFixture.buildPersonDTO(firstName, lastName));
        // Create a vehicle
        createVehicle(VehicleRegistrationTestFixture.buildVehicleDTO(registrationNumber));
        // Link the person to vehicle
        mvc.perform(patch("/vehicle-registration/link-person-to-vehicle")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(VehicleRegistrationTestFixture
                        .buildPersonVehicleDTO(firstName, lastName, registrationNumber))))
                .andReturn();

        // Act & Assert
        mvc.perform(get("/vehicle-registration/get-person-vehicle-registrations")
                .param("firstName", firstName)
                .param("lastName", lastName))
                .andExpect(status().isOk())
                .andExpect(content().json(successfulGetResponse()))
                .andReturn();
    }

    private String successfulGetResponse() {
        return "{\"status\": \"SUCCESS\",\"statusReason\": \"Found person-vehicle registration(s)\",\"personVehicles\": {\"firstName\": \"Paul\",\"lastName\": \"Alex\",\"vehicles\": [{\"registrationNumber\": \"XHJZ-56S\"}]}}";
    }

    private void createPerson(PersonRequest personRequest) throws Exception {
        mvc.perform(post("/vehicle-registration/create-person")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(personRequest)))
                .andReturn();
    }

    private void createVehicle(VehicleRequest vehicleRequest) throws Exception {
        mvc.perform(post("/vehicle-registration/create-vehicle")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(vehicleRequest)))
                .andReturn();
    }

    private String duplicatePersonErrorResponse() {
        return "{\"status\": \"ERROR\",\"statusReason\": \"Duplicate Person\"}";
    }

    private String duplicateVehicleErrorResponse() {
        return "{\"status\": \"ERROR\",\"statusReason\": \"Duplicate Vehicle\"}";
    }

    private String duplicatePersonVehicleLinkErrorResponse() {
        return "{\"status\": \"ERROR\",\"statusReason\": \"Duplicate person vehicle link\"}";
    }

    private String successfulUnlinkResponse() {
        return "{\"status\": \"SUCCESS\",\"statusReason\": \"Person vehicle unlink successful\"}";
    }

    private String asJsonString(final Object data) throws Exception {
        return new ObjectMapper().writeValueAsString(data);
    }

}
