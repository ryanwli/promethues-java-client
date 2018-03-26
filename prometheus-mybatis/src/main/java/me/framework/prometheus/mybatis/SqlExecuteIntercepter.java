package me.framework.prometheus.mybatis;

import me.framework.prometheus.constants.Constants;
import io.prometheus.client.Summary;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

/**
 * Created by ryan on 2018/3/21.
 */
@Intercepts(value = {
        @Signature (type=Executor.class,
                method="update",
                args={MappedStatement.class,Object.class}),
        @Signature(type=Executor.class,
                method="query",
                args={MappedStatement.class,Object.class,RowBounds.class,ResultHandler.class,
                        CacheKey.class,BoundSql.class}),
        @Signature(type=Executor.class,
                method="query",
                args={MappedStatement.class,Object.class,RowBounds.class,ResultHandler.class})})
public class SqlExecuteIntercepter implements Interceptor{

    static  final Summary summary = Summary.build()
            .name(Constants.MYBATIS_EXECUTE_CONDITION).labelNames(Constants.METHOD_INVOKE_LABLE_METHOD)
            .help("sql execute condition")
            .register();


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        Object result = null;
        if (target instanceof Executor) {
            Object[] args= invocation.getArgs();
            MappedStatement mappedStatement = (MappedStatement) args[0];
            final Summary.Timer t = summary.labels(mappedStatement.getId()).startTimer();
            try {
                result = invocation.proceed();
            }finally {
                t.observeDuration();
            }
        }
        return result;
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
