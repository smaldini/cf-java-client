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

package org.cloudfoundry.operations.spaces;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Spaces Operations API
 */
public interface Spaces {

    /**
     * Gets space information
     *
     * @param getSpaceRequest details of space information required
     * @return the space information
     */
    Mono<SpaceDetail> get(GetSpaceRequest getSpaceRequest);

    /**
     * Lists the spaces
     *
     * @return the spaces
     */
    Publisher<SpaceSummary> list();

}
