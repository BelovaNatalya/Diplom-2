package client;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class ApiClient {
    public static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api/";
    public static RequestSpecification getBaseSpecification() {
        return new RequestSpecBuilder()
                .log(LogDetail.ALL)
                .setContentType(ContentType.JSON)
                .setBaseUri(BASE_URL)
                .build();
    }
}