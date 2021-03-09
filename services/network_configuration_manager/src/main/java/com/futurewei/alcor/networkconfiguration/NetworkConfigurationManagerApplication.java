package com.futurewei.alcor.networkconfiguration;

import com.futurewei.alcor.common.db.DbBaseConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({DbBaseConfiguration.class})
public class NetworkConfigurationManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(NetworkConfigurationManagerApplication.class, args);
    }
}
