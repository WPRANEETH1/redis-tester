package com.missakai.redistester.config;

import io.lettuce.core.ReadFrom;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Objects;

/**
 *
 */
@Configuration
@Slf4j
public class RedisWriteToMasterReadFromReplicaConfiguration {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(LettuceClientConfiguration clientConfiguration, RedisProperties redisProperties) {

        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(
                Collections.singletonList(
                        redisProperties.getHost()
                                .concat(":")
                                .concat(String.valueOf(redisProperties.getPort()))));
        setCredentials(clusterConfiguration, redisProperties);

        return new LettuceConnectionFactory(clusterConfiguration, clientConfiguration);
    }

    @Bean
    public LettuceClientConfiguration redisClientConfiguration(RedisProperties redisProperties) {
        LettuceClientConfiguration.LettuceClientConfigurationBuilder clientConfigBuilder
                = LettuceClientConfiguration.builder().readFrom(ReadFrom.REPLICA_PREFERRED);

        if (redisProperties.isSsl())
            clientConfigBuilder.useSsl();

        ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .enablePeriodicRefresh(Duration.ofSeconds(300))
                .enableAdaptiveRefreshTrigger(
                        ClusterTopologyRefreshOptions.RefreshTrigger.MOVED_REDIRECT,
                        ClusterTopologyRefreshOptions.RefreshTrigger.UNKNOWN_NODE,
                        ClusterTopologyRefreshOptions.RefreshTrigger.UNCOVERED_SLOT)
                .adaptiveRefreshTriggersTimeout(Duration.ofSeconds(60)).build();

        ClusterClientOptions options = ClusterClientOptions.builder()
                .maxRedirects(5).topologyRefreshOptions(topologyRefreshOptions)
                .validateClusterNodeMembership(false).build();

        clientConfigBuilder.clientOptions(options);

        return clientConfigBuilder.build();
    }

    private void setCredentials(RedisConfiguration.WithAuthentication configuration, RedisProperties redisProperties) {
        if (!Objects.requireNonNullElse(redisProperties.getUsername(), "").isBlank()) {
            log.info("Setting Redis username");
            configuration.setUsername(redisProperties.getUsername());
        }

        if (!Objects.requireNonNullElse(redisProperties.getPassword(), "").isBlank()) {
            log.info("Setting Redis password");
            configuration.setPassword(redisProperties.getPassword());
        }
    }

}
