package com.futurewei.alcor.netwconfigmanager.client.gRPC;

import com.futurewei.alcor.netwconfigmanager.client.GoalStateClient;
import com.futurewei.alcor.netwconfigmanager.config.Config;
import com.futurewei.alcor.netwconfigmanager.entity.HostGoalState;
import com.futurewei.alcor.schema.GoalStateProvisionerGrpc;
import com.futurewei.alcor.schema.Goalstate;
import com.futurewei.alcor.schema.Goalstateprovisioner;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service("grpcGoalStateClient")
public class GoalStateClientImpl implements GoalStateClient {
    private static final Logger LOG = LoggerFactory.getLogger(GoalStateClientImpl.class);

    private int hostAgentPort;

    private final ExecutorService executor;

    //    @Autowired
    public GoalStateClientImpl() {
//        this.grpcPort = globalConfig.targetHostPort;
//        this.executor = new ThreadPoolExecutor(globalConfig.grpcMinThreads,
//                globalConfig.grpcMaxThreads,
//                50,
//                TimeUnit.SECONDS,
//                new LinkedBlockingDeque<>(),
//                new DefaultThreadFactory(globalConfig.grpThreadsName));
        this.hostAgentPort = 50001;
        this.executor = new ThreadPoolExecutor(100,
                200,
                50,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                new DefaultThreadFactory("grpc-thread-pool"));
    }

    @Override
    public Map<String, List<Goalstateprovisioner.GoalStateOperationReply.GoalStateOperationStatus>> sendGoalStates(Map<String, HostGoalState> hostGoalStates) throws Exception {
        List<Future<Map<String, List<Goalstateprovisioner.GoalStateOperationReply.GoalStateOperationStatus>>>>
                futures = new ArrayList<>(hostGoalStates.size());
        Map<String, List<Goalstateprovisioner.GoalStateOperationReply.GoalStateOperationStatus>> result = new HashMap<>();

        for (HostGoalState hostGoalState : hostGoalStates.values()) {
            Future<Map<String, List<Goalstateprovisioner.GoalStateOperationReply.GoalStateOperationStatus>>> future =
                    executor.submit(() -> {
                        try {
                            return doSendGoalState(hostGoalState);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return Collections.emptyMap();
                        }
                    });
            futures.add(future);
        }

        //Handle all failed hosts
        for (Future<Map<String, List<Goalstateprovisioner.GoalStateOperationReply.GoalStateOperationStatus>>> future : futures) {
            result.putAll(future.get());
        }

        return result;
    }

    private Map<String, List<Goalstateprovisioner.GoalStateOperationReply.GoalStateOperationStatus>> doSendGoalState(HostGoalState hostGoalState) throws InterruptedException {

        String hostIp = hostGoalState.getHostIp();
        Goalstate.GoalStateV2 goalState = hostGoalState.getGoalState();

        Map<String, List<Goalstateprovisioner.GoalStateOperationReply.GoalStateOperationStatus>> result = new HashMap<>();

        ManagedChannel channel = ManagedChannelBuilder.forAddress(hostIp, this.hostAgentPort)
                .usePlaintext()
                .build();
        GoalStateProvisionerGrpc.GoalStateProvisionerStub stub = GoalStateProvisionerGrpc.newStub(channel);

        StreamObserver<Goalstate.GoalStateV2> stateV2StreamObserver = stub.pushGoalStatesStream(new StreamObserver<>() {
            @Override
            public void onNext(Goalstateprovisioner.GoalStateOperationReply goalStateOperationReply) {
                List<Goalstateprovisioner.GoalStateOperationReply.GoalStateOperationStatus> operationStatusesList
                        = goalStateOperationReply.getOperationStatusesList();
                result.put(hostIp, operationStatusesList);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        });

        stateV2StreamObserver.onNext(goalState);
        stateV2StreamObserver.onCompleted();

        shutdown(channel);
        return result;
    }

    private void shutdown(ManagedChannel channel) {
        try {
            channel.shutdown().awaitTermination(Config.SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.warn("Timed out forcefully shutting down connection: {}", e.getMessage());
        }
    }
}
