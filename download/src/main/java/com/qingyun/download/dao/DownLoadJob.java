package com.qingyun.download.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.text.TextUtils;

import com.qingyun.download.DownLoadState;
import com.qingyun.download.utils.LogUtil;

/**
 * 作者： qingyun on 17/1/5.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
@Keep
public class DownLoadJob implements Parcelable
{
    private static final String TEMP_SUFFIX = ".download";
    private String userAgent = "";
    private boolean showNotification = false;
    private String downActivityName = "";
    private String downLoadUrl = "";
    private String cacheFilePath = "";
    private String fileName = "";
    private String fileIconUrl = "";
    private int downLoadState = DownLoadState.init;

    private long fileSize = 0;
    private long fileCurrentSize = 0;
    private long speed = 0;
    private String errorMessage;

    public DownLoadJob()
    {

    }

    public DownLoadJob(String downLoadUrl, String cacheFilePath)
    {
        this.downLoadUrl = downLoadUrl;
        this.cacheFilePath = cacheFilePath;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        DownLoadJob other = (DownLoadJob) obj;
        if (other.getDownLoadUrl().equals(downLoadUrl))
        {
            return true;
        }
        return false;
    }

    @Override
    public String toString()
    {
        return "DownLoadJob:downLoadUrl->" + downLoadUrl + "  cacheFilePath->" + cacheFilePath;
    }


    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
    }

    public String getCacheFilePath()
    {
        return cacheFilePath;
    }

    public void setCacheFilePath(String cacheFilePath)
    {
        this.cacheFilePath = cacheFilePath;
    }

    public String getDownActivityName()
    {
        return downActivityName;
    }

    public void setDownActivityName(String downActivityName)
    {
        this.downActivityName = downActivityName;
    }

    public String getDownLoadUrl()
    {
        return downLoadUrl;
    }

    public void setDownLoadUrl(String downLoadUrl)
    {
        this.downLoadUrl = downLoadUrl;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public boolean isShowNotification()
    {
        return showNotification;
    }

    public void setShowNotification(boolean showNotification)
    {
        this.showNotification = showNotification;
    }

    public String getTempFilePath()
    {
        if (!TextUtils.isEmpty(this.cacheFilePath))
        {
            return this.cacheFilePath + TEMP_SUFFIX;
        }
        return "";
    }

    public int getDownLoadState()
    {
        return downLoadState;
    }

    public void setDownLoadState(int downLoadState)
    {
        this.downLoadState = downLoadState;
    }

    public String getUserAgent()
    {
        return userAgent;
    }

    public void setUserAgent(String userAgent)
    {
        this.userAgent = userAgent;
    }

    public long getFileCurrentSize()
    {
        return fileCurrentSize;
    }

    public void setFileCurrentSize(long fileCurrentSize)
    {
        this.fileCurrentSize = fileCurrentSize;
    }

    public long getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(long fileSize)
    {
        this.fileSize = fileSize;
    }

    public long getSpeed()
    {
        return speed;
    }

    public void setSpeed(long speed)
    {
        this.speed = speed;
    }

    public int getProgress()
    {
        return (int) (fileCurrentSize / (fileSize * 1.0f) * 100);
    }

    public String getFileIconUrl()
    {
        return fileIconUrl;
    }

    public void setFileIconUrl(String fileIconUrl)
    {
        this.fileIconUrl = fileIconUrl;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }


    @Override
    public int describeContents()
    {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags)
    {/**/
        dest.writeString(this.errorMessage);
        dest.writeString(this.userAgent);
        dest.writeByte(showNotification ? (byte) 1 : (byte) 0);
        dest.writeString(this.downActivityName);
        dest.writeString(this.downLoadUrl);
        dest.writeString(this.cacheFilePath);
        dest.writeString(this.fileName);
        dest.writeString(this.fileIconUrl);
        dest.writeInt(this.downLoadState);
        dest.writeLong(this.fileSize);
        dest.writeLong(this.fileCurrentSize);
        dest.writeLong(this.speed);
    }

    public boolean getShowNotification()
    {
        return this.showNotification;
    }

    protected DownLoadJob(Parcel in)
    {
        this.errorMessage = in.readString();
        this.userAgent = in.readString();
        this.showNotification = in.readByte() != 0;
        this.downActivityName = in.readString();
        this.downLoadUrl = in.readString();
        this.cacheFilePath = in.readString();
        this.fileName = in.readString();
        this.fileIconUrl = in.readString();
        this.downLoadState = in.readInt();
        this.fileSize = in.readLong();
        this.fileCurrentSize = in.readLong();
        this.speed = in.readLong();
    }

    public static final Creator<DownLoadJob> CREATOR = new Creator<DownLoadJob>()
    {
        public DownLoadJob createFromParcel(Parcel source)
        {
            return new DownLoadJob(source);
        }

        public DownLoadJob[] newArray(int size)
        {
            return new DownLoadJob[size];
        }
    };
}
