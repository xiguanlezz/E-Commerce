package com.cj.cn.util;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.upload.FastImageFile;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * FastDFS的工具类:
 * 参考GitHub提供的接口:
 * https://github.com/tobato/FastDFS_Client/blob/master/src/main/java/com/github/tobato/fastdfs/service/FastFileStorageClient.java
 */
@Component("fastDFSClient")
public class FastDFSClientUtil {
    private Logger logger = LoggerFactory.getLogger(FastDFSClientUtil.class);
    @Autowired
    private FastFileStorageClient storageClient;

    /**
     * 上传文件
     *
     * @param file 要上传的文件
     * @return 文件路径
     */
    public String uploadMp3(MultipartFile file) {
        //第一个参数表示文件输入流; 第二个参数表示文件大小; 第三个参数表示文件扩展名; 第四个参数表示文件元数据(描述文件的)
        StorePath storePath = null;
        try {
            storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(),
                    "mp3", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println(storePath.getFullPath());
//        System.out.println(storePath.getPath());
//        System.out.println(storePath.getPath());
        return storePath.getGroup() + "/" + storePath.getPath();
        //StorePath类中包括group和path
    }

    /**
     * 上传文件
     *
     * @param file 要上传的文件
     * @return 文件存储路径(group1 / M00 / 00 / 00 / ...)
     */
    public String uploadFile(MultipartFile file) {
        StorePath storePath = null;
        try {
            String fileExtendName = PropertiesUtil.getProperty("fastdfs.pic.extend.name");
            storePath = storageClient.uploadImage(new FastImageFile(file.getInputStream(), file.getSize(), fileExtendName, null));
            return storePath.getGroup() + "/" + storePath.getPath();
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("上传文件发生错误");
            return "";
        }
    }

    /**
     * 上传文件
     *
     * @param file 要上传的文件
     * @return 文件存储路径(group1 / M00 / 00 / 00 / ...)
     */
    public String uploadFile(File file) {
        StorePath storePath = null;
        try {
            String fileExtendName = PropertiesUtil.getProperty("fastdfs.pic.extend.name");
            storePath = storageClient.uploadImage(new FastImageFile(new FileInputStream(file), file.length(), fileExtendName, null));
            return storePath.getGroup() + "/" + storePath.getPath();
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("上传文件发生错误");
            return "";
        }
    }


    /**
     * 删除文件
     *
     * @param filePath 文件路径(group1/M00/00/00/...)
     * @return 是否删除成功
     */
    public boolean deleteFile(String filePath) {
        if (StringUtils.isBlank(filePath))
            return false;
        StorePath storePath = StorePath.parseFromUrl(filePath);
        if (storePath == null)
            return false;
        try {
            storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
