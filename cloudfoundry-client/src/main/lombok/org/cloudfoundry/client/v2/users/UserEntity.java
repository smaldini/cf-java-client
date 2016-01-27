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

package org.cloudfoundry.client.v2.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The entity response payload for the User resource
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class UserEntity extends AbstractUserEntity {

    @Builder
    UserEntity(@JsonProperty("active") Boolean active,
               @JsonProperty("admin") Boolean admin,
               @JsonProperty("audited_organizations_url") String auditedOrganizationsUrl,
               @JsonProperty("audited_spaces_url") String auditedSpacesUrl,
               @JsonProperty("billing_managed_organizations_url") String billingManagedOrganizationsUrl,
               @JsonProperty("default_space_guid") String defaultSpaceUrl,
               @JsonProperty("default_space_url") String defaultSpaceId,
               @JsonProperty("managed_organizations_url") String managedOrganizationsUrl,
               @JsonProperty("managed_spaces_url") String managedSpacesUrl,
               @JsonProperty("organizations_url") String organizationsUrl,
               @JsonProperty("spaces_url") String spacesUrl,
               @JsonProperty("username") String username) {
        super(active, admin, auditedOrganizationsUrl, auditedSpacesUrl, billingManagedOrganizationsUrl, defaultSpaceId, defaultSpaceUrl, managedOrganizationsUrl, managedSpacesUrl, organizationsUrl,
            spacesUrl, username);
    }

}
