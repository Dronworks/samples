private Map<String, String> buildAgentMap(List<UHAgent> canBeAnalyzedList, List<UHAgent> canBeEvaluatedList) {
        Map<String, UserAttributes> agentMap = new HashMap<>();
        canBeAnalyzedList.forEach(agent -> agentMap.put(agent.getId(), UserAttributes.builder().canBeAnalyzed(true).build()));
        for (UHAgent userHubAgent : canBeEvaluatedList){
            if(agentMap.containsKey(userHubAgent.getId())){
                agentMap.get(userHubAgent.getId()).setCanBeCoachedOrEvaluated(true);
            }
            else{
                agentMap.put(userHubAgent.getId(),UserAttributes.builder().canBeCoachedOrEvaluated(true).build());
            }
        }
        Map<String,String> agentStringMap = new HashMap<>();
        for(Map.Entry<String, UserAttributes> agent : agentMap.entrySet()){
            try {
                agentStringMap.put(agent.getKey(), objectMapper.writeValueAsString(agent.getValue()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        if(agentStringMap.size() == 0){
            return NO_AGENTS_MAP;
        }
        return agentStringMap;
    }
