package com.hongshu.common.utils;

import com.hongshu.common.constant.Constantss;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;

/**
 * 文件相关工具类
 *
 * @Author hongshu
 */
@Slf4j
public class AboutFileUtil {

    public static final String[] IMG_FILE = {
            Constantss.FILE_SUFFIX_BMP,
            Constantss.FILE_SUFFIX_JPG,
            Constantss.FILE_SUFFIX_PNG,
            Constantss.FILE_SUFFIX_TIF,
            Constantss.FILE_SUFFIX_GIF,
            Constantss.FILE_SUFFIX_JPEG,
            Constantss.FILE_SUFFIX_WEBP};
    public static final String[] DOC_FILE = {
            Constantss.FILE_SUFFIX_DOC,
            Constantss.FILE_SUFFIX_DOCX,
            Constantss.FILE_SUFFIX_TXT,
            Constantss.FILE_SUFFIX_HLP,
            Constantss.FILE_SUFFIX_WPS,
            Constantss.FILE_SUFFIX_RTF,
            Constantss.FILE_SUFFIX_XLS,
            Constantss.FILE_SUFFIX_XLSX,
            Constantss.FILE_SUFFIX_PPT,
            Constantss.FILE_SUFFIX_PPTX,
            Constantss.FILE_SUFFIX_JAVA,
            Constantss.FILE_SUFFIX_HTML,
            Constantss.FILE_SUFFIX_PDF,
            Constantss.FILE_SUFFIX_MD,
            Constantss.FILE_SUFFIX_SQL,
            Constantss.FILE_SUFFIX_CSS,
            Constantss.FILE_SUFFIX_JS,
            Constantss.FILE_SUFFIX_VUE,
            Constantss.FILE_SUFFIX_JAVA};
    public static final String[] VIDEO_FILE = {
            Constantss.FILE_SUFFIX_AVI,
            Constantss.FILE_SUFFIX_MP4,
            Constantss.FILE_SUFFIX_MPG,
            Constantss.FILE_SUFFIX_MOV,
            Constantss.FILE_SUFFIX_SWF,
            Constantss.FILE_SUFFIX_3GP,
            Constantss.FILE_SUFFIX_RM,
            Constantss.FILE_SUFFIX_RMVB,
            Constantss.FILE_SUFFIX_WMV,
            Constantss.FILE_SUFFIX_MKV};
    public static final String[] MUSIC_FILE = {
            Constantss.FILE_SUFFIX_WAV,
            Constantss.FILE_SUFFIX_AIF,
            Constantss.FILE_SUFFIX_AU,
            Constantss.FILE_SUFFIX_MP3,
            Constantss.FILE_SUFFIX_RAM,
            Constantss.FILE_SUFFIX_WMA,
            Constantss.FILE_SUFFIX_MMF,
            Constantss.FILE_SUFFIX_AMR,
            Constantss.FILE_SUFFIX_AAC,
            Constantss.FILE_SUFFIX_FLAC};
    public static String[] ALL_FILE = new String[1];

    static {
        ALL_FILE = ArrayUtils.addAll(ALL_FILE, IMG_FILE);
        ALL_FILE = ArrayUtils.addAll(ALL_FILE, DOC_FILE);
        ALL_FILE = ArrayUtils.addAll(ALL_FILE, VIDEO_FILE);
        ALL_FILE = ArrayUtils.addAll(ALL_FILE, MUSIC_FILE);
    }

    /**
     * 判断一个文件是否存在，不存在就创建它 Method execute,只能建最后面那个目录
     *
     * @param path
     * @return
     */
    public void creatFile(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            log.error("该目录不存在");
        } else {
            file.mkdir();
        }
    }

    /**
     * 从文件名中得到其后缀名
     *
     * @param filename
     * @return 后缀名
     */
    public String getFileSuffix(String filename) {
        String suffix;
        suffix = filename.substring(
                filename.lastIndexOf(Constantss.SYMBOL_POINT) + 1);
        return suffix;
    }

    /**
     * 通过其后缀名判断其是否合法,合法后缀名为常见的
     *
     * @param suffix 后缀名
     * @return 合法返回true，不合法返回false
     */
    public boolean isSafe(String suffix) {
        suffix = suffix.toLowerCase();
        for (int i = 0; i < ALL_FILE.length; i++) {
            if (ALL_FILE[i].equals(suffix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通过其后缀名判断其是否是图片
     *
     * @param suffix 后缀名
     * @return 合法返回true，不合法返回false
     */
    public boolean isPic(String suffix) {
        suffix = suffix.toLowerCase();
        for (int i = 0; i < IMG_FILE.length; i++) {
            if (IMG_FILE[i].equals(suffix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 进行上传文件的相关操作
     *
     * @param file
     * @throws IOException
     * @throws FileNotFoundException
     */
    public int uploadFile(File file, String fileLocation) throws IOException {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        int result = 1;
        try {
            FileInputStream fis = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(new File(fileLocation));
            bis = new BufferedInputStream(fis);
            bos = new BufferedOutputStream(fos);

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            try {
                try {
                    if (null != bis) {
                        bis.close();
                        bis = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    result = 0;
                }
                try {
                    if (null != bos) {
                        bos.close();
                        bos = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    result = 0;
                }
                fis.close();
                fos.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                result = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        } finally {
            try {
                if (null != bis) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                result = 0;

            }
            try {
                if (null != bos) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                result = 0;

            }
        }
        return result;
    }

    /**
     * 计算文件大小，将long类型转换为String类型
     *
     * @param fileSize
     * @return
     */
    public String getFileStringSize(long fileSize) {
        //size不能为0？
        double temp = 0.0;
        String ssize = "";
        temp = (double) fileSize / Constantss.NUM_1024;
        if (temp >= Constantss.NUM_1024) {
            temp = temp / Constantss.NUM_1024;
            if (temp >= Constantss.NUM_1024) {
                temp = temp / Constantss.NUM_1024;
                ssize = temp + "000";
                ssize = ssize.substring(Constantss.NUM_ZERO, ssize.indexOf(Constantss.SYMBOL_POINT) + Constantss.NUM_THREE) + "GB";
            } else {
                ssize = temp + "000";
                ssize = ssize.substring(Constantss.NUM_ZERO, ssize.indexOf(Constantss.SYMBOL_POINT) + Constantss.NUM_THREE) + "MB";
            }
        } else {
            ssize = temp + "000";
            ssize = ssize.substring(Constantss.NUM_ZERO, ssize.indexOf(Constantss.SYMBOL_POINT) + Constantss.NUM_THREE) + "KB";
        }
        return ssize;
    }
}
