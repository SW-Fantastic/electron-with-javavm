# Electron + Java VM

## 简介

本项目的是一个演示项目，它展示了如何将Electron和一个Java后端集成在一起，它的原理是通过Node-FFI-NAPI调用Native层
，通过它启动一个Java虚拟机并且加载Java应用。

这里是本项目的Electron部分

> 另外，本项目的运行路径，不应该存在中文和特殊符号，否则Java应用可能无法正常载入。

为了能方便的使用Vue，所以本项目采用了一个Vue的集成项目（Vue-Electron-Builder），此项目对Nodejs有版本要求，需要使用Nodejs 16版本，本项目采用yarn构建，虽然它同样可以使用NPM，
但作者更建议使用Yarn，另外，Vue版本为Vue3。

本项目中，`views`,`store`,`services`,`router`,`assets`均为业务逻辑。

与后端交互的部分非常简单，它只是一个启动器，位于`backend`目录中。

同样需要关注的还有`background.js`，它是启动Electron功能的部分，以及
`preload.js`，它是Nodejs和Electron的桥梁，为前端提供了`Backend`相关的接口。

项目在关键的位置有比较详细的注释，因此这里就不在过多解释了。

## 打包

如果需要打包为可执行文件，可以直接使用“yarn electron:build“或者”npm run electron:build”
但是打包完成后，记得复制Backend和BackendAPI的动态库到打包后的目录中。

关于这部分，Release中将会存在一个打包完毕的Demo可供参考。