package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.teamAssignment.teamAssignmentStrategy;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TeamAssignmentStrategyFactory {

    private final Map<String, TeamAssignmentStrategy> strategies;

    public TeamAssignmentStrategyFactory(Map<String, TeamAssignmentStrategy> strategies) {
        this.strategies = strategies;
    }

    public TeamAssignmentStrategy getStrategy(TeamAssignmentStrategyEnum strategyEnum) {
        TeamAssignmentStrategy strategy = strategies.get(strategyEnum.name());
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy not found: " + strategyEnum);
        }
        return strategy;
    }
}