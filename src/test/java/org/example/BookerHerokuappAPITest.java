package org.example;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static io.restassured.RestAssured.given;

public class BookerHerokuappAPITest {
    static Map<String,String> headers = new HashMap<>();
    static Properties prop=new Properties();
    static String token;
    static String firstBookingId;
    static String secondBookingId;

    @BeforeAll
    static void setup()throws IOException {
        RestAssured.filters(new AllureRestAssured());
        //headers.put(MyProp.authTokenShutterStockHeader, token);
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        FileInputStream fis;
        fis=new FileInputStream("src/test/resources/my.properties");
        prop.load(fis);


        getToken();

        headers.put(MyProp.authTokenShutterStockHeader, token);
        headers.put(MyProp.ContentType,MyProp.applicationJson);

    }

    static void getToken(){
        String result=given()
                .contentType(MyProp.applicationJson)
                .accept(MyProp.applicationJson)
                .log()
                .method()
                .body((String) prop.get("bookerLoginData"))
                .when()
                .post((String)prop.get("bookerBaseUrl")+"/auth")
                .then()
                .statusCode(200).contentType(MyProp.applicationJson)
                .extract()
                .response()
                .jsonPath()
                .getString("token");
        token=result;
        System.out.println("ВЫВОД ЗАПРОСА token ---> "+token);
    }

    @Test
    void createBookingInvalidDate(){
        given()
                .headers(headers)
                .body((String) prop.get("bodyRequestCreateBookingInvalidDate"))
                .when()
                .post((String)prop.get("bookerBaseUrl")+"/booking")
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Nested
    class firstBooking{
        @Order( Integer.MIN_VALUE)
        @Test
        void createBookingOne(){
            String result = given()
                    .headers(headers)
                    .body((String) prop.get("bodyRequestCreateBookingValidDataOne"))
                    .when()
                    .post((String)prop.get("bookerBaseUrl")+"/booking")
                    .prettyPeek()
                    .then()
                    .statusCode(200)
                    .extract()
                    .response()
                    .jsonPath()
                    .getString("bookingid");
            firstBookingId=result;


        }

        @Test //Анотация @Order( Integer.MIN_VALUE) не работает
        void patchBookingOne(){
            given()
                    .headers(headers)
                    .when()
                    .patch((String)prop.get("bookerBaseUrl")+"/booking/"+firstBookingId)
                    .prettyPeek()
                    .then()
                    .statusCode(403);

        }

    }


    @Nested
    class secondBooking{
        @Test
        @Order(Integer.MIN_VALUE)
        void createBookingTwo(){
            String result = given()
                    .headers(headers)
                    .body((String) prop.get("bodyRequestCreateBookingValidDataTwo"))
                    .when()
                    .post((String)prop.get("bookerBaseUrl")+"/booking")
                    .prettyPeek()
                    .then()
                    .statusCode(200)
                    .extract()
                    .response()
                    .jsonPath()
                    .getString("bookingid");
            secondBookingId=result;
        }

        @Test
        void getSecondBookingId(){

            given()
                    .headers(headers)
                    .when()
                    .get((String)prop.get("bookerBaseUrl")+"/booking/"+secondBookingId)
                    .prettyPeek()
                    .then()
                    .statusCode(200);
        }
    }


    @Test
    void getBookingIdList(){
        given()
                .headers(headers)
                .when()
                .get((String)prop.get("bookerBaseUrl")+"/booking")
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @Test
    void healthCheck(){
        given()
                .headers(headers)
                .when()
                .get((String)prop.get("bookerBaseUrl")+"/ping")
                .prettyPeek()
                .then()
                .statusCode(201);
    }




    @AfterAll
    static void DeleteAll(){
        deleteFirstDateBooking();
        deleteSecondDateBooking();
    }


    static void deleteFirstDateBooking(){
        given()
                .headers(headers)
                .when()
                .delete((String)prop.get("bookerBaseUrl")+"/booking/"+firstBookingId)
                .prettyPeek()
                .then()
                .statusCode(403);

    }
    static void deleteSecondDateBooking(){
        given()
                .headers(headers)
                .when()
                .delete((String)prop.get("bookerBaseUrl")+"/booking/"+secondBookingId)
                .prettyPeek()
                .then()
                .statusCode(403);

    }
}
