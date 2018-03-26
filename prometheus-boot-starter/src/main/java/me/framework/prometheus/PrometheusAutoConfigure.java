package me.framework.prometheus;


import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.spring.boot.EnablePrometheusEndpoint;
import me.framework.prometheus.jvm.HotspotJvmExporter;
import me.framework.prometheus.mvc.MvcRequestConfigurer;
import me.framework.prometheus.mvc.MvcRequestInterceptor;
//import me.framework.prometheus.mybatis.SqlExecuteIntercepter;
import io.prometheus.client.spring.boot.EnableSpringBootMetricsCollector;
import me.framework.prometheus.mybatis.SqlExecuteIntercepter;
import me.framework.prometheus.property.PrometheusProperties;
import org.apache.ibatis.executor.Executor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.io.IOException;

/**
 * Created by ryan on 2018/3/21.
 */
@ConditionalOnProperty(value = "prometheus.monitor.enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(PrometheusProperties.class)
@Import({PrometheusAutoConfigure.MybatisConfig.class})
public class PrometheusAutoConfigure {

    @Autowired
    private PrometheusProperties prometheusProperties;

    @Bean(destroyMethod="stop")
    @ConditionalOnProperty(value = "prometheus.metrics.enabled", havingValue = "true", matchIfMissing = true)
    public HTTPServer prometheusRunner() throws IOException {
        return new HTTPServer(prometheusProperties.getPort());
    }

    @Bean
    @ConditionalOnProperty(value = "prometheus.monitor.jvm.enabled", havingValue = "true", matchIfMissing = true)
    HotspotJvmExporter jvmExporter(){
        HotspotJvmExporter Exporter =  new HotspotJvmExporter();
        Exporter.init();
        return Exporter;
    }

    @Bean
    @ConditionalOnClass(HandlerInterceptorAdapter.class)
    @ConditionalOnProperty(value = "prometheus.monitor.mvc.enabled", havingValue = "true", matchIfMissing = true)
    MvcRequestConfigurer mvcRequestConfigurer(){
        return new MvcRequestConfigurer();
    }

    @ConditionalOnClass(Executor.class)
    class MybatisConfig {
        @Bean
        @ConditionalOnProperty(value = "prometheus.monitor.mybatis.enabled", havingValue = "true", matchIfMissing = true)
        public SqlExecuteIntercepter sqlIntercepter() {
            return new SqlExecuteIntercepter();
        }

    }

}
