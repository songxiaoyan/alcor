package com.futurewei.alcor.networkconfiguration.service;

import com.futurewei.alcor.schema.GoalStateProvisionerGrpc;
import com.futurewei.alcor.schema.Goalstate;
import com.futurewei.alcor.schema.Goalstateprovisioner;
import com.futurewei.alcor.schema.Port;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class GoalStateProvisionerGrpcService extends GoalStateProvisionerGrpc.GoalStateProvisionerImplBase {

    @Override
    public StreamObserver<Goalstate.GoalStateV2> pushGoalStatesStream(StreamObserver<Goalstateprovisioner.GoalStateOperationReply> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(Goalstate.GoalStateV2 goalStateV2) {
                log.info("NCM grpc server get message: {}", goalStateV2);
                processRequestStream(goalStateV2);
            }

            @Override
            public void onError(Throwable throwable) {
                log.info("An unknown error occurred");
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    private void processRequestStream(Goalstate.GoalStateV2 goalStateV2) {
        // TODO: Process the received message
        Map<String, Port.PortState> portStateMap = goalStateV2.getPortStatesMap();
        for (Map.Entry<String, Port.PortState> portStateEntry : portStateMap.entrySet()) {
            Port.PortConfiguration configuration = portStateEntry.getValue().getConfiguration();
        }
    }


}
