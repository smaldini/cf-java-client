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

package org.cloudfoundry.client.v2.organizations;

import org.cloudfoundry.client.ValidationResult;
import org.junit.Test;

import static org.cloudfoundry.client.ValidationResult.Status.INVALID;
import static org.cloudfoundry.client.ValidationResult.Status.VALID;
import static org.junit.Assert.assertEquals;

public final class AssociateOrganizationPrivateDomainRequestTest {

    @Test
    public void isValid() {
        ValidationResult result = AssociateOrganizationPrivateDomainRequest.builder()
            .organizationId("test-organization-id")
            .privateDomainId("test-private-domain-id")
            .build()
            .isValid();

        assertEquals(VALID, result.getStatus());
    }

    @Test
    public void isValidNoId() {
        ValidationResult result = AssociateOrganizationPrivateDomainRequest.builder()
            .privateDomainId("test-private-domain-id")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("organization id must be specified", result.getMessages().get(0));
    }

    @Test
    public void isValidNoPrivateDomainId() {
        ValidationResult result = AssociateOrganizationPrivateDomainRequest.builder()
            .organizationId("test-organization-id")
            .build()
            .isValid();

        assertEquals(INVALID, result.getStatus());
        assertEquals("private domain id must be specified", result.getMessages().get(0));
    }

}
