package sunseries.travel.access.control.configuration;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultShutdownStrategy;
import org.apache.camel.spi.ShutdownStrategy;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import sunseries.travel.access.control.rcp.PermissionHandleMessage;
import sunseries.travel.java.rpc.v2.server.RpcServerV2;

@Configuration
public class AccessControlConfiguration {
    @Value("${redis.permission-queue.host}")
    private String host;

    @Value("${redis.permission-queue.port}")
    private int port;

    @Value("${redis.permission-queue.name}")
    private String queueName;

    @Bean
    public ShutdownStrategy shutdownStrategy() {
        return new DefaultShutdownStrategy();
    }

    @Bean
    CamelContextConfiguration contextConfiguration(ShutdownStrategy shutdownStrategy) {
        return new CamelContextConfiguration() {
            @Override
            public void beforeApplicationStart(CamelContext context) {
                context.setShutdownStrategy(shutdownStrategy);
            }

            @Override
            public void afterApplicationStart(CamelContext cc) {
            }
        };
    }

    @Bean
    public RpcServerV2 rpcServer(PermissionHandleMessage permissionHandleMessage) {
        return new RpcServerV2(host, port, queueName, 100, permissionHandleMessage);
    }
}
