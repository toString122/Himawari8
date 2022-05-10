package com.liwj.client.utils;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.win32.StdCallLibrary;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

/**
 * @description: 图片工具类
 * @author: liwj
 * @create: 2022-05-10 15:35
 **/
public class ImgUtils {

    public static String downloadImg() throws Exception {
        String imgName = getImgName();
        System.out.println("imgName = " + imgName);
        if (imgName == null || "".equals(imgName)) {
            throw new Exception("图片名称为空");
        }
        String pictureUrl = "http://localhost:8000/" + imgName;
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
        File file = new File("C://tmp1");
        if (!file.exists()) {
            boolean isMake = file.mkdirs();
        }
        String fileName = new Date().getTime() + ".jpg";
        //输出流，图片输出的目的文件
        BufferedOutputStream fos = new BufferedOutputStream(Files.newOutputStream(Paths.get("C://tmp1/" + fileName)));
        while ((len = stream.read(test)) != -1) {
            fos.write(test, 0, len);
        }
        stream.close();
        fos.close();
        return fileName;
    }

    public static String getImgName() {
        String url = "http://localhost:8000/getImg";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, String.class);
    }


    private interface MyUser32 extends StdCallLibrary {

        MyUser32 INSTANCE = (MyUser32) Native.loadLibrary("user32", MyUser32.class);

        boolean SystemParametersInfoA(int uiAction, int uiParam, String fnm, int fWinIni);
    }


    public static void setWallpaper() throws Exception {
        String fileName = downloadImg();
        String finalImg = "C:/tmp1/" + fileName;
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
                "Control Panel\\Desktop", "Wallpaper", finalImg);
        //WallpaperStyle = 10 (Fill), 6 (Fit), 2 (Stretch), 0 (Tile), 0 (Center)
        //For windows XP, change to 0
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
                "Control Panel\\Desktop", "WallpaperStyle", "6"); //fill
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
                "Control Panel\\Desktop", "TileWallpaper", "0");   // no tiling

        // refresh the desktop using User32.SystemParametersInfo(), so avoiding an OS reboot
        int SPI_SETDESKWALLPAPER = 0x14;
        int SPIF_UPDATEINIFILE = 0x01;
        int SPIF_SENDWININICHANGE = 0x02;
        // User32.System
        boolean result = MyUser32.INSTANCE.SystemParametersInfoA(SPI_SETDESKWALLPAPER, 0,
                finalImg, SPIF_UPDATEINIFILE | SPIF_SENDWININICHANGE);
        if (result) {
            System.out.println("Wallpaper set successfully");
        } else {
            System.out.println("Wallpaper set failed");
        }
    }

}
