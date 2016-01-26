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

package org.cloudfoundry.operations.applications;

import lombok.Builder;
import lombok.Data;
import org.cloudfoundry.operations.Validatable;
import org.cloudfoundry.operations.ValidationResult;

import java.io.File;

/**
 * The request options for the push application operation
 */
@Data
public final class PushApplicationRequest implements Validatable {

    /**
     * The application directory or a ZIP archive file type (e.g. JAR, WAR)
     *
     * @param application the application
     * @return the application
     */
    private final File application;

    /**
     * Custom buildpack by name (e.g. {@code my-buildpack}) or Git URL (e.g. {@code https://github.com/cloudfoundry/java-buildpack.git}) or Git URL with a branch or tag (e.g. {@code
     * https://github.com/cloudfoundry/java-buildpack.git#v3.3.0} for {@code v3.3.0} tag). To use built-in buildpacks only, specify {@code default} or {@code null}.
     *
     * @param buildpack custom buildpack by name
     * @return custom buildpack by name
     */
    private final String buildpack;

    /**
     * Startup command, set to {@code null} to reset to default start command
     *
     * @param command the startup command
     * @return the startup command
     */
    private final String command;

    /**
     * Whether to create a random route for this application
     *
     * @param createRandomRoute whether to create a random route
     * @return whether to create a random route
     */
    private final Boolean createRandomRoute;

    /**
     * The disk limit (e.g. 256M, 1024M, 1G)
     *
     * @param diskLimit the disk limit
     * @return the disk limit
     */
    private final String diskLimit;

    /**
     * The Docker image to be used (e.g. {@code user/docker-image-name})
     *
     * @param dockerImage the Docker image to use
     * @return the Docker image to use
     */
    private final String dockerImage;

    /**
     * The domain (e.g. {@code example.com})
     *
     * @param domain the domain
     * @return the domain
     */
    private final String domain;

    /**
     * The application health check type
     *
     * @param healthCheckType the application health check type
     * @return the application health check type
     */
    private final HealthCheckType healthCheckType;

    /**
     * The hostname (e.g. {@code my-subdomain})
     *
     * @param hostname the hostname
     * @return the hostname
     */
    private final String hostname;

    /**
     * The number of instances
     *
     * @param instances the number of instances
     * @return the number of instances
     */
    private final Integer instances;

    /**
     * Map the root domain to this application
     *
     * @param mapRootDomain whether to map the root domain
     * @return whether to map the root domain
     */
    private final Boolean mapRootDomain;

    /**
     * Whether to map a route to this application and remove routes from previous pushes of this application
     *
     * @param mapRoute whether to map a route to this application
     * @return whether to map a route to this application
     */
    private final Boolean mapRoute;

    /**
     * The memory limit (e.g. 256M, 1024M, 1G)
     *
     * @param memoryLimit the memory limit
     * @return the memory limit
     */
    private final String memoryLimit;

    /**
     * The name of the application
     *
     * @param name the name of the application
     * @return the name of the application
     */
    private final String name;

    /**
     * The path for the route
     *
     * @param routePath the path for the route
     * @return the path for the route
     */
    private final String routePath;

    /**
     * The stack to use
     *
     * @param stack the stack to use
     * @return the stack to use
     */
    private final String stack;

    /**
     * Whether to start application after pushing
     *
     * @param start whether to start application
     * @return whether to start application
     */
    private final Boolean start;

    @Builder
    PushApplicationRequest(File application,
                           String buildpack,
                           String command,
                           Boolean createRandomRoute,
                           String diskLimit,
                           String dockerImage,
                           String domain,
                           HealthCheckType healthCheckType,
                           String hostname,
                           Integer instances,
                           Boolean mapRootDomain,
                           Boolean mapRoute,
                           String memoryLimit,
                           String name,
                           String routePath,
                           String stack,
                           Boolean start) {
        this.application = application;
        this.buildpack = buildpack;
        this.command = command;
        this.createRandomRoute = createRandomRoute;
        this.diskLimit = diskLimit;
        this.dockerImage = dockerImage;
        this.domain = domain;
        this.healthCheckType = healthCheckType;
        this.hostname = hostname;
        this.instances = instances;
        this.mapRootDomain = mapRootDomain;
        this.mapRoute = mapRoute;
        this.memoryLimit = memoryLimit;
        this.name = name;
        this.routePath = routePath;
        this.stack = stack;
        this.start = start;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.application == null) {
            builder.message("application must be specified");
        }

        if (this.name == null) {
            builder.message("name must be specified");
        }

        return builder.build();
    }

    /**
     * The application health check types
     */
    public enum HealthCheckType {

        /**
         * No health check
         */
        NONE,

        /**
         * Port health check
         */
        PORT
    }

}
