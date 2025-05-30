package com.fhs.cache.service;

import com.fhs.common.utils.ConverterUtils;
import com.fhs.common.utils.JsonUtils;
import com.fhs.core.trans.constant.TransType;
import com.fhs.trans.service.impl.RpcTransService;
import com.fhs.trans.service.impl.SimpleTransService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 翻译缓存管理器
 * @author wanglei
 */
public class TransCacheManager {


    @Autowired(required = false)
    private RpcTransService rpcTransService;

    @Autowired(required = false)
    private SimpleTransService simpleTransService;

    @Autowired(required = false)
    private RedisCacheService<String> redisCacheService;

    /**
     * 清理缓存
     *
     * @param targetClass 目标
     * @param pkey        主键
     */
    public void clearCache(Class targetClass, Object pkey) {
        if (Objects.nonNull(simpleTransService)) {
            simpleTransService.clearGlobalCache(pkey, targetClass.getName(), TransType.SIMPLE);
        }
        if (Objects.nonNull(rpcTransService)) {
            rpcTransService.clearGlobalCache(pkey, targetClass.getName(), TransType.RPC);
        }
        if (Objects.nonNull(redisCacheService)) {
            Map<String, String> body = new HashMap<>();
            body.put("messageType", "clear");
            body.put("target", targetClass.getName());
            body.put("pkey", ConverterUtils.toString(pkey));
            body.put("transType", TransType.SIMPLE);
            redisCacheService.convertAndSend("trans", JsonUtils.map2json(body));
            body.put("transType", TransType.RPC);
            redisCacheService.convertAndSend("trans", JsonUtils.map2json(body));
        }
    }

    /**
     * 配置RPC 翻译的缓存
     *
     * @param targetClassName 目标类
     * @param cacheSett       缓存配置
     */
    public void setRpcTransCache(String targetClassName, SimpleTransService.TransCacheSett cacheSett) {
        rpcTransService.setTransCache(targetClassName, cacheSett);
    }

    /**
     * 配置唯一键缓存
     * @param targetClassName 目标类名
     * @param uniqueField 唯一键字段
     */
    public void setUniqueFieldCache(String targetClassName,String uniqueField){
        simpleTransService.setUniqueFieldCache(targetClassName,uniqueField);
    }

}
