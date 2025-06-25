package com.Login.Email.tests;


import com.Login.Email.reports.ExtentFactory;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class AuthControllerTest {


   // @LocalServerPort
   // private int port;

    private static ExtentReports extent;

    @BeforeAll
    static void setUpReport() {
        extent = ExtentFactory.getInstance();

    }


    @BeforeEach void setUp() {
      //  RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    @Order(1)
    void loginShouldReturnToken_WhenCredentialsAreValid() {
        ExtentTest test = extent.createTest("Login con credenciales válidas");

        String requestBody = """
            {
                "username": "superadmin",
                "password": "superadmin"
            }
        """;

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/auth/login");

        if (response.statusCode() == 200 && response.jsonPath().getString("accessToken") != null) {
            test.pass("Login exitoso, se recibió un token");
        } else {
            test.fail("Fallo el login: código " + response.statusCode());
        }
        System.out.println("Body: " + response.getBody().asString());




        Assertions.assertEquals(200, response.statusCode());
    }

    @AfterAll
    public static void reporte() {
        extent.flush();
    }



    @Test
    @Order(2)
    void registerShouldReturnToken_WhenRequestIsValid() {
        ExtentTest test = extent.createTest("Registro con datos válidos");

        String uniqueUsername = "usuario" + System.currentTimeMillis(); // para evitar duplicados
        String uniqueEmail = "correo" + System.currentTimeMillis() + "@mail.com";

        String requestBody = String.format("""
        {
            "username": "%s",
            "password": "password123",
            "email": "%s"
        }
    """, uniqueUsername, uniqueEmail);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/auth/register");

        String responseBody = response.getBody().asString();
        System.out.println("Register Response: " + responseBody);

        if (response.statusCode() == 200 &&
                response.jsonPath().getString("accessToken") != null &&
                response.jsonPath().getString("user.username").equals(uniqueUsername)) {
            test.pass("Registro exitoso. Se recibió token y username correcto.");
        } else {
            test.fail("Falló el registro: código " + response.statusCode());
        }

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertNotNull(response.jsonPath().getString("accessToken"));
        Assertions.assertEquals(uniqueUsername, response.jsonPath().getString("user.username"));
    }
}
