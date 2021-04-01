package com.futurewei.alcor.netwconfigmanager.server.grpc;

import com.futurewei.alcor.common.db.CacheException;
import com.futurewei.alcor.common.logging.Logger;
import com.futurewei.alcor.common.logging.LoggerFactory;
import com.futurewei.alcor.netwconfigmanager.client.GoalStateClient;
import com.futurewei.alcor.netwconfigmanager.client.gRPC.GoalStateClientImpl;
import com.futurewei.alcor.netwconfigmanager.config.Config;
import com.futurewei.alcor.netwconfigmanager.entity.HostGoalState;
import com.futurewei.alcor.netwconfigmanager.repo.NetworkConfigRepo;
import com.futurewei.alcor.netwconfigmanager.server.NetworkConfigServer;
import com.futurewei.alcor.netwconfigmanager.util.NetworkConfigManagerUtil;
import com.futurewei.alcor.schema.*;
import io.grpc.stub.StreamObserver;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Service
public class GoalStateProvisionerServer implements NetworkConfigServer {

    private static final Logger logger = LoggerFactory.getLogger();

    private final int port;
    private final Server server;

    @Autowired
    private NetworkConfigRepo networkConfigRepo;

    public GoalStateProvisionerServer() {
        this.port = 50010;
        this.server = ServerBuilder.forPort(this.port)
                .addService(new GoalStateProvisionerImpl())
                .build();
    }

    @Override
    public void start() throws IOException {
        this.server.start();
        logger.log(Level.INFO, "GoalStateProvisionerServer : Server started, listening on " + this.port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                logger.log(Level.INFO, "*** shutting down gRPC server since JVM is shutting down");
                try {
                    GoalStateProvisionerServer.this.stop();
                } catch (InterruptedException e) {
                    logger.log(Level.WARNING, "*** gRPC server shut down error");
                }
                logger.log(Level.INFO, "*** server shut down");
            }
        });
    }

    @Override
    public void stop() throws InterruptedException {
        logger.log(Level.INFO,"GoalStateProvisionerServer : Server stop, was listening on " + this.port);
        if (this.server != null) {
            this.server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    @Override
    public void blockUntilShutdown() throws InterruptedException {
        logger.log(Level.INFO,"GoalStateProvisionerServer : Server blockUntilShutdown, listening on " + this.port);
        if (this.server != null) {
            this.server.awaitTermination();
        }
    }

    private class GoalStateProvisionerImpl extends GoalStateProvisionerGrpc.GoalStateProvisionerImplBase {

        GoalStateProvisionerImpl() { }

        @Override
        public StreamObserver<Goalstate.GoalStateV2> pushGoalStatesStream(final StreamObserver<Goalstateprovisioner.GoalStateOperationReply> responseObserver) {

            return new StreamObserver<Goalstate.GoalStateV2>() {
                @SneakyThrows
                @Override
                public void onNext(Goalstate.GoalStateV2 value) {

                    logger.log(Level.INFO, "pushGoalStatesStream : receiving GS V2 message " + value.getHostResourcesCount());

                    //prepare GS message based on host
                    Map<String, HostGoalState> hostGoalStates = NetworkConfigManagerUtil.splitClusterToHostGoalState(value);

                    //store the goal state in cache
                    saveGoalStateInDB(hostGoalStates);

                    //send them down to target ACA
                    try {
                        GoalStateClient grpcGoalStateClient = new GoalStateClientImpl();
                        Map<String, List<Goalstateprovisioner.GoalStateOperationReply.GoalStateOperationStatus>> goalStateReplyMap
                                = grpcGoalStateClient.sendGoalStates(hostGoalStates);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //consolidate response from ACA and send response to DPM
                    Goalstateprovisioner.GoalStateOperationReply reply =
                            Goalstateprovisioner.GoalStateOperationReply.newBuilder()
                                    .setFormatVersion(100)
                                    .build();
                    responseObserver.onNext(reply);
                }

                @Override
                public void onError(Throwable t) {
                    t.printStackTrace();
                    logger.log(Level.WARNING, "*** pushGoalStatesStream cancelled");
                    responseObserver.onCompleted();
                }

                @Override
                public void onCompleted() {
                    logger.log(Level.INFO, "pushGoalStatesStream : onCompleted() ");
                    responseObserver.onCompleted();
                }
            };
        }
    }

    private void saveGoalStateInDB(Map<String, HostGoalState> hostGoalStates) throws CacheException {
        for (Map.Entry<String, HostGoalState> goalStateEntry : hostGoalStates.entrySet()) {
            String hostId = goalStateEntry.getKey();
            HostGoalState goalState = goalStateEntry.getValue();
            List<Goalstate.ResourceIdType> hostResources = goalState.getGoalState().getHostResourcesMap()
                    .get(hostId).getResourcesList();
            ArrayList<String> resourceIdList = hostResources.stream().map(Goalstate.ResourceIdType::getId)
                    .collect(Collectors.toCollection(ArrayList::new));
            networkConfigRepo.addListResourceIdItem(hostId,resourceIdList);

            for (Goalstate.ResourceIdType hostResource : hostResources) {
                String resourceId = hostResource.getId();
                Common.ResourceType type = hostResource.getType();
                switch (type) {
                    case VPC:
                        networkConfigRepo.addResourceStateItem(resourceId, goalState.getGoalState().getVpcStatesMap().get(resourceId));
                        break;
                    case SUBNET:
                        networkConfigRepo.addResourceStateItem(resourceId, goalState.getGoalState().getSubnetStatesMap().get(resourceId));
                        break;
                    case PORT:
                        networkConfigRepo.addResourceStateItem(resourceId, goalState.getGoalState().getPortStatesMap().get(resourceId));
                        break;
                    case NEIGHBOR:
                        networkConfigRepo.addResourceStateItem(resourceId, goalState.getGoalState().getNeighborStatesMap().get(resourceId));
                        break;
                    case SECURITYGROUP:
                        networkConfigRepo.addResourceStateItem(resourceId, goalState.getGoalState().getSecurityGroupStatesMap().get(resourceId));
                        break;
                    case DHCP:
                        networkConfigRepo.addResourceStateItem(resourceId, goalState.getGoalState().getDhcpStatesMap().get(resourceId));
                        break;
                    case ROUTER:
                        networkConfigRepo.addResourceStateItem(resourceId, goalState.getGoalState().getRouterStatesMap().get(resourceId));
                        break;
                    case GATEWAY:
                        networkConfigRepo.addResourceStateItem(resourceId, goalState.getGoalState().getGatewayStatesMap().get(resourceId));
                        break;
                    case UNRECOGNIZED:
                        break;
                }
            }
        }
    }
}
