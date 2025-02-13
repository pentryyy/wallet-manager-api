package com.pentryyy.wallet_manager_api.exception;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponseBuilder {

    public Map<String, Object> createErrorResponse(String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", errorMessage);
        return response;
    }
}