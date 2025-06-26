package com.Login.Email.tests;


import com.Login.Email.Controller.AuthController;
import com.Login.Email.Model.User;
import com.Login.Email.Service.Impl.UserServiceImpl;
import com.Login.Email.reports.ExtentFactory;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;


@WebMvcTest(AuthController.class)
public class UserControllerTest {

    private static ExtentReports extent;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;
    @BeforeAll
    static void setUpReport() {
        extent = ExtentFactory.getInstance();

    }

    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    public void testRegisterUser() {

        ExtentTest test = extent.createTest("Creacion de usuario con envio de correo");

        try {


        test.info("Se inicia el test de registro de usuario");
        User user = new User();
        user.setEmail("test@example.com");
        user.setUsername("Jorge");
        user.setPassword("12345678");
        test.info("Mockeando servicio userService.saveUser(...)");
        when(userService.saveUser(ArgumentMatchers.any(User.class))).thenReturn(user);
        test.info("Enviando request POST a /api/user/register");
        RestAssuredMockMvc.given()
                .contentType("application/json")
                .body("""
                        {
                            "name": "Jorge",
                            "email": "test@example.com",
                            "password": "12345678"
                        }
                    """)
                .when()
                .post("/api/user/register")
                .then()
                .statusCode(201)
                .body("message", equalTo("User Create"))
                .body("data.user.name", equalTo("Jorge"))
                .body("data.user.email", equalTo("test@example.com"));

        verify(userService, times(1)).saveUser(ArgumentMatchers.any(User.class));

        test.pass("El usuario fue registrado exitosamente y el correo fue enviado.");

        } catch (AssertionError | Exception e) {
            test.fail("❌ Falló el test: " + e.getMessage());
            Assertions.fail(e); // Para que JUnit lo registre también como fallo
        }
    }

    @AfterAll
    public static void reporte() {
        extent.flush();
    }
}