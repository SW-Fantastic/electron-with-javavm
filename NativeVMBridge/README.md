# Electron - Java - Bridge

本项目用于将Electron和Java VM进行集成，它能够使用JNI接口启动一个Java虚拟机，并且加载指定的classpath和mainClass，
通过它们启动一个Java项目。

启动Java项目会导致线程阻塞，所以main方法请务必在Thread中执行。

对于windows ：

如果需要编译此项目，请安装Visual Studio并且安装CMake，通过CMake编译本项目可以得到对应的动态链接库。

对于其他系统：

暂时不支持，如果以后做了就会放上来。
