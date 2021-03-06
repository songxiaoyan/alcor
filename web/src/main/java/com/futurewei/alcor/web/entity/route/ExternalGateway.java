/*
Copyright 2019 The Alcor Authors.

Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/
package com.futurewei.alcor.web.entity.route;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ExternalGateway {

    @JsonProperty("network_id")
    private String networkId;

    @JsonProperty("enable_snat")
    private boolean enableSnat;

    @JsonProperty("external_fixed_ips")
    private List<FixedIp> externalFixedIps;

    public ExternalGateway(String networkId, boolean enableSnat, List<FixedIp> externalFixedIps) {
        this.networkId = networkId;
        this.enableSnat = enableSnat;
        this.externalFixedIps = externalFixedIps;
    }
}
