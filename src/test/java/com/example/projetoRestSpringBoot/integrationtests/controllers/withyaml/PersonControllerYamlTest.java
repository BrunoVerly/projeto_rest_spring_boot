package com.example.projetoRestSpringBoot.integrationtests.controllers.withyaml;

import com.example.projetoRestSpringBoot.config.TestConfigs;
import com.example.projetoRestSpringBoot.integrationtests.controllers.withyaml.mapper.YmalMapper;
import com.example.projetoRestSpringBoot.integrationtests.dto.PersonDTO;
import com.example.projetoRestSpringBoot.integrationtests.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerYamlTest extends AbstractIntegrationTest{

    private static RequestSpecification specification;
    private static YmalMapper objetcMapper;
    private static PersonDTO personDTO;
    @BeforeAll
    static void setUp() {
        objetcMapper = new YmalMapper();
        personDTO = new PersonDTO();
    }


    @Test
    @Order(1)
    void createTest() throws JsonProcessingException {
        mockPerson();
        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_EXAMPLE)
                .setBasePath("/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL) {})
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL) {})
                .build();

        var createdPersonDTO = given().config(
                RestAssuredConfig.config()
                        .encoderConfig(
                                EncoderConfig.encoderConfig()
                                        .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
                )
                .spec(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .body(personDTO, objetcMapper)
                .when()
                .post()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(PersonDTO.class, objetcMapper);

        personDTO = createdPersonDTO;

        assertNotNull(createdPersonDTO.getId());
        assertTrue(createdPersonDTO.getId() > 0);
        assertEquals("Linus", createdPersonDTO.getFirstName());
        assertEquals("Torvalds", createdPersonDTO.getLastName());
        assertEquals("Helsinki - Finland", createdPersonDTO.getAddress());
        assertEquals("Male", createdPersonDTO.getGender());
        assertTrue(createdPersonDTO.getEnabled());

    }
    @Test
    @Order(2)
    void updateTest() throws JsonProcessingException {
        personDTO.setLastName("Benedict Torvalds");

        var createdPersonDTO = given().config(
                        RestAssuredConfig.config()
                                .encoderConfig(
                                        EncoderConfig.encoderConfig()
                                                .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
                )
                .spec(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .body(personDTO, objetcMapper)
                .when()
                .put()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(PersonDTO.class, objetcMapper);


        personDTO = createdPersonDTO;

        assertNotNull(createdPersonDTO.getId());
        assertTrue(createdPersonDTO.getId() > 0);
        assertEquals("Linus", createdPersonDTO.getFirstName());
        assertEquals("Benedict Torvalds", createdPersonDTO.getLastName());
        assertEquals("Helsinki - Finland", createdPersonDTO.getAddress());
        assertEquals("Male", createdPersonDTO.getGender());
        assertTrue(createdPersonDTO.getEnabled());

    }

    @Test
    @Order(3)
    void findByIdTest() throws JsonProcessingException {

        var createdPersonDTO = given().config(
                        RestAssuredConfig.config()
                                .encoderConfig(
                                        EncoderConfig.encoderConfig()
                                                .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
                )
                .spec(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .pathParam("id", personDTO.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(PersonDTO.class, objetcMapper);

        personDTO = createdPersonDTO;

        assertNotNull(createdPersonDTO.getId());
        assertTrue(createdPersonDTO.getId() > 0);
        assertEquals("Linus", createdPersonDTO.getFirstName());
        assertEquals("Benedict Torvalds", createdPersonDTO.getLastName());
        assertEquals("Helsinki - Finland", createdPersonDTO.getAddress());
        assertEquals("Male", createdPersonDTO.getGender());
        assertTrue(createdPersonDTO.getEnabled());
    }

    @Test
    @Order(4)
    void disableTest() throws JsonProcessingException {

        var createdPersonDTO = given().config(
                        RestAssuredConfig.config()
                                .encoderConfig(
                                        EncoderConfig.encoderConfig()
                                                .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
                )
                .spec(specification)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .pathParam("id", personDTO.getId())
                .when()
                .patch("{id}")
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(PersonDTO.class, objetcMapper);

        personDTO = createdPersonDTO;

        assertNotNull(createdPersonDTO.getId());
        assertTrue(createdPersonDTO.getId() > 0);
        assertEquals("Linus", createdPersonDTO.getFirstName());
        assertEquals("Benedict Torvalds", createdPersonDTO.getLastName());
        assertEquals("Helsinki - Finland", createdPersonDTO.getAddress());
        assertEquals("Male", createdPersonDTO.getGender());
        assertFalse(createdPersonDTO.getEnabled());
    }

    @Test
    @Order(5)
    void deleteTest() throws JsonProcessingException {

        given().config(
                        RestAssuredConfig.config()
                                .encoderConfig(
                                        EncoderConfig.encoderConfig()
                                                .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT))
                )
                .spec(specification)
                .pathParam("id", personDTO.getId())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);

    }

    /**
    @Test
    @Order(6)
    void findAllTest() throws JsonProcessingException {
        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_EXAMPLE)
                .setBasePath("/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL) {})
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL) {})
                .build();

        var response = given(specification)
                .accept(MediaType.APPLICATION_YAML_VALUE)
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_YAML_VALUE)
                .extract()
                .body()
                .as(PersonDTO[].class, objetcMapper);

        List <PersonDTO> peopleDTO = Arrays.asList(response);
        PersonDTO person1 = peopleDTO.get(0);
        personDTO = person1;

        assertNotNull(person1.getId());
        assertTrue(person1.getId() > 0);
        assertEquals("Albert", person1.getFirstName());
        assertEquals("Einstein", person1.getLastName());
        assertEquals("Ulm - Alemanha", person1.getAddress());
        assertEquals("Male", person1.getGender());
        assertTrue(person1.getEnabled());

        PersonDTO person10 = peopleDTO.get(9);
        personDTO = person10;

        assertNotNull(person1.getId());
        assertTrue(person10.getId() > 0);
        assertEquals("Thomas", person10.getFirstName());
        assertEquals("Edison", person10.getLastName());
        assertEquals("Milan - EUA", person10.getAddress());
        assertEquals("Male", person10.getGender());
        assertTrue(person10.getEnabled());
    }
    **/

    private void mockPerson() {
        personDTO.setFirstName("Linus");
        personDTO.setLastName("Torvalds");
        personDTO.setAddress("Helsinki - Finland");
        personDTO.setGender("Male");
        personDTO.setEnabled(true);
    }

}