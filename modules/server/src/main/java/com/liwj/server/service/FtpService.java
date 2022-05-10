package com.liwj.server.service;

import com.liwj.server.utils.FtpUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;

/**
 * @description:
 * @author: liwj
 * @create: 2022-05-10 14:15
 **/
@Service
public class FtpService {

    @Value("${img.path}")
    private String path;


    public String downloadImg() {
        String result = "";

        //清空图片文件夹
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            assert files != null;
            for (File file1 : files) {
                boolean delete = file1.delete();
            }
        }

        String imgPath = "";
        FTPClient ftpClient = FtpUtils.getFtpClient("ftp.ptree.jaxa.jp", "liwenji-e_outlook.com", "SP+wari8");
        String lastDic = FtpUtils.getLastDic("/jma/hsd", ftpClient);
        imgPath = "/jma/hsd/" + lastDic;
        String lastDic1 = FtpUtils.getLastDic(imgPath, ftpClient);
        imgPath = imgPath + "/" + lastDic1 + "/";
        String lastDic2 = FtpUtils.getLastDic(imgPath, ftpClient);
        imgPath = imgPath + lastDic2;
        System.out.println("imgPath = " + imgPath);
        assert ftpClient != null;
        String lastImg = FtpUtils.getLastImg(ftpClient, imgPath);
        System.out.println("lastImg = " + lastImg);
        boolean isDownLoad = FtpUtils.downLoadFTP(ftpClient, imgPath, lastImg, path);
        if (isDownLoad) {
            //转换格式到jpg
            String fileName = new Date().getTime() + ".jpg";
            String finalImg = path + File.separator + fileName;
            //格式转换
            try {
                //1.读取本地png图片or读取url图片
                File input = new File(path + File.separator + lastImg);
                BufferedImage bimg = ImageIO.read(input);//读取本地图片
                //BufferedImage bimg = ImageIO.read(new URL("http://img.alicdn.com/tfs/TB1kbMoUOLaK1RjSZFxXXamPFXa.png"));//读取url图片
                //2. 填充透明背景为白色
                BufferedImage res = new BufferedImage(bimg.getWidth(), bimg.getHeight(), BufferedImage.TYPE_INT_RGB);
                res.createGraphics().drawImage(bimg, 0, 0, Color.WHITE, null); //背景填充色设置为白色，也可以设置为其他颜色比如粉色Color.PINK
                //3. 保存成jpg到本地
                File output = new File(finalImg);
                ImageIO.write(res, "jpg", output);
                //4. 删除原图
                boolean delete = input.delete();
                System.out.println("delete = " + delete);
                result = fileName;
            } catch (Exception e) {
                e.printStackTrace();
                return result;
            }
        } else {
            return result;
        }
        return result;
    }

}
