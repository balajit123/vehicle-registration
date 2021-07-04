package com.vehicle.registration.mapper;

import com.vehicle.registration.dto.PersonRequest;
import com.vehicle.registration.entity.Person;

import java.util.function.Function;

public class PersonMapper implements Function<PersonRequest, Person> {

    @Override
    public Person apply(PersonRequest personRequest) {
        return Person.builder()
                .firstName(personRequest.getFirstName())
                .lastName(personRequest.getLastName())
                .build();
    }
}
