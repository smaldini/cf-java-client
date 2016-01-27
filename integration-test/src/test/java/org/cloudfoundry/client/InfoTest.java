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

package org.cloudfoundry.client;

import com.github.zafarkhaja.semver.Version;
import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.v2.info.GetInfoRequest;
import org.cloudfoundry.client.v2.info.GetInfoResponse;
import org.junit.Test;

import static org.cloudfoundry.client.CloudFoundryClient.SUPPORTED_API_VERSION;
import static org.junit.Assert.assertTrue;

public final class InfoTest extends AbstractIntegrationTest {

    @Test
    public void info() {
        GetInfoRequest request = GetInfoRequest.builder()
            .build();

        this.cloudFoundryClient.info().get(request)
            .subscribe(this.<GetInfoResponse>testSubscriber()
                .assertThat(response -> {
                    Version expected = Version.valueOf(SUPPORTED_API_VERSION);
                    Version actual = Version.valueOf(response.getApiVersion());

                    assertTrue(expected.greaterThanOrEqualTo(actual));
                }));
    }

}
