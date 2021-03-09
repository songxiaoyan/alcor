package com.futurewei.alcor.networkconfiguration.server;

import com.futurewei.alcor.networkconfiguration.service.GoalStateProvisionerGrpcService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GrpcServer implements InitializingBean {

    @Value("${grpc.server.port}")
    private int serverPort;

    private Server server;

    private void start() throws Exception {
        try {
            server = ServerBuilder.forPort(serverPort).addService(new GoalStateProvisionerGrpcService()).build().start();
        } catch (Exception e) {
            log.info("NCM grpc server start failed");
            throw e;
        }
        log.info("NCM grpc server has been successfully started");

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    private void stop() {
        if (null != server) {
            server.shutdown();
        }
    }

    private void awaitTermination() throws InterruptedException {
        if (null != server) {
            server.awaitTermination();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
        awaitTermination();
    }
}
