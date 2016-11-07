/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.uaa;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.uaa.users.ChangeUserPasswordRequest;
import org.cloudfoundry.uaa.users.CreateUserRequest;
import org.cloudfoundry.uaa.users.CreateUserResponse;
import org.cloudfoundry.uaa.users.DeleteUserRequest;
import org.cloudfoundry.uaa.users.DeleteUserResponse;
import org.cloudfoundry.uaa.users.Email;
import org.cloudfoundry.uaa.users.GetUserVerificationLinkRequest;
import org.cloudfoundry.uaa.users.GetUserVerificationLinkResponse;
import org.cloudfoundry.uaa.users.InviteUsersRequest;
import org.cloudfoundry.uaa.users.InviteUsersResponse;
import org.cloudfoundry.uaa.users.ListUsersRequest;
import org.cloudfoundry.uaa.users.ListUsersResponse;
import org.cloudfoundry.uaa.users.LookupUserIdsRequest;
import org.cloudfoundry.uaa.users.LookupUserIdsResponse;
import org.cloudfoundry.uaa.users.Name;
import org.cloudfoundry.uaa.users.UpdateUserRequest;
import org.cloudfoundry.uaa.users.User;
import org.cloudfoundry.uaa.users.UserId;
import org.cloudfoundry.uaa.users.VerifyUserRequest;
import org.cloudfoundry.uaa.users.VerifyUserResponse;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

public final class UsersTest extends AbstractIntegrationTest {

    @Autowired
    private String password;

    @Autowired
    private UaaClient uaaClient;

    @Autowired
    private String username;

    @Ignore("TODO: Refresh token after password change")
    @Test
    public void changePassword() throws TimeoutException, InterruptedException {
        getUserIdByUsername(this.uaaClient, this.username)
            .then(userId -> this.uaaClient.users()
                .changePassword(ChangeUserPasswordRequest.builder()
                    .oldPassword(this.password)
                    .password("test-new-password")
                    .userId(userId)
                    .build()))
            .as(StepVerifier::create)
            .consumeNextWith(response -> {
                assertThat(response.getMessage()).isEqualTo("password updated");
                assertThat(response.getStatus()).isEqualTo("ok");
            })
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void create() throws TimeoutException, InterruptedException {
        String userName = this.nameFactory.getUserName();

        this.uaaClient.users()
            .create(CreateUserRequest.builder()
                .email(Email.builder()
                    .value("test-email")
                    .primary(true)
                    .build())
                .externalId("test-external-id")
                .name(Name.builder()
                    .familyName("test-family-name")
                    .givenName("test-given-name")
                    .build())
                .password("test-password")
                .userName(userName)
                .build())
            .as(StepVerifier::create)
            .consumeNextWith(response -> {
                assertThat(response.getExternalId()).isEqualTo("test-external-id");
                assertThat(response.getUserName()).isEqualTo(userName);
            })
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() throws TimeoutException, InterruptedException {
        String userName = this.nameFactory.getUserName();

        createUserId(this.uaaClient, userName)
            .then(userId -> this.uaaClient.users()
                .delete(DeleteUserRequest.builder()
                    .userId(userId)
                    .build()))
            .map(DeleteUserResponse::getId)
            .then(userId -> requestListUsers(this.uaaClient, userId))
            .map(ListUsersResponse::getTotalResults)
            .as(StepVerifier::create)
            .expectNext(0)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void getVerificationLink() throws TimeoutException, InterruptedException {
        String userName = this.nameFactory.getUserName();

        createUserId(this.uaaClient, userName)
            .then(userId -> this.uaaClient.users()
                .getVerificationLink(GetUserVerificationLinkRequest.builder()
                    .redirectUri("test-redirect-uri")
                    .userId(userId)
                    .build()))
            .map(GetUserVerificationLinkResponse::getVerifyLink)
            .as(StepVerifier::create)
            .consumeNextWith(location -> assertThat(location).contains("/verify_user?code="))
            .expectComplete();
    }

    @Test
    public void invite() throws TimeoutException, InterruptedException {
        this.uaaClient.users()
            .invite(InviteUsersRequest.builder()
                .email("test@email.address")
                .redirectUri("test-redirect-uri")
                .build())
            .flatMapIterable(InviteUsersResponse::getNewInvites)
            .single()
            .as(StepVerifier::create)
            .consumeNextWith(invite -> {
                assertThat(invite.getEmail()).isEqualTo("test@email.address");
                assertThat(invite.getErrorCode()).isNull();
                assertThat(invite.getSuccess()).isTrue();
            })
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() throws TimeoutException, InterruptedException {
        String userName = this.nameFactory.getUserName();

        createUserId(this.uaaClient, userName)
            .then(userId -> this.uaaClient.users()
                .list(ListUsersRequest.builder()
                    .filter(String.format("id eq \"%s\"", userId))
                    .build()))
            .flatMapIterable(ListUsersResponse::getResources)
            .map(User::getUserName)
            .as(StepVerifier::create)
            .expectNext(userName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void lookup() throws TimeoutException, InterruptedException {
        String userName = this.nameFactory.getUserName();

        createUserId(this.uaaClient, userName)
            .then(userId -> this.uaaClient.users()
                .lookup(LookupUserIdsRequest.builder()
                    .filter(String.format("id eq \"%s\"", userId))
                    .build()))
            .flatMapIterable(LookupUserIdsResponse::getResources)
            .map(UserId::getUserName)
            .as(StepVerifier::create)
            .expectNext(userName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void update() throws TimeoutException, InterruptedException {
        String userName = this.nameFactory.getUserName();

        createUserId(this.uaaClient, userName)
            .then(userId -> this.uaaClient.users()
                .update(UpdateUserRequest.builder()
                    .email(Email.builder()
                        .value("test-email-2")
                        .primary(true)
                        .build())
                    .id(userId)
                    .name(Name.builder()
                        .familyName("test-family-name")
                        .givenName("test-given-name")
                        .build())
                    .userName(userName)
                    .version("0")
                    .build()))
            .flatMap(response -> Flux.fromIterable(response.getEmail())
                .map(Email::getValue))
            .as(StepVerifier::create)
            .expectNext("test-email-2")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void verifyUser() throws TimeoutException, InterruptedException {
        String userName = this.nameFactory.getUserName();

        createUserId(this.uaaClient, userName)
            .then(userId -> this.uaaClient.users()
                .verify(VerifyUserRequest.builder()
                    .userId(userId)
                    .build()))
            .map(VerifyUserResponse::getUserName)
            .as(StepVerifier::create)
            .expectNext(userName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createUserId(UaaClient uaaClient, String userName) {
        return requestCreateUser(uaaClient, userName)
            .map(CreateUserResponse::getId);
    }

    private static Mono<String> getUserIdByUsername(UaaClient uaaClient, String username) {
        return requestLookupByUsername(uaaClient, username)
            .flatMapIterable(LookupUserIdsResponse::getResources)
            .map(UserId::getId)
            .single();
    }

    private static Mono<CreateUserResponse> requestCreateUser(UaaClient uaaClient, String userName) {
        return uaaClient.users()
            .create(CreateUserRequest.builder()
                .email(Email.builder()
                    .value("test-email")
                    .primary(true)
                    .build())
                .name(Name.builder()
                    .familyName("test-family-name")
                    .givenName("test-given-name")
                    .build())
                .password("test-password")
                .verified(false)
                .userName(userName)
                .build());
    }

    private static Mono<ListUsersResponse> requestListUsers(UaaClient uaaClient, String userId) {
        return uaaClient.users()
            .list(ListUsersRequest.builder()
                .filter(String.format("id eq \"%s\"", userId))
                .build());
    }

    private static Mono<LookupUserIdsResponse> requestLookupByUsername(UaaClient uaaClient, String username) {
        return uaaClient.users()
            .lookup(LookupUserIdsRequest.builder()
                .filter(String.format("userName eq \"%s\"", username))
                .build());
    }

}
