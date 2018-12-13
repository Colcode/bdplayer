
**百度播放器**   [![](https://www.jitpack.io/v/W252016021/bdplayer.svg)](https://www.jitpack.io/#W252016021/bdplayer)
-
# bdplayer
------
* **基于百度视频sdk封装的播放器类库**
```java
全功能版支持全媒体格式
支持滑动快进和音量声音滑动调节
支持倍速播放<0.8x-2.0x>
支持锁屏播放
支持横竖屏切换
支持裁剪方式切换
```
##### 目前所知问题:
```java
.WMV文件无法快进
```
##### 引用方式1:

```java
allprojects {
 repositories {
   ...
      maven { url 'https://www.jitpack.io' }
   }
 }
```

```java
 dependencies {
	        implementation 'com.github.W252016021:bdplayer:v1.0'
	}
```
##### 引用方式2：
```java
直接下载*bdplayer.aar*文件导入项目即可
```
###### 下载地址: [bdplayer.aar](https://www.lanzous.com/b521906/ "bdplayer.aar")
##### 播放方式：

```java
VideoInfo info = new VideoInfo();
info.setAK("*********************************");//你在百度申请到的access key,无效ak可能导致无法播放
info.setUrl("http://192.168.1.103/dy.mp4");
info.setTitle("毒液BD中英双字");
startActivity(new Intent(this, VideoPlayerActivity.class).putExtra("VideoInfo", info));
```
##### 百度access key申请地址：
[百度access key申请](https://console.bce.baidu.com/iam/#/iam/accesslist "百度access key申请")

##### 防混淆设置
将以下语句加入到您的proguard混淆配置文件中，

```java
-libraryjars libs/bdplayer.jar
-keep class com.baidu.cloud.media.**{ *;}
```

###### by QQ252016021 2018.12.13
