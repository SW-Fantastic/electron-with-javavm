import Datagram from "dgram";

/**
 * 提供一个UDP服务，监听一个UDP端口。
 * 
 * 端口在初始化时应当随机分配，初始化完毕后应当存储端口号，方便
 * 在页面刷新后指定同一个端口号与Java应用建立通信。
 * 
 * UDP消息是一个JSON，它包含type和data两个字段，
 * 其中type为String，data通常比较随意。
 */
export class ServerEvents {

    /**
     * 创建事件监听的UDP服务。
     * 
     * @param Number port UDP端口号，不指定将会随机一个。
     */
    constructor(port) {

        this.handles = {};

        this.socket = Datagram.createSocket("udp4");
        this.socket.on("message", (message,remote) => {
            const msg = JSON.parse(message.toString());
            if(this.handles[msg.type]) {
                for(const handle of this.handles[msg.type]) {
                    handle(msg.data);
                }
            }
        });
        this.port = ( port !== null && port > 0 ? port : Math.floor(Math.random() * (10000 - 4000) + 4000) );
        this.socket.bind(this.port,"127.0.0.1");
        
    }

    listen(eventType, callback) {
        if(!this.handles[eventType]) {
            this.handles[eventType] = [callback];
        } else {
            this.handles[eventType].push(callback);
        }
    }

}
