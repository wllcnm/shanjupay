package com.shanjupay.common.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.IOUtils;

import java.io.*;
import java.util.UUID;

public class QiNiuUtil {

    public static void upload2qiniu(String accessKey, String secretKey, String bucket, Region region, String fileName, byte[] bytes) {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(region);
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本

        UploadManager uploadManager = new UploadManager(cfg);

        String key = null;

        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(bytes, fileName, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
                throw new RuntimeException(r.toString());
            }
            throw new RuntimeException(r.toString());
        }
    }


    //测试文件上传
    private static void testUpload() {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region2());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
//...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
//...生成上传凭证，然后准备上传
        String accessKey = "NttkmP7tBhIVc0Ap-k4a3AXzwBHTnNtuA2JmutbU";
        String secretKey = "F8c4f2-RW2dnJZyp3Svz1wsmssLejchovy6yAs-d";
        String bucket = "awei-my-oss";
//默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = null;
        try {
            FileInputStream file = new FileInputStream(new File("G:\\绿图\\绿图\\d33417e2c561abd2e8128361ee9b2bf9c7bda0c1f4aceff34.webp"));
            byte[] bytes = IOUtils.toByteArray(file);

            //生成一个不重复的key
            key = UUID.randomUUID().toString();
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            try {
                Response response = uploadManager.put(bytes, key, upToken);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (UnsupportedEncodingException ex) {
            //ignore
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        //上传测试
        QiNiuUtil.testUpload();
    }

}
