package com.holddie.springboot.freemarker.config.redis.builder;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.holddie.springboot.freemarker.config.redis.IRedisBuilder;
import com.holddie.springboot.freemarker.config.redis.RedisConfigType;
import com.holddie.springboot.freemarker.config.redis.RedisConnectionConfig;
import com.holddie.springboot.freemarker.config.redis.RedisType;
import com.sohu.tv.cachecloud.client.basic.util.HttpUtils;
import jodd.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Service;

/**
 * 独立模式构建器
 * v2.0: 新增手动配置方式,by kingapex 2017-12-26
 * @author fk, kingapex
 * @version v2.0
 * @since v6.4
 * 2017年10月27日 下午2:37:04
 */
@Service
public class RedisStandaloneBuilder implements IRedisBuilder {


    private static Logger logger = LoggerFactory.getLogger(RedisStandaloneBuilder.class);

    @Autowired
    private RedisConnectionConfig config;


    @Override
    public JedisConnectionFactory buildConnectionFactory() {
        RedisStandaloneConfiguration configuration = buildStandaloneConfig();
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(configuration);

        return jedisConnectionFactory;
    }


    /**
     * 构建redis cluster的配置
     * @return redis cluster的配置
     */
    public RedisStandaloneConfiguration buildStandaloneConfig() {


        //cache cloud rest api配置方式
        if (RedisConfigType.rest.name().equals(config.getConfigType())) {
            RedisStandaloneConfiguration configuration = createRestStandaloneConfig();
            return configuration;
        }

        //手动配置方式
        if (RedisConfigType.manual.name().equals(config.getConfigType())) {
            RedisStandaloneConfiguration configuration = createManualStandaloneConfig();
            return configuration;
        }

        throw new RuntimeException("redis 配置错误：错误的redis.config.type，只允许com.enation.eop.sdk.config.redis.configure.RedisConfigType中定义的值");


    }


    /**
     * 构建手动方式的 redis Standalone 配置
     * @return
     */
    private RedisStandaloneConfiguration createManualStandaloneConfig() {

        String host = config.getHost();
        if (StringUtil.isEmpty(host)) {
            throw new RuntimeException("redis 配置错误：redis.host为空");
        }


        int port = config.getPort();
        String password = config.getPassword();

        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);

        if (StringUtils.isNotEmpty(password)) {
            configuration.setPassword(RedisPassword.of(password));
        }
        return configuration;
    }


    /**
     * 构建rest方式的 Standalone 配置
     * @return
     */
    private RedisStandaloneConfiguration createRestStandaloneConfig() {

        String redis_cluster_suffix = "/cache/client/redis/standalone/%s.json?clientVersion=";
        String REDIS_CLUSTER_URL = config.getRestUrl() + redis_cluster_suffix + config.getRestClientVersion();

        String url = String.format(REDIS_CLUSTER_URL, String.valueOf(config.getRestAppid()));
        String response = HttpUtils.doGet(url);

        String appId = config.getRestAppid();
        /**
         * 心跳返回的请求为空；
         */

        if (response == null || response.isEmpty()) {
            logger.warn("cannot get response from server, appId={}. continue...", appId);
        }

        /**
         * 心跳返回的请求无效；
         */
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJson = null;
        try {
            responseJson = mapper.readTree(response);
        } catch (Exception e) {
            logger.error("read json from response error, appId: {}.", appId, e);
        }

        if (responseJson == null) {
            logger.warn("invalid response, appId: {}. continue...", appId);
        }

        /**
         * 从心跳中提取HostAndPort，构造JedisPool实例；
         */
        String instance = responseJson.get("standalone").asText();
        String[] instanceArr = instance.split(":");
        if (instanceArr.length != 2) {
            logger.warn("instance info is invalid, instance: {}, appId: {}, continue...", instance, appId);
        }

        String host = instanceArr[0];
        int port = Integer.valueOf(instanceArr[1]);
        RedisStandaloneConfiguration clusterConfiguration = new RedisStandaloneConfiguration(host, port);

        return clusterConfiguration;
    }


    @Override
    public RedisType getType() {

        return RedisType.standalone;
    }

}
