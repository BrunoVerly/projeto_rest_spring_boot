package com.example.projetoRestSpringBoot.data.dto;

import com.example.projetoRestSpringBoot.serializer.GenderSerializer;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.persistence.*;
import org.hibernate.boot.jaxb.hbm.internal.RepresentationModeConverter;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

//@JsonPropertyOrder({"id", "address", "first_name", "last_name","gender"})
//@JsonFilter("PersonFilter")
public class PersonDTO extends RepresentationModel<PersonDTO> implements Serializable {
    private static final long serialVersionUID = 1L;


    private Long id;
    //@JsonProperty("first_name")
    private String firstName;
    //@JsonPropertyOrder("last_name")
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    private String lastName;
    //@JsonInclude(JsonInclude.Include.NON_EMPTY)
    //private String phoneNumber;
    //@JsonFormat(pattern = "dd/MM/yyyy")
    //private Date birthDay;
    private String address;
    //@JsonIgnore
    //@JsonSerialize(using = GenderSerializer.class)
    private String gender;
    private boolean enabled;
    //private String sensitiveData;


    public PersonDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    //public Date getBirthDay() {
    //    return birthDay;
    //}
    //public void setBirthDay(Date birthDay) {
    //    this.birthDay = birthDay;
    //}
    //public String getPhoneNumber() {
    //    return phoneNumber;
    //}
    //public void setPhoneNumber(String phoneNumber) {
    //    this.phoneNumber = phoneNumber;
    //}
    //public String getSensitiveData() {
    //    return sensitiveData;
    //}    public void setSensitiveData(String sensitiveData) {
    //    this.sensitiveData = sensitiveData;
    //}
    public boolean getEnabled() {
        return enabled;
    }    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PersonDTO personDTO = (PersonDTO) o;
        return enabled == personDTO.enabled && Objects.equals(id, personDTO.id) && Objects.equals(firstName, personDTO.firstName) && Objects.equals(lastName, personDTO.lastName) && Objects.equals(address, personDTO.address) && Objects.equals(gender, personDTO.gender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, firstName, lastName, address, gender, enabled);
    }
}
