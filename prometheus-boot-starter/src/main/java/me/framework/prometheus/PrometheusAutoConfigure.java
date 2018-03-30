package me.framework.prometheus;


import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.spring.boot.SpringBootMetricsCollector;
import me.framework.prometheus.jvm.HotspotJvmExporter;
import me.framework.prometheus.mvc.MvcRequestConfigurer;
import me.framework.prometheus.mybatis.SqlExecuteIntercepter;
import me.framework.prometheus.property.PrometheusProperties;
import org.apache.ibatis.executor.Executor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.DataSourcePublicMetrics;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.endpoint.SystemPublicMetrics;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ryan on 2018/3/21.
 */
@ConditionalOnProperty(value = "prometheus.monitor.enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(PrometheusProperties.class)
@Import({PrometheusAutoConfigure.MybatisConfig.class, PrometheusAutoConfigure.MvcConfig.class})
public class PrometheusAutoConfigure {

    @Autowired
    private PrometheusProperties prometheusProperties;

    @Bean(destroyMethod="stop")
    @ConditionalOnProperty(value = "prometheus.metrics.enabled", havingValue = "true", matchIfMissing = true)
    public HTTPServer prometheusRunner() throws IOException {
        return new HTTPServer(prometheusProperties.getPort());
    }

    @Bean
    @ConditionalOnProperty(value = "prometheus.metrics.enabled", havingValue = "true", matchIfMissing = true)
    public SpringBootMetricsCollector springBootMetricsCollector(Collection<PublicMetrics> publicMetrics) {
        List<PublicMetrics> metricss = publicMetrics.stream()
                .filter(p -> p instanceof DataSourcePublicMetrics || p instanceof SystemPublicMetrics)
                .collect(Collectors.toList());
        SpringBootMetricsCollector springBootMetricsCollector = new SpringBootMetricsCollector(metricss);
        springBootMetricsCollector.register();
        return springBootMetricsCollector;
    }

    @Bean
    @ConditionalOnProperty(value = "prometheus.monitor.jvm.enabled", havingValue = "true", matchIfMissing = true)
    HotspotJvmExporter jvmExporter(){
        HotspotJvmExporter Exporter =  new HotspotJvmExporter();
        Exporter.init();
        return Exporter;
    }

    @ConditionalOnClass(value = { MvcRequestConfigurer.class })
    class MvcConfig {
        @Bean
        @ConditionalOnProperty(value = "prometheus.monitor.mvc.enabled", havingValue = "true", matchIfMissing = true)
        MvcRequestConfigurer mvcRequestConfigurer(){
            return new MvcRequestConfigurer();
        }
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
