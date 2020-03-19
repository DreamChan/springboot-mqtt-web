package cn.dreamchan;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Slf4j
@EnableSwagger2
@SpringBootApplication
public class SpringbootMqttWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootMqttWebApplication.class, args);
    }

}

