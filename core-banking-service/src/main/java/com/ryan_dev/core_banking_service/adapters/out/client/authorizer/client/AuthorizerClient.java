package com.ryan_dev.core_banking_service.adapters.out.client.authorizer.client;

import com.ryan_dev.core_banking_service.adapters.out.client.authorizer.request.AuthorizerRequest;
import com.ryan_dev.core_banking_service.adapters.out.client.authorizer.response.AuthorizerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "authorizer-client", url = "${app.client.authorizer.url}")
@Component
public interface AuthorizerClient {

    @PostMapping
    AuthorizerResponse validate(@RequestBody AuthorizerRequest request);
}

