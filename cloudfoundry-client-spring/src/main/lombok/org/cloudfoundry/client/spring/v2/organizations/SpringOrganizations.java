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

package org.cloudfoundry.client.spring.v2.organizations;

import lombok.ToString;
import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.spring.util.QueryBuilder;
import org.cloudfoundry.client.spring.v2.FilterBuilder;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorByUsernameResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationAuditorResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationBillingManagerByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationBillingManagerByUsernameResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationBillingManagerRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationBillingManagerResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerByUsernameResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationManagerResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationPrivateDomainRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationPrivateDomainResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserByUsernameResponse;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserRequest;
import org.cloudfoundry.client.v2.organizations.AssociateOrganizationUserResponse;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.DeleteOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationInstanceUsageRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationInstanceUsageResponse;
import org.cloudfoundry.client.v2.organizations.GetOrganizationMemoryUsageRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationMemoryUsageResponse;
import org.cloudfoundry.client.v2.organizations.GetOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.GetOrganizationUserRolesRequest;
import org.cloudfoundry.client.v2.organizations.GetOrganizationUserRolesResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationAuditorsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationAuditorsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationBillingManagersRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationBillingManagersResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationManagersRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationManagersResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationServicesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationServicesResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpaceQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationUsersRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationUsersResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.organizations.Organizations;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationAuditorByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationAuditorRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationBillingManagerByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationBillingManagerRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationManagerByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationManagerRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationPrivateDomainRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationUserByUsernameRequest;
import org.cloudfoundry.client.v2.organizations.RemoveOrganizationUserRequest;
import org.cloudfoundry.client.v2.organizations.SummaryOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.SummaryOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.UpdateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.UpdateOrganizationResponse;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ProcessorGroup;
import reactor.fn.Consumer;

import java.net.URI;

/**
 * The Spring-based implementation of {@link Organizations}
 */
@ToString(callSuper = true)
public final class SpringOrganizations extends AbstractSpringOperations implements Organizations {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param processorGroup The group to use when making requests
     */
    public SpringOrganizations(RestOperations restOperations, URI root, ProcessorGroup<?> processorGroup) {
        super(restOperations, root, processorGroup);
    }

    @Override
    public Mono<AssociateOrganizationAuditorResponse> associateAuditor(final AssociateOrganizationAuditorRequest request) {
        return put(request, AssociateOrganizationAuditorResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "auditors",
                    request.getAuditorId());
            }

        });
    }

    @Override
    public Mono<AssociateOrganizationAuditorByUsernameResponse> associateAuditorByUsername(final AssociateOrganizationAuditorByUsernameRequest request) {
        return put(request, AssociateOrganizationAuditorByUsernameResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "auditors");
            }

        });
    }

    @Override
    public Mono<AssociateOrganizationBillingManagerResponse> associateBillingManager(final AssociateOrganizationBillingManagerRequest request) {
        return put(request, AssociateOrganizationBillingManagerResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "billing_managers",
                    request.getBillingManagerId());
            }

        });
    }

    @Override
    public Mono<AssociateOrganizationBillingManagerByUsernameResponse> associateBillingManagerByUsername(final AssociateOrganizationBillingManagerByUsernameRequest request) {
        return put(request, AssociateOrganizationBillingManagerByUsernameResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "billing_managers");
            }

        });
    }

    @Override
    public Mono<AssociateOrganizationManagerResponse> associateManager(final AssociateOrganizationManagerRequest request) {
        return put(request, AssociateOrganizationManagerResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "managers", request.getManagerId());
            }

        });
    }

    @Override
    public Mono<AssociateOrganizationManagerByUsernameResponse> associateManagerByUsername(final AssociateOrganizationManagerByUsernameRequest request) {
        return put(request, AssociateOrganizationManagerByUsernameResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "managers");
            }

        });
    }

    @Override
    public Mono<AssociateOrganizationPrivateDomainResponse> associatePrivateDomain(final AssociateOrganizationPrivateDomainRequest request) {
        return put(request, AssociateOrganizationPrivateDomainResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "private_domains", request
                    .getPrivateDomainId());
            }

        });
    }

    @Override
    public Mono<AssociateOrganizationUserResponse> associateUser(final AssociateOrganizationUserRequest request) {
        return put(request, AssociateOrganizationUserResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "users", request.getUserId());
            }

        });
    }

    @Override
    public Mono<AssociateOrganizationUserByUsernameResponse> associateUserByUsername(final AssociateOrganizationUserByUsernameRequest request) {
        return put(request, AssociateOrganizationUserByUsernameResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "users");
            }

        });
    }

    @Override
    public Mono<CreateOrganizationResponse> create(final CreateOrganizationRequest request) {
        return post(request, CreateOrganizationResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations");
            }

        });
    }

    @Override
    public Mono<Void> delete(final DeleteOrganizationRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId());
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Mono<GetOrganizationResponse> get(final GetOrganizationRequest request) {
        return get(request, GetOrganizationResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId());
            }

        });
    }

    @Override
    public Mono<GetOrganizationInstanceUsageResponse> getInstanceUsage(final GetOrganizationInstanceUsageRequest request) {
        return get(request, GetOrganizationInstanceUsageResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "instance_usage");
            }

        });
    }

    @Override
    public Mono<GetOrganizationMemoryUsageResponse> getMemoryUsage(final GetOrganizationMemoryUsageRequest request) {
        return get(request, GetOrganizationMemoryUsageResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "memory_usage");
            }

        });
    }

    @Override
    public Mono<GetOrganizationUserRolesResponse> getUserRoles(final GetOrganizationUserRolesRequest request) {
        return get(request, GetOrganizationUserRolesResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "user_roles");
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Mono<ListOrganizationsResponse> list(final ListOrganizationsRequest request) {
        return get(request, ListOrganizationsResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Mono<ListOrganizationAuditorsResponse> listAuditors(final ListOrganizationAuditorsRequest request) {
        return get(request, ListOrganizationAuditorsResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "auditors");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Mono<ListOrganizationBillingManagersResponse> listBillingManagers(final ListOrganizationBillingManagersRequest request) {
        return get(request, ListOrganizationBillingManagersResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "billing_managers");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Mono<ListOrganizationManagersResponse> listManagers(final ListOrganizationManagersRequest request) {
        return get(request, ListOrganizationManagersResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "managers");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Mono<ListOrganizationPrivateDomainsResponse> listPrivateDomains(final ListOrganizationPrivateDomainsRequest request) {
        return get(request, ListOrganizationPrivateDomainsResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "private_domains");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Mono<ListOrganizationServicesResponse> listServices(final ListOrganizationServicesRequest request) {
        return get(request, ListOrganizationServicesResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "services");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Mono<ListOrganizationSpaceQuotaDefinitionsResponse> listSpaceQuotaDefinitions(final ListOrganizationSpaceQuotaDefinitionsRequest request) {
        return get(request, ListOrganizationSpaceQuotaDefinitionsResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "space_quota_definitions");
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Mono<ListOrganizationSpacesResponse> listSpaces(final ListOrganizationSpacesRequest request) {
        return get(request, ListOrganizationSpacesResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "spaces");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Mono<ListOrganizationUsersResponse> listUsers(final ListOrganizationUsersRequest request) {
        return get(request, ListOrganizationUsersResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "users");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Mono<Void> removeAuditor(final RemoveOrganizationAuditorRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "auditors", request.getAuditorId());
            }

        });
    }

    @Override
    public Mono<Void> removeAuditorByUsername(final RemoveOrganizationAuditorByUsernameRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "auditors");
            }
        });
    }

    @Override
    public Mono<Void> removeBillingManager(final RemoveOrganizationBillingManagerRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "billing_managers", request
                    .getBillingManagerId());
            }

        });
    }

    @Override
    public Mono<Void> removeBillingManagerByUsername(final RemoveOrganizationBillingManagerByUsernameRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "billing_managers");
            }

        });
    }

    @Override
    public Mono<Void> removeManager(final RemoveOrganizationManagerRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "managers", request.getManagerId());
            }

        });
    }

    @Override
    public Mono<Void> removeManagerByUsername(final RemoveOrganizationManagerByUsernameRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "managers");
            }

        });
    }

    @Override
    public Mono<Void> removePrivateDomain(final RemoveOrganizationPrivateDomainRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "private_domains",
                    request.getPrivateDomainId());
            }

        });
    }

    @Override
    public Mono<Void> removeUser(final RemoveOrganizationUserRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "users", request.getUserId());
            }

        });
    }

    @Override
    public Mono<Void> removeUserByUsername(final RemoveOrganizationUserByUsernameRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "users");
            }

        });
    }

    @Override
    public Mono<SummaryOrganizationResponse> summary(final SummaryOrganizationRequest request) {
        return get(request, SummaryOrganizationResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId(), "summary");
            }

        });
    }

    @Override
    public Mono<UpdateOrganizationResponse> update(final UpdateOrganizationRequest request) {
        return put(request, UpdateOrganizationResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "organizations", request.getOrganizationId());
            }

        });
    }

}
