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

package org.cloudfoundry.client.v3.processes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.cloudfoundry.client.Validatable;
import org.cloudfoundry.client.ValidationResult;

/**
 * The request payload for the Update Process operation
 */
@Data
public final class UpdateProcessRequest implements Validatable {

    /**
     * The command
     *
     * @param command the command
     * @return the command
     */
    @Getter(onMethod = @__(@JsonProperty("command")))
    private final String command;

    /**
     * The process id
     *
     * @param processId the process id
     * @return the process id
     */
    @Getter(onMethod = @__(@JsonIgnore))
    private final String processId;

    @Builder
    UpdateProcessRequest(String command, String processId) {
        this.command = command;
        this.processId = processId;
    }

    @Override
    public ValidationResult isValid() {
        ValidationResult.ValidationResultBuilder builder = ValidationResult.builder();

        if (this.processId == null) {
            builder.message("process id must be specified");
        }

        if (this.command == null) {
            builder.message("command must be specified");
        }

        return builder.build();
    }
}
