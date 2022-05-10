package com.liwj.client;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

@SpringBootTest
class ClientApplicationTests {

    @Test
    void contextLoads() {

    }


    @Test
    public void test() throws Exception {
        //因为是测试，图片url可以随便搞一个，我从百度随便复制了个图片url
        String pictureUrl = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fnimg.ws.126.net%2F%3Furl%3Dhttp%253A%252F%252Fdingyue.ws.126.net%252F2021%252F0812%252Fc8eb08e2j00qxqd8w0057c0012w00obg.jpg%26thumbnail%3D650x2147483647%26quality%3D80%26type%3Djpg&refer=http%3A%2F%2Fnimg.ws.126.net&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1651129353&t=f034e15dc07748726fa861080938ec36";
        //建立图片连接
        URL url = new URL(pictureUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //设置请求方式
        connection.setRequestMethod("GET");
        //设置超时时间
        connection.setConnectTimeout(10 * 10000);
        InputStream stream = connection.getInputStream();
        int len = 0;
        byte[] test = new byte[1024];
        //如果没有文件夹则创建
        File file = new File("C://tmp");
        if (!file.exists()) {
            file.mkdirs();
        }
        String fileName = new Date().getTime() + ".png";
        //输出流，图片输出的目的文件
        BufferedOutputStream fos = new BufferedOutputStream(Files.newOutputStream(Paths.get("C://tmp/" + fileName)));
        while ((len = stream.read(test)) != -1) {
            fos.write(test, 0, len);
        }
        stream.close();
        fos.close();
    }

}
