package com.example.projetoRestSpringBoot.integrationtests.swagger;

import com.example.projetoRestSpringBoot.config.TestConfigs;
import com.example.projetoRestSpringBoot.integrationtests.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SwaggerIntegrationTest extends AbstractIntegrationTest {

	@Test
	void shouldDisplaySwaggerUiPage() {
        var content = given()
            .basePath("/swagger-ui/index.html")
                .port(TestConfigs.SERVER_PORT)
            .when()
                .get()
            .then()
                .statusCode(200)
            .extract()
                .body()
                    .asString();


        assertTrue(content.contains("Swagger UI"));
	}

}
