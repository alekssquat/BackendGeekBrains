package org.example;


import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.specification.Argument;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static io.restassured.RestAssured.*;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Unit test for simple App.
 */
public class ShutterstockAPITest
{
    static Map<String,String> headers = new HashMap<>();
    static Properties prop=new Properties();
    static String token="Bearer v2/OFRpVTNBUjRkSjZHbGQzeUd6b2NzaUV6M2tQbU01R2cvMzE4NTc4MzM1L2N1c3RvbWVyLzQvUEZYMXVBMGxKMExDMFY4NHB2RmMyWS1NTzF5WmdFaW02Nk83Wk1tYnk0azVxU0RGTGU5clNHXy1VOHpKOTNxaXJsOHJtOXJ4amdzcXhfYmtTcUJTczRuTU85UW5hZ0g4WFR0VFVDRjBZYUs3MFppUVlaMVkxeXBGc3E5LWlCeVFjbWwzZEN3Q0NaSFUtMXpscUFpWVhlenk0SHE4alBZclljeDBMdlJxcmQtNExGaEZOMUtEUkdoWmFteF9kLVBWVUY5aXloME9sek1GU3FjWEstamg2US9KbTN0aDJzQ3ZUZVRReWhZXzhteDBn";
    static String baseUrl="https://api.shutterstock.com";

    @BeforeAll
    static void setup()throws IOException{
        RestAssured.filters(new AllureRestAssured());
        //headers.put(MyProp.authTokenShutterStockHeader, token);
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        FileInputStream fis;
        fis=new FileInputStream("src/test/resources/my.properties");
        prop.load(fis);

        headers.put(MyProp.authTokenShutterStockHeader, (String) prop.get("token"));
        headers.put(MyProp.ContentType,MyProp.applicationJson);

    }

    @Test
    void getImagePrettyPeek(){
        String result = given()
                .log()
                .method()
                .headers(headers)
                .when()
                .get((String)prop.get("baseUrl")+"/v2/images/search")
                .prettyPeek()
                .then()
                .statusCode(200)
                .contentType(MyProp.applicationJson)
                .extract()
                .response()
                .jsonPath()
                .getString("data[0].id");
        System.out.println("ВЫВОД ЗАПРОСА ---> "+result);
    }

    @Test
    void getImage(){
        given()
                .headers(headers)
                .expect()
                .body("page",equalTo(1))
                .when()
                .get((String)prop.get("baseUrl")+"/v2/images/search")
                .then()
                .statusCode(200)
                .contentType("application/json");
    }

    @Test
    void getKeyWords() throws FileNotFoundException {
        given()
                .contentType(MyProp.applicationJson)
                .accept(MyProp.applicationJson)
                .log()
                .method()
                .body(new FileInputStream("src/test/resources/plaintext.json"))
                //.body((String) prop.get("plaintext"))
                .headers(headers)
                .when()
                .post((String)prop.get("baseUrl")+"/v2/images/search/suggestions")
                .prettyPeek()
                .then()
                .statusCode(200);
    }


}
