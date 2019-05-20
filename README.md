# 不要直接运行jar好像直接运行跟netbeans编码不统一文字会智障
# 2019.5.11javahttpd(目前仅支持火狐核心的post)  
不愧是我，只会写bug
# 2019.5.12修改了缓冲区大小，现在可以正常传输二进制文件的图片，安装包，以及其他类型资源
文件名还是没解决
# 2019.5.13修改了细节，现在可以支持ie跟谷歌了
另外由于每次都要检查boundary所以尽量不要发大文件，后面会添加content-length字段，用来专门传大文件。
# 2019.5.14修改了细节
支持中文文件名
# 2019.5.17发现新问题
由于之前文件也是用String拼接，而java的定义跟gc问题，会越拼越慢，所以打算对contlength大的文件先用临时文件存放，然后再做处理。post类需要重新封装
# 2019.5.18使用临时文件
使用了临时文件存储，由于String拼接会越来越慢，使用文件存储方便处理文件，非文件也可以读取文件去获取参数。
# 添加了动态get的方法
由于原先的动态get没做，所以添加了上去，但是还没做get传参。
# 2019.5.20单身节快乐
终版，删掉了一些不需要的东西，url解析跟post非文件非boundary没做实在是懒，我本来只想做个作业系统，所以就这样了叭，如果有兴趣可以在上面添加
![运行](https://github.com/heweisheng/javahttpd/blob/master/%E5%B1%95%E7%A4%BA/1.png)
![运行](https://github.com/heweisheng/javahttpd/blob/master/%E5%B1%95%E7%A4%BA/2.png)
![运行](https://github.com/heweisheng/javahttpd/blob/master/%E5%B1%95%E7%A4%BA/3.png)
