package com.liwj.server.utils;

import com.liwj.server.pojo.Pojo;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: FTP工具类
 * @author: liwj
 * @create: 2022-05-10 14:06
 **/
public class FtpUtils {

    public static boolean downLoadFTP(FTPClient ftp, String filePath, String fileName, String downPath) {
        File file0 = new File(downPath);
        if (!file0.exists() && !file0.isDirectory()) {
            boolean mkdir = file0.mkdir();
            System.out.println("mkdir = " + mkdir);
        }
        // 默认失败
        boolean flag = false;
        try {
            // 跳转到文件目录
            ftp.changeWorkingDirectory(filePath);
            // 获取目录下文件集合
            ftp.enterLocalPassiveMode();
            FTPFile[] files = ftp.listFiles();
            for (FTPFile file : files) {
                // 取得指定文件并下载
                if (file.getName().equals(fileName)) {
                    File downFile = new File(downPath + File.separator
                            + file.getName());
                    OutputStream out = Files.newOutputStream(downFile.toPath());
                    flag = ftp.retrieveFile(file.getName(), out);
                    out.close();
                    if (flag) {
                        System.out.println("下载成功");
                    } else {
                        System.out.println("下载失败");
                    }
                    break;
                }

            }

        } catch (Exception e) {
            System.out.println("下载失败");
        }

        return flag;
    }

    public static boolean downloadImg(FTPClient ftpClient, String path) throws IOException {
        boolean isTrue;
        OutputStream os;
        File localFile = new File("C:\\tmp1");
        if (!localFile.exists() && !localFile.isDirectory()) {
            boolean mkdir = localFile.mkdir();
            System.out.println("mkdir = " + mkdir);
        }
        os = Files.newOutputStream(localFile.toPath());
        isTrue = ftpClient.retrieveFile(new String(path.getBytes(), StandardCharsets.ISO_8859_1), os);
        os.close();
        return isTrue;
    }

    public static String getLastDic(String path, FTPClient ftpClient) {
        Map<String, Object> modifiedTimeMap = getAllFileLastModifiedTime(ftpClient, path);
        Pojo pojo = new Pojo();
        for (Map.Entry<String, Object> stringObjectEntry : modifiedTimeMap.entrySet()) {
            Date date = new Date(stringObjectEntry.getValue().toString());
            if (pojo.getTime() == null || date.getTime() > pojo.getTime().getTime()) {
                pojo.setTime(date);
                pojo.setName(stringObjectEntry.getKey());
            }
        }
        return pojo.getName();
    }

    public static String getLastImg(FTPClient ftpClient, String path) {
        Pojo pojo = new Pojo();
        FTPFile[] fileList = new FTPFile[0];
        try {
            fileList = ftpClient.listFiles(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (FTPFile ftpFile : fileList) {
            String name = ftpFile.getName();
            if (name.endsWith(".png") && name.contains("FLDK")) {
                Date modifiedDate = ftpFile.getTimestamp().getTime();
                if (pojo.getTime() == null || modifiedDate.after(pojo.getTime())) {
                    pojo.setTime(modifiedDate);
                    pojo.setName(name);
                }
            }
        }
        return pojo.getName();
    }

    public static FTPClient getFtpClient(String host, String username, String password) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(host, 21);
            boolean isLogin = ftpClient.login(username, password);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            //连接ftp失败返回null
            if (!isLogin) {
                ftpClient.disconnect();
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ftpClient.enterLocalPassiveMode();
        return ftpClient;
    }

    public static Map<String, Object> getAllFileLastModifiedTime(FTPClient ftpClient, String path) {
        FTPFile[] fileList = new FTPFile[0];
        try {
            fileList = ftpClient.listFiles(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //HashMap容量：fileList.length
        Map<String, Object> map = new HashMap(fileList.length);
        for (FTPFile ftpFile : fileList) {
            //key 文件名；value修改时间(java.util.Date)
            map.put(ftpFile.getName(), ftpFile.getTimestamp().getTime());
        }
        return map;
    }
}
