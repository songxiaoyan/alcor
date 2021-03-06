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
package com.futurewei.alcor.dataplane.entity;

import com.futurewei.alcor.schema.Goalstate.GoalState;
import com.futurewei.alcor.web.entity.dataplane.UnicastGoalStateByte;

/**
 * UnicastGoalState contains a goalState object and the destination host ip address hostIp,
 * to which the GoalState object is sent. In order to support the hierarchical topic of pulsar,
 * it also contains the nextTopic field, which is used by pulsar to determine which topic to
 * send the goalState to. The field goalStateBuilder is a temporary object that is used to
 * construct a goalState object that must be cleared before the UnicastGoalState is sent.
 */
public class UnicastGoalState {
    private String hostIp;
    private String nextTopic;
    private GoalState goalState;
    private GoalState.Builder goalStateBuilder;

    public UnicastGoalState() {
        goalStateBuilder = GoalState.newBuilder();
    }

    public UnicastGoalState(String hostIp, GoalState goalState) {
        this.hostIp = hostIp;
        this.goalState = goalState;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getNextTopic() {
        return nextTopic;
    }

    public void setNextTopic(String nextTopic) {
        this.nextTopic = nextTopic;
    }

    public GoalState getGoalState() {
        return goalState;
    }

    public GoalState.Builder getGoalStateBuilder() {
        return goalStateBuilder;
    }

    public void setGoalStateBuilder(GoalState.Builder goalStateBuilder) {
        this.goalStateBuilder = goalStateBuilder;
    }

    public void setGoalState(GoalState goalState) {
        this.goalState = goalState;
    }

    public UnicastGoalStateByte getUnicastGoalStateByte() {
        UnicastGoalStateByte unicastGoalStateByte = new UnicastGoalStateByte();
        unicastGoalStateByte.setNextTopic(this.nextTopic);
        unicastGoalStateByte.setGoalStateByte(this.goalState.toByteArray());

        return unicastGoalStateByte;
    }
}
