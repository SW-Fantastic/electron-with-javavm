# WebBackend

本项目展示了如何将Electron和Java VM结合使用，它是一个基于SpringBoot的简单
Web项目，启动本项目的时候，需要指定main方法的第一个参数为UDP端口号，它表示Electron
监听的UDP端口，本应用将会以UDP消息的形式和Electron直接进行通信。

本应用的源码非常简单，它包含如下的部分：

## 业务逻辑

一些业务逻辑，用于测试这种开发的方式是否能够正常使用，并且为Electron提供部分
可供把玩的小功能。

主要涉及这些包：`org.swdc.demo.table.entity`,`org.swdc.demo.table.repo`
`org.swdc.demo.table.web.*`

## UDP通信

主要用于和Electron进行通信和事件的发送，例如传输后端输出的Log，当然只是Log，
System.out和System.err是不能输出到Electron的，这些UDP消息通过这一部分来
运作并且与Electron形成配合。

涉及的包是这个：`org.swdc.demo.table.udp`

## 其他

至于其他部分其实没有很多需要注意的，本项目不定期更新。