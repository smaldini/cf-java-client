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

package org.cloudfoundry.operations.domains;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainRequest;
import org.cloudfoundry.client.v2.privatedomains.CreatePrivateDomainResponse;
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.utils.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.operations.util.v2.TestObjects.fill;
import static org.cloudfoundry.operations.util.v2.TestObjects.fillPage;
import static org.mockito.Mockito.when;

public final class DefaultDomainsTest {

    private static void requestCreateDomain(CloudFoundryClient cloudFoundryClient, String domain, String organizationId) {
        when(cloudFoundryClient.privateDomains()
            .create(CreatePrivateDomainRequest.builder()
                .name(domain)
                .owningOrganizationId(organizationId)
                .build()))
            .thenReturn(Mono
                .just(CreatePrivateDomainResponse.builder().build()));
    }

    private static void requestOrganizations(CloudFoundryClient cloudFoundryClient, String organization) {
        when(cloudFoundryClient.organizations()
            .list(fillPage(ListOrganizationsRequest.builder())
                .name(organization)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationsResponse.builder())
                    .resource(fill(OrganizationResource.builder(), "organization-").build())
                    .totalPages(1)
                    .build()));
    }

    private static void requestOrganizationsNoResults(CloudFoundryClient cloudFoundryClient, String organization) {
        when(cloudFoundryClient.organizations()
            .list(fillPage(ListOrganizationsRequest.builder())
                .name(organization)
                .page(1)
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListOrganizationsResponse.builder())
                    .totalPages(1)
                    .build()));
    }

    public static final class CreateDomain extends AbstractOperationsApiTest<Void> {

        private final DefaultDomains domains = new DefaultDomains(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            requestOrganizations(this.cloudFoundryClient, "test-organization");
            requestCreateDomain(this.cloudFoundryClient, "test-domain", "test-organization-id");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<Void> invoke() {
            CreateDomainRequest request = CreateDomainRequest.builder()
                .domain("test-domain")
                .organization("test-organization")
                .build();

            return this.domains.create(request);
        }

    }

    public static final class CreateDomainInvalidOrganization extends AbstractOperationsApiTest<Void> {

        private final DefaultDomains domains = new DefaultDomains(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            requestOrganizationsNoResults(this.cloudFoundryClient, "test-organization");
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) throws Exception {
            testSubscriber
                .assertError(IllegalArgumentException.class);
        }

        @Override
        protected Publisher<Void> invoke() {
            CreateDomainRequest request = CreateDomainRequest.builder()
                .domain("test-domain")
                .organization("test-organization")
                .build();

            return this.domains.create(request);
        }

    }

}
