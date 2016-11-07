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

package org.cloudfoundry.reactor;

import org.junit.Ignore;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.time.Duration;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public final class DefaultConnectionContextTest extends AbstractRestTest {

    private final ConnectionContext connectionContext = DefaultConnectionContext.builder()
        .apiHost(this.mockWebServer.getHostName())
        .port(this.mockWebServer.getPort())
        .secure(false)
        .build();

    @Test
    public void getInfo() throws Exception {
        mockRequest(InteractionContext.builder()
            .request(TestRequest.builder()
                .method(GET).path("/v2/info")
                .build())
            .response(TestResponse.builder()
                .status(OK)
                .payload("fixtures/client/v2/info/GET_response.json")
                .build())
            .build());

        this.connectionContext
            .getRoot("authorization_endpoint")
            .as(StepVerifier::create)
            .expectNext("http://localhost:8080/uaa")
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

}
