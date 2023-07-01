/**
 * 提供Java应用的信息供Electron使用。
 */
class Backend {

    constructor() {
        // Session Storage中存储的后端端口号
        this.ServerPortKey = "springboot-port";
    }

    /**
     * 读取后端的Http端口号
     * @returns 
     */
    getServerPort() {
        const val = sessionStorage.getItem(this.ServerPortKey);
        return val ? val : null;
    }

    /**
     * 修改后端的Http端口号
     * @param Number port 
     */
    setServerPort(port) {
        sessionStorage.setItem(this.ServerPortKey,port);
    }

}

export const BackendSetting = new Backend();