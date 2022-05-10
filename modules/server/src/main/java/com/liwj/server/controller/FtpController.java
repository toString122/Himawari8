package com.liwj.server.controller;

import com.liwj.server.service.FtpService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @description: ftp控制器
 * @author: liwj
 * @create: 2022-05-10 14:05
 **/
@RestController
public class FtpController {


    @Resource
    private FtpService ftpService;

    @RequestMapping("getImg")
    public String getImg() {
        String imgName = ftpService.downloadImg();
        if (imgName != null && !"".equals(imgName)) {
            System.out.println("imgName = " + imgName);
            return imgName;
        }
        return "";
    }
}
