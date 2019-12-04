package com.dyw.register;

import com.dyw.register.controller.EgciController;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.dyw.register.*"})
@MapperScan("com.dyw.register.dao")
public class RegisterApplication implements CommandLineRunner {
    @Autowired
    private EgciController egciController;

    private Logger logger = LoggerFactory.getLogger(RegisterApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RegisterApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        new Thread(() -> {
            try {
                egciController.initServer();
//                egciController.test();
            } catch (Exception e) {
                logger.error("错误：", e);
            } finally {
                logger.error("人脸通行服务程序出现严重错误,需要被关闭");
                System.exit(0);//出现任何错误，关闭程序
            }

        }).start();
    }
}
