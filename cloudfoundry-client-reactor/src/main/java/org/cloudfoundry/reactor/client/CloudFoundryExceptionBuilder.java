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

package org.cloudfoundry.reactor.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.client.v2.CloudFoundryException;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClientException;

import java.io.IOException;
import java.util.Map;

public final class CloudFoundryExceptionBuilder {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private CloudFoundryExceptionBuilder() {
    }

    /**
     * Build a {@link CloudFoundryException} from an {@link HttpClientException}
     *
     * @param cause the cause
     * @param <T>   The type of the {@link Mono}
     * @return a {@link Mono#error} with a properly configured {@link CloudFoundryException}
     */
    @SuppressWarnings("unchecked")
    public static <T> Mono<T> build(HttpClientException cause) {
        return cause.getResponse().receive().aggregate().asInputStream()
            .then(in -> {
                try {
                    Map<String, ?> response = OBJECT_MAPPER.readValue(in, Map.class);
                    Integer code = (Integer) response.get("code");
                    String description = (String) response.get("description");
                    String errorCode = (String) response.get("error_code");

                    return Mono.error(new CloudFoundryException(code, description, errorCode, cause));
                } catch (IOException e) {
                    throw Exceptions.propagate(cause);
                }
            });
    }

}
