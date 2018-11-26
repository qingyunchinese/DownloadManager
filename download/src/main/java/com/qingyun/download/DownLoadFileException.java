package com.qingyun.download;

/**
 * 作者： qingyun on 17/1/5.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
public class DownLoadFileException extends Exception
{
    private String errorBody = "";
    private String errorCode = "";

    public DownLoadFileException(String errorBody, String errorCode) {
        this.errorBody = errorBody;
    }

    public DownLoadFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorBody() {
        return errorBody;
    }

    public void setErrorBody(String errorBody) {
        this.errorBody = errorBody;
    }

    public static String getDetailMessage(int errorCode) {
        String detailMessage = "";
        switch (errorCode) {
            case 404:
                detailMessage = "无法找到指定位置的资源,资源地址不可用";
                break;
            case 410:
                detailMessage = "无法找到指定位置的资源,资源已永久删除";
                break;
            case 400:
                detailMessage = "无法访问到指定位置的资源";
                break;
            case 405:
                detailMessage = "禁止访问资源";
                break;
        }
        return detailMessage;
    }
}
