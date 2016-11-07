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

package org.cloudfoundry.reactor.uaa;

import org.cloudfoundry.uaa.IdentityZoned;
import org.junit.Test;
import reactor.ipc.netty.http.client.HttpClientRequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public final class IdentityZoneBuilderTest {

    private final HttpClientRequest outbound = mock(HttpClientRequest.class);

    @Test
    public void augment() {
        IdentityZoneBuilder.augment(this.outbound, new StubIdentityZoned());

        verify(this.outbound).addHeader("X-Identity-Zone-Id", "test-identity-zone-id");
    }

    @Test
    public void augmentNotIdentityZoned() {
        IdentityZoneBuilder.augment(this.outbound, new Object());

        verifyZeroInteractions(this.outbound);
    }

    private static final class StubIdentityZoned implements IdentityZoned {

        @Override
        public String getIdentityZoneId() {
            return "test-identity-zone-id";
        }

    }

}
