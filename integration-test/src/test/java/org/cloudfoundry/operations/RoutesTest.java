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

package org.cloudfoundry.operations;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.operations.routes.CheckRouteRequest;
import org.cloudfoundry.operations.routes.CreateRouteRequest;
import org.cloudfoundry.operations.routes.ListRoutesRequest;
import org.cloudfoundry.operations.routes.MapRouteRequest;
import org.cloudfoundry.operations.routes.UnmapRouteRequest;
import org.junit.Test;

import static org.cloudfoundry.operations.routes.ListRoutesRequest.Level.ORGANIZATION;

public final class RoutesTest extends AbstractIntegrationTest {

    @Test
    public void check() {
        this.cloudFoundryOperations.routes().check(new CheckRouteRequest("domain", "host", "path"))
                .subscribe(testSubscriber()
                        .assertEquals(false));
    }

    @Test
    public void create() {
        this.cloudFoundryOperations.routes().create(new CreateRouteRequest("domain", "host", "path", spaceName))
                .subscribe(testSubscriber());
    }

//    @Test
//    public void delete() {
//        this.cloudFoundryOperations.routes().delete(new DeleteRouteRequest("domain", "host", "path"))
//                .subscribe(testSubscriber()
//                        .assertCount(1));
//    }
//
//    @Test
//    public void deleteOrphaned() {
//        this.cloudFoundryOperations.routes().deleteOrphaned(new DeleteOrphanedRouteRequest())
//                .subscribe(testSubscriber()
//                        .assertCount(1));
//    }

    @Test
    public void list() {
        this.cloudFoundryOperations.routes().list(new ListRoutesRequest(ORGANIZATION))
                .subscribe(testSubscriber()
                        .assertCount(0));
    }

    @Test
    public void map() {
        this.cloudFoundryOperations.routes().map(new MapRouteRequest("application", "domain", "host", "path"))
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

    @Test
    public void unmap() {
        this.cloudFoundryOperations.routes().unmap(new UnmapRouteRequest("application", "domain", "host"))
                .subscribe(testSubscriber()
                        .assertCount(1));
    }

}
