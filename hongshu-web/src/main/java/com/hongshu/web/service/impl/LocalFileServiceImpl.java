package com.hongshu.web.service.impl;

import com.hongshu.common.constant.Constantss;
import com.hongshu.common.exception.exceptionType.InsertException;
import com.hongshu.common.global.ErrorCode;
import com.hongshu.common.utils.DateUtilss;
import com.hongshu.common.utils.FileUtils;
import com.hongshu.common.utils.StringUtilss;
import com.hongshu.web.domain.entity.WebFileSort;
import com.hongshu.web.service.IWebFileSortService;
import com.hongshu.web.service.LocalFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * 本地文件系统实现类【通过IO流存储到本地】
 *
 * @Author hongshu
 */
@Slf4j
//@RefreshScope
@Service
public class LocalFileServiceImpl implements LocalFileService {

    @Autowired
    IWebFileSortService fileSortService;
    /**
     * 本地图片上传路径
     */
    @Value(value = "${file.upload.path}")
    private String path;

    @Override
    public List<String> batchUploadFile(List<MultipartFile> multipartFileList, WebFileSort fileSort) throws IOException {
        List<String> urlList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFileList) {
            urlList.add(this.uploadSingleFile(multipartFile, fileSort));
        }
        return urlList;
    }

    @Override
    public String uploadFile(MultipartFile multipartFile, WebFileSort fileSort) throws IOException {
        return this.uploadSingleFile(multipartFile, fileSort);
    }

    @Override
    public String uploadPictureByUrl(String itemUrl, WebFileSort fileSort) {
        String sortUrl = fileSort.getUrl();
        //判断url是否为空，如果为空，使用默认
        if (StringUtilss.isEmpty(sortUrl)) {
            sortUrl = "base/common/";
        } else {
            sortUrl = fileSort.getUrl();
        }
        //获取新文件名 【默认为jpg】
        String newFileName = System.currentTimeMillis() + ".jpg";
        //文件绝对路径
        String newPath = path + sortUrl + "/jpg/" + DateUtilss.getYears() + "/"
                + DateUtilss.getMonth() + "/" + DateUtilss.getDay() + "/";

        //文件相对路径
        String fileUrl = sortUrl + "/jpg/" + DateUtilss.getYears() + "/"
                + DateUtilss.getMonth() + "/" + DateUtilss.getDay() + "/" + newFileName;

        String saveUrl = newPath + newFileName;

        // 将图片上传到本地服务器中以及七牛云中
        BufferedOutputStream out = null;
        FileOutputStream os = null;
        // 输入流
        InputStream inputStream = null;

        // 判断文件是否存在
        java.io.File file1 = new java.io.File(newPath);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        try {
            // 构造URL
            URL url = new URL(itemUrl);
            // 打开连接
            URLConnection con = url.openConnection();
            // 设置用户代理
            con.setRequestProperty("User-agent", "	Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
            // 设置10秒
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);
            // 当获取的相片无法正常显示的时候，需要给一个默认图片
            inputStream = con.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            java.io.File file = new java.io.File(saveUrl);
            os = new FileOutputStream(file, true);
            // 开始读取
            while ((len = inputStream.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            return fileUrl;
        } catch (Exception e) {
            log.error("上传图片失败: {}", e.getMessage());
            throw new InsertException(ErrorCode.INSERT_DEFAULT_ERROR, "获取图片超时，文件上传失败");
        } finally {
            try {
                // 完毕，关闭所有链接
                os.close();
                inputStream.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

    }

    /**
     * 本地文件服务图片上传【上传到本地硬盘】
     *
     * @return
     */
    private String uploadSingleFile(MultipartFile multipartFile, WebFileSort fileSort) throws IOException {
        String sortUrl = fileSort.getUrl();
        //判断url是否为空，如果为空，使用默认
        if (StringUtilss.isEmpty(sortUrl)) {
            sortUrl = "base/common/";
        } else {
            sortUrl = fileSort.getUrl();
        }
        String oldName = multipartFile.getOriginalFilename();
        //获取扩展名，默认是jpg
        String picExpandedName = FileUtils.getPicExpandedName(oldName);
        //获取新文件名
        String newFileName = System.currentTimeMillis() + Constantss.SYMBOL_POINT + picExpandedName;

        String newPath = path + sortUrl + "/" + picExpandedName + "/" + DateUtilss.getYears() + "/"
                + DateUtilss.getMonth() + "/" + DateUtilss.getDay() + "/";

        String picurl = sortUrl + "/" + picExpandedName + "/" + DateUtilss.getYears() + "/"
                + DateUtilss.getMonth() + "/" + DateUtilss.getDay() + "/" + newFileName;
        String saveUrl = newPath + newFileName;

        // 保存本地，创建目录
        java.io.File file1 = new java.io.File(newPath);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        java.io.File saveFile = new java.io.File(saveUrl);
        // 序列化文件到本地
        saveFile.createNewFile();
        multipartFile.transferTo(saveFile);
        return picurl;
    }
}
