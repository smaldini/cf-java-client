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

package org.cloudfoundry.operations.routes;

import lombok.Builder;
import lombok.Data;
import org.cloudfoundry.operations.Validatable;
import org.cloudfoundry.operations.ValidationResult;

/**
 * The request options for the create route operation
 */
@Data
public final class CreateRouteRequest implements Validatable {

    /**
     * The domain of the route
     *
     * @param domain the domain
     * @return the domain
     */
    private final String domain;

    /**
     * The host name of the route.
     *
     * @param host the host name
     * @return the host name
     */
    private final String host;

    /**
     * The path of the route.
     *
     * @param path the path
     * @return the path
     */
    private final String path;

    /**
     * The space to create the route in
     *
     * @param space the space
     * @return the space
     */
    private final String space;

    @Builder
    public CreateRouteRequest(String domain, String host, String path, String space) {
        this.domain = domain;
        this.host = host;
        this.path = path;
        this.space = space;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.domain == null) {
            builder.message("domain must be specified");
        }

        if (this.space == null) {
            builder.message("space must be specified");
        }

        return builder.build();
    }

}
