package me.framework.test;

import me.framework.prometheus.mvc.annotation.EnableMvcMetrics;
import me.framework.test.mappers.DemoMapper;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by ryan on 2018/3/21.
 */
@EnableMvcMetrics
@RestController
public class TestController {

    @Autowired
    DemoMapper demoMapper;

    @RequestMapping("/endpointA")
    public void handlerA() throws Exception {
        Thread.sleep(RandomUtils.nextLong(0, 500));
    }

    @EnableMvcMetrics
    @RequestMapping("/endpointB")
    public List<DemoModel> handlerB() throws InterruptedException {
        return demoMapper.getDiscount(714);
    }

}
