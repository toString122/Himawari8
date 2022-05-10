package com.liwj.client;

import com.liwj.client.utils.ImgUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }


    @Scheduled(cron = "0 0/20 * * * ? ")
    public void scheduled() throws Exception {
        ImgUtils.setWallpaper();
    }

}
