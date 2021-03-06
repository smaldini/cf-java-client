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

import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Applications Operations API
 */
public interface Applications {

    /**
     * Deletes a specific application and, optionally, all routes mapped to the application.
     *
     * Warning: deleting routes mapped to the application deletes them even if they are mapped to other applications.
     *
     * @param request the delete application request
     */
    Mono<Void> delete(DeleteApplicationRequest request);

    /**
     * Gets information for a specific application
     *
     * @param request the get application request
     * @return the application
     */
    Mono<ApplicationDetail> get(GetApplicationRequest request);

    /**
     * Lists the applications
     *
     * @return the applications
     */
    Publisher<ApplicationSummary> list();

    /**
     * Rename a specific application
     *
     * @param request the rename application request
     */
    Publisher<Void> rename(RenameApplicationRequest request);

    /**
     * Scales a specific application
     *
     * @param request the scale application request
     * @return the application scale
     */
    Publisher<ApplicationScale> scale(ScaleApplicationRequest request);
}
