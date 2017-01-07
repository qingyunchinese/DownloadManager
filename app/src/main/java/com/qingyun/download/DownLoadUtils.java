package com.qingyun.download;

import com.qingyun.utils.SDCardManager;

import java.io.File;

/**
 * 作者： qingyun on 17/1/5.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
public class DownLoadUtils {
    /**
     * 文件大小转化为相应的B/MB/G单位
     *
     * @param fileSize
     * @return
     */
    public static String convertStorage(long fileSize) {
        // TODO Auto-generated method stub
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (fileSize >= gb) {
            return String.format("%.1f GB", (float) fileSize / gb);
        } else if (fileSize >= mb) {
            float f = (float) fileSize / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (fileSize >= kb) {
            float f = (float) fileSize / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", fileSize);
    }

    /**
     * 获取文件后缀
     *
     * @param filePath
     * @return
     */
    public static String getFileSuffix(String filePath) {
        int dotPosition = filePath.lastIndexOf('.');
        if (dotPosition == -1)
            return "*/*";
        String ext = filePath.substring(dotPosition + 1, filePath.length())
                .toLowerCase();
        return ext;
    }

    /**
     * 获取路径上文件名
     *
     * @param url
     * @return
     */
    public static String getUrlContrainFileName(String url) {
        // TODO Auto-generated method stub
        if (url == null || url.equals("") || !url.contains("/")) {
            return "";
        }
        int separatorPosition = url.lastIndexOf("/");
        String result = url.substring(separatorPosition + 1, url.length());
        return result;
    }


    /**
     * 文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean isFileExist(String filePath) {
        // TODO Auto-generated method stub
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public static void deleteDir(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteDir(files[i]);
                }
                file.delete();
            }
        } else {
            System.out.println("所删除的文件不存在");
        }
    }

    public static void deleteFile(String filePath) {
        // TODO Auto-generated method stub
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    public static String getFileMediaType(String fileUrl) {
        String fileSuffix = getFileSuffix(fileUrl);
        fileSuffix = fileSuffix.toLowerCase();
        String fileMediaType;
        switch (fileSuffix) {
            case "png":
            case "jpg":
            case "jpeg":
            case "gif":
                fileMediaType = "image/" + fileSuffix;
                break;
            case "apk":
            case "text":
            case "pdf":
            case "zip":
                fileMediaType = "application/" + fileSuffix;
                break;
            case "mp4":
            case "3gp":
                fileMediaType = "video/" + fileSuffix;
                break;
            case "mp3":
            case "wav":
            case "ogg":
                fileMediaType = "audio/" + fileSuffix;
                break;
            default:
                fileMediaType = "application/octet-stream";
        }
        return fileMediaType + "; charset=utf-8";
    }


    public static String getDownLoadDefaultPath(String downLoadUrl) {
        // TODO Auto-generated method stub
        String fileName = DownLoadUtils.getUrlContrainFileName(downLoadUrl);
        String filePath = SDCardManager.getInstance().getAppDir().getAbsolutePath() + "/" + fileName;
        return filePath;
    }

    public static String getDownLoadDefaultName(String downLoadUrl) {
        // TODO Auto-generated method stub
        String fileName = DownLoadUtils.getUrlContrainFileName(downLoadUrl);
        return fileName;
    }
}
