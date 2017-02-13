Android 下载管理:
====  
      1.多线程管理，根据网络状态，调整线程池数量.  
      2.采用okhttp进行网络通讯.  
      3.greenDao进行数据库存储.  
___
设计思路：
====
      下载Service设置android:process=":downLoadService"属性，讲其放入其它进程，不占用应用进程资源;  
      通过BroadCastService与主进程通信主进程QYDownLoadManager采用观察者模式，收到广播后，更新下载对象的状态，并通知实现了DownloadListener接口的类，进行UI更新;  
      不过记得在关闭应用时，一定要记得关闭下载进程。
