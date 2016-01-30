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

package org.cloudfoundry.client.v2.spaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;
import org.cloudfoundry.client.v2.GreaterThanOrEqualToFilterParameter;
import org.cloudfoundry.client.v2.InFilterParameter;
import org.cloudfoundry.client.v2.PaginatedRequest;

import java.util.List;

/**
 * The request payload for the List all Events for the Space operation
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class ListSpaceEventsRequest extends PaginatedRequest implements Validatable {

    /**
     * The actees
     *
     * @param actees the actees
     * @return the actees
     */
    @Getter(onMethod = @__(@InFilterParameter("actee")))
    private final List<String> actees;

    /**
     * The space id
     *
     * @param spaceId the space id
     * @return the space id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String spaceId;

    /**
     * The timestamps
     *
     * @param timestamps the timestamps
     * @return the timestamps
     */
    @Getter(onMethod = @__(@GreaterThanOrEqualToFilterParameter("timestamp")))
    private final List<String> timestamps;

    /**
     * The types
     *
     * @param types the types
     * @return the types
     */
    @Getter(onMethod = @__(@InFilterParameter("type")))
    private final List<String> types;

    @Builder
    ListSpaceEventsRequest(OrderDirection orderDirection, Integer page, Integer resultsPerPage,
                           @Singular List<String> actees,
                           String spaceId,
                           @Singular List<String> timestamps,
                           @Singular List<String> types) {
        super(orderDirection, page, resultsPerPage);
        this.actees = actees;
        this.spaceId = spaceId;
        this.timestamps = timestamps;
        this.types = types;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.spaceId == null) {
            builder.message("space id must be specified");
        }

        return builder.build();
    }

}
