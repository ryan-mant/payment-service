package com.ryan_dev.core_banking_service.adapters.out.client.authorizer.request;

import java.math.BigDecimal;

public record AuthorizerRequest(String document, BigDecimal amount) {}
