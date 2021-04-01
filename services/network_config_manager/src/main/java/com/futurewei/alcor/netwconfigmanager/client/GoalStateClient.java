package com.futurewei.alcor.netwconfigmanager.client;

import com.futurewei.alcor.netwconfigmanager.entity.HostGoalState;
import com.futurewei.alcor.schema.Goalstateprovisioner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface GoalStateClient {
    Map<String, List<Goalstateprovisioner.GoalStateOperationReply.GoalStateOperationStatus>> sendGoalStates(Map<String, HostGoalState> hostGoalStates) throws Exception;
}
