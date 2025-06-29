package com.xuecheng.system;

//import com.spring4all.swagger.EnableSwagger2Doc;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <p>
 *     系统管理启动类
 * </p>
 *
 * @Description:
 */
@EnableScheduling
@OpenAPIDefinition(
        info = @Info(
                title = "学成在线内容管理系统",
                version = "1.0.0",
                description = "对课程相关信息进行管理"
        )
)
@SpringBootApplication
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class,args);
    }
}