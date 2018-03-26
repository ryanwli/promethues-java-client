package me.framework.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
@MapperScan(basePackages =
        {
                "me.framework.test.mappers"
        })
public class Application {
    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder(Application.class).web(true).run(args);
    }
}
