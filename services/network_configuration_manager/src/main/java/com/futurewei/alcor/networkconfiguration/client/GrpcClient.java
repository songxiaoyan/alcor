package com.futurewei.alcor.networkconfiguration.client;

import com.futurewei.alcor.networkconfiguration.config.GrpcConfig;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class GrpcClient {

    private int grepPort;
    private int shutdownTimeout;

    private final ExecutorService executor;

    @Autowired
    public GrpcClient(GrpcConfig config) {
        this.grepPort = config.grpcPort;
        this.shutdownTimeout = config.shutdownTimeout;
        this.executor = new ThreadPoolExecutor(config.grpcMinThreads, config.grpcMaxThreads,
                config.keepAliveTime, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(), new DefaultThreadFactory(config.grpThreadsName));
    }


    public void pushGoalStatesStream() {

    }

    private ManagedChannel newChannel(String hostIp) {
        return ManagedChannelBuilder.forAddress(hostIp, grepPort).usePlaintext().build();
    }

    private void shutdown(ManagedChannel channel) {
        try {
            channel.shutdown().awaitTermination(shutdownTimeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn("Timed out forcefully shutting down connection: {}", e.getMessage());
        }
    }

}
