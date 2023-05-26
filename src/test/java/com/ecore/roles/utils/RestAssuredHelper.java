package com.ecore.roles.utils;

import com.ecore.roles.constants.RestConstants;
import com.ecore.roles.model.Membership;
import com.ecore.roles.model.Role;
import com.ecore.roles.web.dto.MembershipDto;
import com.ecore.roles.web.dto.RoleDto;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;

import java.util.UUID;

import static com.ecore.roles.constants.RestConstants.V1_ROLES_FIND;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.http.ContentType.JSON;

public class RestAssuredHelper {

    public static void setUp(int port) {
        RestAssured.reset();
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.baseURI = "http://localhost:" + port;
    }

    public static EcoreValidatableResponse sendRequest(ValidatableResponse validatableResponse) {
        return new EcoreValidatableResponse(validatableResponse);
    }

    public static EcoreValidatableResponse createRole(Role role) {
        return sendRequest(givenNullableBody(RoleDto.fromModel(role))
                .contentType(JSON)
                .when()
                .post(RestConstants.V1_ROLES)
                .then());
    }

    public static EcoreValidatableResponse getRoles() {
        return sendRequest(when()
                .get(RestConstants.V1_ROLES)
                .then());
    }

    public static EcoreValidatableResponse getRole(UUID roleId) {
        return sendRequest(given()
                .pathParam("roleId", roleId)
                .when()
                .get(RestConstants.V1_ROLES_ROLE_ID)
                .then());
    }

    public static EcoreValidatableResponse getRole(UUID userID, UUID teamID) {
        return sendRequest(given()
                .queryParam("userID", userID)
                .queryParam("teamID", teamID)
                .when()
                .get(V1_ROLES_FIND)
                .then());
    }

    public static EcoreValidatableResponse createMembership(Membership membership) {
        return sendRequest(givenNullableBody(MembershipDto.fromModel(membership))
                .contentType(JSON)
                .when()
                .post(RestConstants.V1_ROLES_MEMBERSHIPS)
                .then());
    }

    public static EcoreValidatableResponse getMemberships(UUID roleId) {
        return sendRequest(given()
                .queryParam("roleId", roleId)
                .when()
                .get(RestConstants.V1_ROLES_MEMBERSHIPS_SEARCH)
                .then());
    }
    
    public static EcoreValidatableResponse getTeams() {
    	 return sendRequest(when()
                 .get("/v1/teams")
                 .then());
    }
    
    public static EcoreValidatableResponse getTeam(UUID teamId) {
    	return sendRequest(given()
                .pathParam("teamId", teamId)
                .when()
                .get("/v1/teams/{teamId}")
                .then());
   }

    private static RequestSpecification givenNullableBody(Object object) {
        RequestSpecification requestSpecification = given();
        if (object != null) {
            requestSpecification = requestSpecification.body(object);
        }
        return requestSpecification;
    }

    public static class EcoreValidatableResponse {

        ValidatableResponse validatableResponse;

        public EcoreValidatableResponse(ValidatableResponse validatableResponse) {
            this.validatableResponse = validatableResponse;
        }

        public void validate(int status, String message) {
            this.validatableResponse
                    .statusCode(status)
                    .body("status", Matchers.equalTo(status))
                    .body("error", Matchers.equalTo(message));
        }

        public ValidatableResponse statusCode(int statusCode) {
            return this.validatableResponse
                    .statusCode(statusCode);
        }

        public ExtractableResponse<Response> extract() {
            return this.validatableResponse
                    .extract();
        }

    }
}
