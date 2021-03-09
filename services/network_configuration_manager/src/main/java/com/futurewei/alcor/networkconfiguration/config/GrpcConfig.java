package com.futurewei.alcor.networkconfiguration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

    public static final int SHUTDOWN_TIMEOUT = 5;

    @Value("${grpc.shutdownTimeout: 5}")
    public int shutdownTimeout;

    @Value("${grpc.keepAliveTime: 50}")
    public int keepAliveTime;

    @Value("${grpc.AGAClient.port}")
    public int grpcPort;

    @Value("${grpc.min-threads: 100}")
    public int grpcMinThreads;

    @Value("${grpc.max-threads: 200}")
    public int grpcMaxThreads;

    @Value("${grpc.threads-pool-name: grpc-thread-pool}")
    public String grpThreadsName;

}
