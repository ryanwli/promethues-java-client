package me.framework.prometheus.jvm;

import io.prometheus.client.hotspot.DefaultExports;

/**
 * Created by ryan on 2018/3/21.
 */
public class HotspotJvmExporter {

    public void init(){
        DefaultExports.initialize();
    }
}
