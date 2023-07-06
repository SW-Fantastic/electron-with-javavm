# Electron With Java VM

本项目探索如何将Java应用和Electron进行集成，它的思路是通过`Electron FFI NAPI`
项目加载一个Native层的动态链接库，并于动态链接库内部载入JVM，通过它开启Java。

通常与Electron集成的Java项目会是一个Http项目，他需要一个端口来提供自己的服务，
这个端口不推荐固定为某个值，而是采用随机端口以防止端口的冲突。

Electron可以通过监听一个随机的UDP端口来接收来自Java层的消息，包括日志输出，
异常的打印，以及Java使用的端口地址。

启动Java时，main函数的第一个参数将会是UDP的端口号，通过BackendAPI启动Java的过程中，
此端口号必须提供。

另外，本项目的运行路径，不应该存在中文和特殊符号，否则Java应用可能无法正常载入。