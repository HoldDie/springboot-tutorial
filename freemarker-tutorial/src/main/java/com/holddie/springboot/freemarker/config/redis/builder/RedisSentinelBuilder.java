package com.holddie.springboot.freemarker.config.redis.builder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.holddie.springboot.freemarker.config.redis.*;
import com.sohu.tv.cachecloud.client.basic.heartbeat.ClientStatusEnum;
import com.sohu.tv.cachecloud.client.basic.util.HttpUtils;
import jodd.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * 哨兵构建器
 * v2.0: 新增手动配置方式,by kingapex 2017-12-26
 * @author fk,kingapex
 * @version 2.0
 * @since v6.4
 * 2017年10月27日 下午2:25:52
 */
@Service
public class RedisSentinelBuilder  implements IRedisBuilder{


	private static Logger logger = LoggerFactory.getLogger(RedisSentinelBuilder.class);


    @Autowired
    private RedisConnectionConfig config;

	
	@Override
	public JedisConnectionFactory buildConnectionFactory( ) {

        RedisSentinelConfiguration sentinelConfiguration = this.buildSentinelConfig();
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(sentinelConfiguration, JedisSetting.getPoolConfig());
		return jedisConnectionFactory;
	}


    /**
     * 构建redis cluster的配置
     * @return redis cluster的配置
     */
    public RedisSentinelConfiguration buildSentinelConfig(){


        //cache cloud rest api配置方式
        if(RedisConfigType.rest.name().equals(config.getConfigType() )){
            RedisSentinelConfiguration configuration = createRestSentinelConfig();
            return  configuration;
        }

        //手动配置方式
        if(RedisConfigType.manual.name().equals(config.getConfigType() )){
            RedisSentinelConfiguration  configuration = createManualSentinelConfig();
            return  configuration;
        }

        throw  new RuntimeException("redis 配置错误：错误的redis.config.type，只允许com.enation.eop.sdk.config.redis.configure.RedisConfigType中定义的值");


    }




    /**
     * 构建手动方式的 redis 哨兵 配置
     * @return
     */
    private RedisSentinelConfiguration createManualSentinelConfig(){

        String masterName  = config.getSentinelMaster();

        if(StringUtil.isEmpty(masterName)){
            throw  new RuntimeException("redis 配置错误： sentinel.master不能为空");
        }


        String nodes = config.getSentinelNodes();

        if(StringUtil.isEmpty(nodes)){
            throw  new RuntimeException("redis 配置错误： sentinel.nodes不能为空");
        }


        Set<String> sentinelSet = new HashSet<String>();
        for (String sentinelStr : nodes.split(",")) {
            String[] sentinelArr = sentinelStr.split(":");
            if (sentinelArr.length == 2) {
                sentinelSet.add(sentinelStr);
            }
        }
        RedisSentinelConfiguration configuration = new RedisSentinelConfiguration( masterName,sentinelSet);
        if (config.getPassword() != null) {
            configuration.setPassword(RedisPassword.of(config.getPassword()));
        }

        return configuration;
    }





    /**
     * 构建rest方式的 哨兵 配置
     * @return
     */
    private RedisSentinelConfiguration createRestSentinelConfig(){

        String redis_cluster_suffix= "/cache/client/redis/sentinel/%s.json?clientVersion=";
        String REDIS_CLUSTER_URL = config.getRestUrl() + redis_cluster_suffix+ config.getRestClientVersion();

        String url = String.format(REDIS_CLUSTER_URL, String.valueOf(config.getRestAppid()));
        String response = HttpUtils.doGet(url);

        String  appId = config.getRestAppid();
        /**
         * 心跳返回的请求为空；
         */

        if (response == null || response.isEmpty()) {
            logger.warn("cannot get response from server, appId={}. continue...", appId);
        }



        /**
         * http请求返回的结果是无效的；
         */
        ObjectMapper mapper = new ObjectMapper();
        JsonNode heartbeatInfo = null;
        try {
            heartbeatInfo = mapper.readTree(response);
        } catch (Exception e) {
//            logger.error("heartbeat error, appId: {}. continue...", appId, e);
        }
        if (heartbeatInfo == null) {
            logger.error("get sentinel info for appId: {} error. continue...", appId);
        }

        /** 检查客户端版本 **/
        if (heartbeatInfo.get("status").intValue() == ClientStatusEnum.ERROR.getStatus()) {
            throw new IllegalStateException(heartbeatInfo.get("message").textValue());
        } else if (heartbeatInfo.get("status").intValue() == ClientStatusEnum.WARN.getStatus()) {
            logger.warn(heartbeatInfo.get("message").textValue());
        } else {
            logger.info(heartbeatInfo.get("message").textValue());
        }

        /**
         * 有效的请求：取出masterName和sentinels，并创建JedisSentinelPool的实例；
         */
        String masterName = heartbeatInfo.get("masterName").asText();
        String sentinels = heartbeatInfo.get("sentinels").asText();
        Set<String> sentinelSet = new HashSet<String>();
        for (String sentinelStr : sentinels.split(" ")) {
            String[] sentinelArr = sentinelStr.split(":");
            if (sentinelArr.length == 2) {
                sentinelSet.add(sentinelStr);
            }
        }
        RedisSentinelConfiguration configuration  = new RedisSentinelConfiguration(masterName, sentinelSet);

        return  configuration;
    }




    @Override
	public RedisType getType() {
		
		return RedisType.sentinel;
	}

}
