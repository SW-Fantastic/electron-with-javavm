import Ffi from "ffi-napi"
import Ref from "ref-napi"

import Process from "process"
import Path from "path"
import Fs from "fs"

import { ServerEvents } from "./ServerEvents"

/**
 * 由于后端使用的是SpringBoot，所以本类是SpringServer
 * 
 * 如果你不用SpringBoot，可以改名。
 */
export class SpringServer {

    /**
     * 创建一个SpringServer对象
     * @param Boolean enableUDP 使用启用UDP监听 
     * @param Number udpPort UDP的端口号
     */
    constructor(enableUDP, udpPort) {
        if(enableUDP) {
            this.serverDispatcher = new ServerEvents(udpPort | null);
            this.udpPort = this.serverDispatcher.port;
        } else {
            this.serverDispatcher = null;
        }
        this.native = Ffi.Library(Path.join(Process.cwd(),"BackendAPI.dll"),{
            initialize:["int", ["int","string","string","string","string"]],
            destroy:["bool",["void"]],
        })
    }

    /**
     * 枚举指定路径的jar文件，提供一个包含内部所有jar文件的绝对路径的数组。
     * 
     * @param String parent 运行所需要的jar文件的相对路径 
     * @returns jar文件的绝对路径数组
     */
    getClassPath(parent) {
        const files = Fs.readdirSync(parent);
        const jars = [];
        for(const item of files) {
            const abs = Path.join(parent,item);
            if(Fs.statSync(abs).isDirectory()) {
                const rst = this.getClassPath(abs);
                for(const jarPath of rst) {
                    jars.push(jarPath);
                }
            } else if(item.toLowerCase().endsWith("jar")) {
                jars.push(abs);
            }
        }
        return jars;
    }

    /**
     * 初始化JavaVM，启动后端应用
     * @returns 
     */
    initializeBackendVM() {

        if(!this.serverDispatcher) {
            return;
        }
        // JVM的VMOptions，jvm的内存调整什么的可能需要。
        const vmOptions = [
           "-javaagent:" + Path.join(Process.cwd(),"agentDemo-1.0-SNAPSHOT.jar")
        ];
        // 枚举jar文件
        const rst = this.getClassPath(Path.join(Process.cwd(), "backend/service"));
        // 拼接classpath
        const cp = rst.join(";");
        return this.native.initialize(
            // UDP端口号
            this.serverDispatcher.port,
            // JavaRuntime的二进制文件路径，一般没有必要修改。
            Path.join(Process.cwd(),"backend/bin"),
            // 类路径
            cp,
            // VMOptions
            vmOptions.join("\n"),
            // Main Class
            "org/springframework/boot/loader/launch/JarLauncher"
        )
    }

    destroyBackendVM() {
        return this.native.destroy(null)
    }

}