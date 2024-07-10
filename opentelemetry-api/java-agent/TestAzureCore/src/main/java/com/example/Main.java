package com.example;

import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpPipelineBuilder;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.core.http.policy.HttpLoggingPolicy;
import com.azure.core.http.policy.HttpPipelinePolicy;
import com.azure.core.http.policy.HttpPolicyProviders;
import com.azure.core.util.Context;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        List<HttpPipelinePolicy> policies = new ArrayList<>();
        // add policies to httpclient instance via HttpPolicyProviders.addAfterRetryPolicies(policies)
        HttpPolicyProviders.addAfterRetryPolicies(policies);
        policies.add(new HttpLoggingPolicy(new HttpLogOptions()));

        HttpPipelineBuilder pipelineBuilder = new HttpPipelineBuilder();
        pipelineBuilder.policies(policies.toArray(new HttpPipelinePolicy[0]));
        HttpPipeline httpPipeline = pipelineBuilder.build();
        com.azure.core.http.HttpResponse response = httpPipeline.sendSync(new com.azure.core.http.HttpRequest(com.azure.core.http.HttpMethod.GET, "https://www.google.com"),
            Context.NONE);
        response.close();
        System.out.println(response.getStatusCode());
    }
}