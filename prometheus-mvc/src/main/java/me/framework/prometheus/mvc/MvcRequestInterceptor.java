package me.framework.prometheus.mvc;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import me.framework.prometheus.constants.Constants;
import me.framework.prometheus.mvc.annotation.EnableMvcMetrics;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * Created by ryan on 2018/3/21.
 */
public class MvcRequestInterceptor extends HandlerInterceptorAdapter {

    private static final Counter reqCounter = Counter.build()
            .namespace(Constants.NAMESPACE_MVC)
            .subsystem(Constants.SUBSYSTEM_SERVER)
            .name("http_requests_total")
            .labelNames(Constants.METHOD_INVOKE_LABLE_MVC_HANDLER, Constants.METHOD_INVOKE_LABLE_MVC_ERROR)
            .help("Total number of mvc request").register();

    private static final Histogram responseTimeInSecond = Histogram.build()
            .namespace(Constants.NAMESPACE_MVC)
            .subsystem(Constants.SUBSYSTEM_SERVER)
            .name("http_response_time_second")
            .labelNames(Constants.METHOD_INVOKE_LABLE_MVC_HANDLER, Constants.METHOD_INVOKE_LABLE_MVC_ERROR)
            .help("Request completed time in milliseconds")
            .buckets(new double[] {.001, .005, .01, .05, 0.075, .1, .25, .5, 1, 2, 5, 10})
            .register();

    private static final long MILLIS_PER_SECOND = 1000L;
    private static final String RESPONSE_TIME_KEY = "RESPONSE_TIME";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (canTrace(handler)) {
            request.setAttribute(RESPONSE_TIME_KEY, System.currentTimeMillis());
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (canTrace(handler)) {
            traceReqCount(request, response, handler, ex);
            traceRTInSecond(request, response, handler, ex);
        }
    }

    private boolean canTrace(Object handler) {
        boolean canTrace = false;
        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            if (method.getDeclaringClass().getAnnotation(EnableMvcMetrics.class) != null
                    || method.getAnnotation(EnableMvcMetrics.class) != null) {
                canTrace = true;
            }

        }
        return canTrace;
    }

    private void traceRTInSecond(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Object rtInMSAttr = request.getAttribute(RESPONSE_TIME_KEY);
        if (rtInMSAttr != null) {
            double completedTime = (System.currentTimeMillis() - (Long) rtInMSAttr) / (double) MILLIS_PER_SECOND;
            String handlerLabel = getHandlerLabel(handler);
            responseTimeInSecond.labels(handlerLabel, Boolean.toString(ex != null)).observe(completedTime);
        }
    }

    private void traceReqCount(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String handlerLabel = getHandlerLabel(handler);
        reqCounter.labels(handlerLabel, Boolean.toString(ex != null)).inc();
    }

    private String getHandlerLabel(Object handler) {
        String handlerLabel = handler.toString();
        if (handler instanceof HandlerMethod) {
            Method method = ((HandlerMethod) handler).getMethod();
            handlerLabel = method.getDeclaringClass().getSimpleName() + "." + method.getName();
        }
        return handlerLabel;
    }

}
