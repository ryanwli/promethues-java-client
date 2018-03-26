package me.framework.prometheus.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by ryan on 2018/3/21.
 */
@ConfigurationProperties(prefix = "prometheus.monitor")
public class PrometheusProperties {
    private int port = 9527;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
