package com.java110.boot;


import com.java110.core.annotation.Java110CmdDiscovery;
import com.java110.core.client.OutRestTemplate;
import com.java110.core.client.RestTemplate;
import com.java110.core.cmd.ServiceCmdEventPublishing;
import com.java110.core.context.Environment;
import com.java110.core.factory.ApplicationContextFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.init.ServiceStartInit;
import com.java110.core.trace.Java110FeignClientInterceptor;
import com.java110.core.trace.Java110RestTemplateInterceptor;
import com.java110.core.utils.StringUtil;
import com.java110.doc.annotation.Java110ApiDocDiscovery;
import com.java110.doc.annotation.Java110CmdDocDiscovery;
import com.java110.doc.registrar.ApiDocCmdPublishing;
import com.java110.doc.registrar.ApiDocPublishing;
import com.java110.hal.start.StartHeartbeat;
import com.java110.intf.dev.ICacheV1InnerServiceSMO;
import okhttp3.ConnectionPool;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

@SpringBootApplication(scanBasePackages = {
        "com.java110.core",
        "com.java110.accessControl",
        "com.java110.openapi",
        "com.java110.barrier",
        "com.java110.car",
        "com.java110.community",
        "com.java110.dev",
        "com.java110.gateway",
        "com.java110.job",
        "com.java110.monitor",
        "com.java110.system",
        "com.java110.user",
        "com.java110.charge",
        "com.java110.hal",
        "com.java110.acct",
        "com.java110.meter",
        "com.java110.lift",
        "com.java110.lamp",
        "com.java110.lock",
        "com.java110.doc",
        "com.java110.boot",
},
        exclude = {LiquibaseAutoConfiguration.class,
                //           org.activiti.spring.boot.SecurityAutoConfiguration.class,
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class}

)
@Java110CmdDiscovery(cmdPublishClass = ServiceCmdEventPublishing.class,
        basePackages = {
                "com.java110.accessControl.cmd",
                "com.java110.openapi.cmd",
                "com.java110.barrier.cmd",
                "com.java110.car.cmd",
                "com.java110.community.cmd",
                "com.java110.dev.cmd",
                "com.java110.gateway.cmd",
                "com.java110.job.cmd",
                "com.java110.monitor.cmd",
                "com.java110.system.cmd",
                "com.java110.charge.cmd",
                "com.java110.acct.cmd",
                "com.java110.meter.cmd",
                "com.java110.lift.cmd",
                "com.java110.lock.cmd",
                "com.java110.lamp.cmd",
                "com.java110.user.cmd"
        })
@EnableScheduling
@EnableAsync
@Java110ApiDocDiscovery(basePackages = {"com.java110.boot.rest"}, apiDocClass = ApiDocPublishing.class)
@Java110CmdDocDiscovery(basePackages = {
        "com.java110.accessControl.cmd",
        "com.java110.openapi.cmd",
        "com.java110.barrier.cmd",
        "com.java110.car.cmd",
        "com.java110.community.cmd",
        "com.java110.dev.cmd",
        "com.java110.gateway.cmd",
        "com.java110.job.cmd",
        "com.java110.monitor.cmd",
        "com.java110.system.cmd",
        "com.java110.charge.cmd",
        "com.java110.acct.cmd",
        "com.java110.meter.cmd",
        "com.java110.lift.cmd",
        "com.java110.lock.cmd",
        "com.java110.lamp.cmd",
        "com.java110.user.cmd"
},
        cmdDocClass = ApiDocCmdPublishing.class)
public class BootApplicationStart {
    private static Logger logger = LoggerFactory.getLogger(BootApplicationStart.class);

    @Resource
    private Java110RestTemplateInterceptor java110RestTemplateInterceptor;

    /**
     * 实例化RestTemplate
     *
     * @return restTemplate
     */
    @Bean
    public OutRestTemplate outRestTemplate() {
        StringHttpMessageConverter m = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        OutRestTemplate restTemplate = new RestTemplateBuilder().additionalMessageConverters(m).build(OutRestTemplate.class);
        restTemplate.getInterceptors().add(java110RestTemplateInterceptor);

        //设置超时时间
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(10000);
        httpRequestFactory.setConnectTimeout(10000);
        httpRequestFactory.setReadTimeout(30000);
        restTemplate.setRequestFactory(httpRequestFactory);
        return restTemplate;
    }

    /**
     * 实例化RestTemplate，通过@LoadBalanced注解开启均衡负载能力.
     *
     * @return restTemplate
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        StringHttpMessageConverter m = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        RestTemplate restTemplate = new RestTemplateBuilder().additionalMessageConverters(m).build(RestTemplate.class);
        restTemplate.getInterceptors().add(java110RestTemplateInterceptor);
        //设置超时时间
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(10000);
        httpRequestFactory.setConnectTimeout(10000);
        httpRequestFactory.setReadTimeout(30000);
        restTemplate.setRequestFactory(httpRequestFactory);
        return restTemplate;
    }

    @Bean
    @ConditionalOnBean(Java110FeignClientInterceptor.class)
    public okhttp3.OkHttpClient okHttpClient(@Autowired
                                             Java110FeignClientInterceptor okHttpLoggingInterceptor) {
        okhttp3.OkHttpClient.Builder ClientBuilder = new okhttp3.OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS) //读取超时
                .connectTimeout(10, TimeUnit.SECONDS) //连接超时
                .writeTimeout(60, TimeUnit.SECONDS) //写入超时
                .connectionPool(new ConnectionPool(10 /*maxIdleConnections*/, 3, TimeUnit.MINUTES))
                .addInterceptor(okHttpLoggingInterceptor);
        return ClientBuilder.build();
    }

    /**
     * spring boot 版的启动方法
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        try {
            ServiceStartInit.preInitSystemConfig();
            ApplicationContext context = SpringApplication.run(BootApplicationStart.class, args);
            //todo 服务启动加载
            ServiceStartInit.initSystemConfig(context);

            Environment.setSystemStartWay(Environment.SPRING_BOOT);


            //todo 刷新缓存
            flushMainCache(args);

            //todo 启动线程
            StartHeartbeat startHeartbeat = new StartHeartbeat();
            startHeartbeat.start();
        } catch (Throwable e) {
            logger.error("系统启动失败", e);
        }
    }

    /**
     * 刷新主要的缓存
     *
     * @param args
     */
    private static void flushMainCache(String[] args) {

        logger.debug("判断是否需要刷新日志，参数 args 为 {}", args);

        //因为好多朋友启动时 不加 参数-Dcache 所以启动时检测 redis 中是否存在 java110_hc_version
        //String mapping = MappingCache.getValue(MappingConstant.ENV_DOMAIN,"java110_hc_version");
        //这里改成强制刷新 因为好多 小伙伴 教不会 如何清理redis 干脆后面就说重启dev 就会刷新缓存 二开的兄弟们根据自己的情况 是否强制
        String mapping = "";
        if (StringUtil.isEmpty(mapping)) {
            ICacheV1InnerServiceSMO devServiceCacheSMOImpl = (ICacheV1InnerServiceSMO) ApplicationContextFactory.getBean("cacheV1InnerServiceSMOImpl");
            devServiceCacheSMOImpl.startFlush();
            return;
        }

        if (args == null || args.length == 0) {
            return;
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-Dcache")) {
                logger.debug("开始刷新日志，入参为：{}", args[i]);
                ICacheV1InnerServiceSMO devServiceCacheSMOImpl = (ICacheV1InnerServiceSMO) ApplicationContextFactory.getBean("cacheV1InnerServiceSMOImpl");
                devServiceCacheSMOImpl.startFlush();
            }
        }
    }

}
