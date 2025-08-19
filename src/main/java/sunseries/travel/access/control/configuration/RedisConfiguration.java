package sunseries.travel.access.control.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPoolConfig;
import sunseries.travel.constant.Origin;

@Slf4j
@Configuration
public class RedisConfiguration {
    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private Integer redisPort;

    @Value("${redis.pool.max.total}")
    private int poolMaxTotal;

    @Value("${redis.pool.max.idle}")
    private int poolMaxIdle;

    @Value("${redis.pool.min.idle}")
    private int poolMinIdle;

    @Value("${redis.cluster.enabled}")
    private boolean clusterEnabled;

    @Value("#{'${redis.cluster.nodes}'.split(',')}") 
    private List<String> clusterNodes;

    @Value("${redis.cluster.port}")
    private int clusterPort;

    @Value("${redis.type}")
    private String redisType;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        org.springframework.data.redis.connection.RedisConfiguration redisConfiguration = null;
        log.info("Redis Cluster Enabled : {}, Redis Type : {}", clusterEnabled, redisType);
        if (clusterEnabled) {
            RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
            for (String clusterNode : clusterNodes) {
                redisClusterConfiguration.addClusterNode(new RedisNode(clusterNode, clusterPort));
            }
            redisConfiguration = redisClusterConfiguration;
        } else {
            redisConfiguration = new RedisStandaloneConfiguration(redisHost, redisPort);
        }

        if ("lettuce".equalsIgnoreCase(redisType)) {
            LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                    .clientName(Origin.MS_ACCESS_CONTROL)
                    .build();
            return new LettuceConnectionFactory(redisConfiguration, clientConfig);
        } else {
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(poolMaxTotal);
            poolConfig.setMaxIdle(poolMaxIdle);
            poolConfig.setMinIdle(poolMinIdle);
            poolConfig.setMaxWaitMillis(100);
            poolConfig.setTestWhileIdle(false);
            poolConfig.setTestOnBorrow(false);
            poolConfig.setTestOnReturn(false);
            poolConfig.setMinEvictableIdleTimeMillis(10000);
            poolConfig.setTimeBetweenEvictionRunsMillis(5000);
            poolConfig.setNumTestsPerEvictionRun(10);
            JedisClientConfiguration clientConfig = JedisClientConfiguration.builder()
                    .clientName(Origin.MS_ACCESS_CONTROL)
                    .usePooling().poolConfig(poolConfig).build();
            if (clusterEnabled) {
                return new JedisConnectionFactory((RedisClusterConfiguration) redisConfiguration, clientConfig);
            } else {
                return new JedisConnectionFactory((RedisStandaloneConfiguration) redisConfiguration, clientConfig);
            }
        }
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        final RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setEnableTransactionSupport(true);
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericToStringSerializer<>(Object.class));
        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        return template;
    }
    
    @Bean
    public RedisConnectionFactory redisClusterConnectionFactory() {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        for (String clusterNode : clusterNodes) {
            redisClusterConfiguration.addClusterNode(new RedisNode(clusterNode, clusterPort));
        }
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder().clientName(Origin.MS_ACCESS_CONTROL).build();
        return new LettuceConnectionFactory(redisClusterConfiguration, clientConfig);
    }
    
    @Bean
    public RedisTemplate<String, String> redisClusterTemplate() {
        final RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setEnableTransactionSupport(true);
        template.setConnectionFactory(redisClusterConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericToStringSerializer<>(Object.class));
        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        return template;
    }
}
