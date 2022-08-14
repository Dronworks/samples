public UserHubResponse<? extends UserHubAgent> getPage(final TenantData tenantData, final AttributeType attribute, int skip)
    {
        String token = lambdaServiceTokenService.getLambdaServiceToken();

        HttpEntity<UserHubSearchRequest> request = setHeadersAndGetRequestBody(attribute.getDescription(), skip, token);
        String userHubSearchUrl = String.join("", cxOneBaseUri, userHubSearchAPI, tenantData.getTenantName());
        UserHubResponse<? extends UserHubAgent> response;
        if(attribute == AttributeType.CAN_BE_COACHED_OR_EVALUATED) {
            response = getCanBeCoachedOrEvaluated(userHubSearchUrl, request);
        } else if (attribute == AttributeType.CAN_BE_ANALYZED) {
            response = getCanBeAnalyzed(userHubSearchUrl, request);
        } else {
            throw new UnsupportedOperationException(attribute.getDescription());
        }

        log.info("Got page for tenant: '{}', attribute: '{}'", tenantData.getTenantName(), attribute);
        metricsService.userHub(MetricsService.COUNTER_TAG_NAME_STATE_SUCCESS);
        return response;
    }

    public UserHubResponse<? extends UserHubAgent> getCanBeCoachedOrEvaluated(String userHubSearchUrl, HttpEntity<UserHubSearchRequest> request) {
        ResponseEntity<UserHubResponse<UserHubAgentCoachedEvaluated>> uhAgentCoachedEvaluatedResponse = restTemplate.exchange(userHubSearchUrl,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {
                });
        return uhAgentCoachedEvaluatedResponse.getBody();
    }

    public UserHubResponse<? extends UserHubAgent> getCanBeAnalyzed(String userHubSearchUrl, HttpEntity<UserHubSearchRequest> request) {
        ResponseEntity<UserHubResponse<UserHubAgentAnalyzable>> uhAgentCoachedEvaluatedResponse = restTemplate.exchange(userHubSearchUrl,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {
                });
        return uhAgentCoachedEvaluatedResponse.getBody();
    }
