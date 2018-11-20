package com.qingyun.download;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.qingyun.download.utils.LogUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 作者： qingyun on 17/1/5.
 * 邮箱：419254872@qq.com
 * 版本：v1.0
 * 描述：
 */
@Entity(nameInDb = "Download", generateConstructors = false)
public class DownLoadRequestDao implements Parcelable {
    @Id
    private Long id;
    @Transient
    private static final String TEMP_SUFFIX = ".download";
    @Property(nameInDb = "userAgent")
    private String userAgent = "";
    /**
     * 是否在通知栏显示
     */
    @Transient
    private boolean showNotification = false;
    /**
     * 点击通知栏打开的Activity
     */
    @Transient
    private String downActivityName = "";
    /**
     * 文件下载网络址
     */
    @Index(name = "url", unique = true)
    private String downLoadUrl = "";
    /**
     * 文件下载保存的地址
     */
    @Property(nameInDb = "cachePath")
    @NotNull
    private String cacheFilePath = "";
    /**
     * 通知栏里显示的文件名称
     */
    @Property(nameInDb = "fileName")
    private String fileName = "";

    /**
     * 文件图标
     */
    @Property(nameInDb = "iconUrl")
    private String fileIconUrl = "";
    /**
     * 文件下载状态
     */
    private int downLoadState = DownLoadState.init;
    @Property(nameInDb = "fileSize")
    private long fileSize = 0;
    @Property(nameInDb = "fileCurrentSize")
    private long fileCurrentSize = 0;
    @Transient
    private long speed = 0;
    /**
     * 存储如数据库中的时间
     */
    @OrderBy("desc")
    private long createTime = -1;

    @Keep
    public DownLoadRequestDao() {

    }

    @Keep
    public DownLoadRequestDao(String downLoadUrl, String cacheFilePath) {
        this.downLoadUrl = downLoadUrl;
        this.cacheFilePath = cacheFilePath;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        DownLoadRequestDao other = (DownLoadRequestDao) obj;
        if (other.getDownLoadUrl().equals(downLoadUrl)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "DownLoadInfo:下载地址->" + downLoadUrl + "  保存地址->" + cacheFilePath;
    }


    @Override
    protected void finalize() throws Throwable {
        LogUtils.v("GC 回收");
        super.finalize();
    }

    public String getCacheFilePath() {
        return cacheFilePath;
    }

    public void setCacheFilePath(String cacheFilePath) {
        this.cacheFilePath = cacheFilePath;
    }

    public String getDownActivityName() {
        return downActivityName;
    }

    public void setDownActivityName(String downActivityName) {
        this.downActivityName = downActivityName;
    }

    public String getDownLoadUrl() {
        return downLoadUrl;
    }

    public void setDownLoadUrl(String downLoadUrl) {
        this.downLoadUrl = downLoadUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isShowNotification() {
        return showNotification;
    }

    public void setShowNotification(boolean showNotification) {
        this.showNotification = showNotification;
    }

    public String getTempFilePath() {
        if (!TextUtils.isEmpty(this.cacheFilePath)) {
            return this.cacheFilePath + TEMP_SUFFIX;
        }
        return "";
    }

    public int getDownLoadState() {
        return downLoadState;
    }

    public void setDownLoadState(int downLoadState) {
        this.downLoadState = downLoadState;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public long getFileCurrentSize() {
        return fileCurrentSize;
    }

    public void setFileCurrentSize(long fileCurrentSize) {
        this.fileCurrentSize = fileCurrentSize;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public int getProgress() {
        return (int) (fileCurrentSize / (fileSize * 1.0f) * 100);
    }

    public String getFileIconUrl() {
        return fileIconUrl;
    }

    public void setFileIconUrl(String fileIconUrl) {
        this.fileIconUrl = fileIconUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {/**/
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


    public boolean getShowNotification() {
        return this.showNotification;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }


    protected DownLoadRequestDao(Parcel in) {
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

    public static final Creator<DownLoadRequestDao> CREATOR = new Creator<DownLoadRequestDao>() {
        public DownLoadRequestDao createFromParcel(Parcel source) {
            return new DownLoadRequestDao(source);
        }

        public DownLoadRequestDao[] newArray(int size) {
            return new DownLoadRequestDao[size];
        }
    };
}
