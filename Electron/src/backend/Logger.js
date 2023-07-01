/**
 * 提供一个Logger，方便打印来自Java应用的日志。
 * 
 * Java应用将会使用UDP消息的形式发送日志到Electron，
 * 并且通过控制台打印日志，日志之外的System.out/System.err等
 * 将不会出现在Electron的控制台中。
 * 
 */
export class Logger {

    constructor(name) {
        this.name = name;
    }

    info(text) {
        console.log("%c[Info][" + this.name + "] :" + text, "color: #83cbac");
    }

    debug(text) {
        console.log("%c[Debug][" + this.name + "] :" + text, "color: #999");
    }

    warn(text) {
        console.log("%c[Warn][" + this.name + "] :" + text, "color: #e2c027");
    }

    error(text) {
        console.log("%c[Error][" + this.name + "] :" + text, "color: #c02c38")
    }

}

