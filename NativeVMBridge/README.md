# Electron - Java - Bridge

本项目用于将Electron和Java VM进行集成，它能够使用JNI接口启动一个Java虚拟机，并且加载指定的classpath和mainClass，
通过它们启动一个Java项目。

启动Java项目会导致线程阻塞，所以main方法请务必在Thread中执行。

目前支持的系统：Window / MacOS

编译使用的指令如下：

```
mkdir Build
cd Build
cmake ..
cmake --build . --config Release
```

## Windows

如果需要编译此项目，请安装Visual Studio并且安装CMake，通过CMake编译本项目可以得到对应的动态链接库，如果你想用使用cmake命令，请把CMake的安装目录下的bin文件夹加入系统的Path，重启终端或者命令提示符，你就能执行cmake命令了。

> 请注意：如果需要使用Javaagent，请复制以下动态链接库到Electron项目的根目录中：
> instrument.dll，jli.dll，java.dll，否则会由于找不到agent的library依赖库而
> 无法正常启动。

## Mac OS

如果需要编译此项目，请按照XCode，它将会提供CMake和相关的GCC工具链，通过CMake编译此项目可得到动态链接库。

## Linux

尚未支持。