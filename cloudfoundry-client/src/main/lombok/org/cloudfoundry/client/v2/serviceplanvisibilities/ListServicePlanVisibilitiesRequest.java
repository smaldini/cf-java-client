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

package org.cloudfoundry.client.v2.serviceplanvisibilities;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v2.InFilterParameter;
import org.cloudfoundry.client.v2.PaginatedRequest;

import java.util.List;

/**
 * The request payload for the List all Service Service Plan Visibilities operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListServicePlanVisibilitiesRequest extends PaginatedRequest implements Validatable {

    /**
     * The organization ids
     *
     * @param organizationIds the organization ids
     * @return the organization ids
     */
    @Getter(onMethod = @__(@InFilterParameter("organization_guid")))
    private final List<String> organizationIds;

    /**
     * The service plan ids
     *
     * @param servicePlanIds the service plan ids
     * @return the service plan ids
     */
    @Getter(onMethod = @__(@InFilterParameter("service_plan_guid")))
    private final List<String> servicePlanIds;

    @Builder
    ListServicePlanVisibilitiesRequest(OrderDirection orderDirection, Integer page, Integer resultsPerPage,
                                       @Singular List<String> organizationIds,
                                       @Singular List<String> servicePlanIds) {
        super(orderDirection, page, resultsPerPage);
        this.organizationIds = organizationIds;
        this.servicePlanIds = servicePlanIds;
    }

    @Override
    public ValidationResult isValid() {
        return ValidationResult.builder().build();
    }

}
