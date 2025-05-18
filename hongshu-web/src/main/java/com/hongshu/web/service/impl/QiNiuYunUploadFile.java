package com.hongshu.web.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import com.google.gson.Gson;
import com.hongshu.common.utils.SpringContextUtils;
import com.hongshu.web.websocket.factory.OssFactory;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @Author hongshu
 */
public class QiNiuYunUploadFile implements OssFactory {

    private final String accessKey;

    private final String secretKey;

    private final String bucketName;

    private final String domain;

    public QiNiuYunUploadFile() {
        Environment bean = SpringContextUtils.getBean(Environment.class);
        this.domain = bean.getProperty("qiNiuYun.domain");
        this.accessKey = bean.getProperty("qiNiuYun.accessKey");
        this.secretKey = bean.getProperty("qiNiuYun.secretKey");
        this.bucketName = bean.getProperty("qiNiuYun.bucketName");
    }


    @Override
    public String save(MultipartFile file) {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.huabei());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本

        UploadManager uploadManager = new UploadManager(cfg);

        String filePath = "";
        String originalFilename = file.getOriginalFilename();
        String fileName = originalFilename.substring(originalFilename.lastIndexOf("."));
        ;

        //1 在文件名称里面添加随机唯一的值
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        // yuy76t5rew01.jpg
        fileName = uuid + fileName;

        //2 把文件按照日期进行分类
        //获取当前日期
        //   2019/11/12
        String datePath = new DateTime().toString("yyyy/MM/dd");
        //拼接
        //  2019/11/12/ewtqr313401.jpg
        fileName = datePath + "/" + fileName;

        try {
            byte[] uploadBytes = file.getBytes();
            // 获取文件流
            InputStream input = new ByteArrayInputStream(uploadBytes);
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucketName);
            Response response = uploadManager.put(input, fileName, upToken, null, null);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);

//            filePath = "http://" + domain + "/" + putRet.key;
            filePath = domain + "/" + putRet.key;
            return filePath;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Boolean delete(String path) {
        String replaceFileName = path.replace("http://" + domain + "/", "");
        Configuration cfg = new Configuration(Region.beimei());

        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);

        try {
            Response response = bucketManager.delete(bucketName, replaceFileName);
            return response.isOK();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
