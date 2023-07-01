import { BackendSetting } from "./Backend"

/**
 * 
 * 模板展开函数。
 * 
 * ES6中支持模板字符串，但是模板字符串严重依赖它的上下文，
 * 本方法允许使用普通的字符串，并且在字符串内部嵌入${something}表达式，
 * 通过提供的Object的属性（例如：something）替换表达式的内容，
 * 达成一种模板渲染的效果。
 * 
 * 例如：字符串为 "user/${userId}/delete" 并且提供JSON对象 { userId：12 }，
 * 那么渲染的结果将会是：user/12/delete
 * 
 * @param 字符串模板 tempStr 
 * @param 字符串模板的参数 params 
 * @returns 
 */
function asString(tempStr, params) {
    let keys = params ? Object.getOwnPropertyNames(params) : [];
    let vars = "";
    if(keys.length > 0) {
        for(const key of keys) {
            vars += `let ${key} = ${params[key]};\n`;
        }
    }
    let func = new Function(vars + "return `" + tempStr + "`")
    return func.apply(params)
}

/**
 * Ajax的请求的参数对象
 */
export class NetRequest {

    /**
     * 构建一个Ajax请求参数
     * @param {} queryParam 一个JSON对象，主要用于替换URL中的模板 
     * @param {} data  一个JSON对象，在Post和Put的时候，将会被JSON.stringify作为请求的body
     */
    constructor(queryParam, data) {
        this.queryParam = queryParam || {};
        this.data = data || {};
    }

}

/**
 * 这是一个装饰器方法，或者说一个注解，
 * 它可以被标注在任何其他函数（类的方法）的上方，
 * 并且提供如下效果：
 * 
 * 被标注的方法必须返回如上定义的NetRequest对象，
 * 你需要通过new操作创建一个NetRequest，此Request包含的参数
 * QueryParam将会用于替换URL中的模板部分，当然，如果URL没有需要替换的模板，
 * 你的NetRequest可以不包含任何内容。（参考asString函数）
 * 
 * 被标注的方法将会返回一个Promise，它的Then方法的参数将会是对指定URL请求的
 * Response。
 * 
 * @param {*} url Get请求的目标URL。
 * @returns 
 */
export function Get(url) {
    return (target,name,desc) => {
        let method = desc.value;
        desc.value = function () {
            const param = method.apply(this,arguments);
            if(!param instanceof NetRequest) {
                throw Error("return value must be a NetRequest instance！");
            }
            return fetch(
                asString("http://localhost:" + BackendSetting.getServerPort() + url, param.queryParam)
            ).then(
                resp => resp.json()
            )
        };
    }
}

/**
 * 这是一个装饰器方法，或者说一个注解，
 * 它可以被标注在任何其他函数（类的方法）的上方，
 * 并且提供如下效果：
 * 
 * 被标注的方法必须返回如上定义的NetRequest对象，
 * 你需要通过new操作创建一个NetRequest，此Request包含的参数
 * QueryParam将会用于替换URL中的模板部分，当然，如果URL没有需要替换的模板，
 * 你的NetRequest可以不包含任何内容。（参考asString函数）
 * 
 * 你的NetRequest对象在QueryParam之外，还可以包含一个Data参数，它将会作为
 * Post请求的Body发送到指定的URL。
 * 
 * 被标注的方法将会返回一个Promise，它的Then方法的参数将会是对指定URL请求的
 * Response。
 * 
 * @param {*} url Get请求的目标URL。
 * @returns 
 */
export function Post(url) {
    return (target,name,desc) => {
        let method = desc.value;
        desc.value = function () {
            const param = method.apply(this,arguments);
            if(!param instanceof NetRequest) {
                throw Error("return value must be a NetRequest instance！");
            }
            return fetch(
                asString("http://localhost:" + BackendSetting.getServerPort() + url, param.queryParam),
                {
                    method: "POST",
                    body: JSON.stringify(param.data),
                    headers: {
                        "Content-Type": "application/json"
                    }
                }
            ).then(
                resp => resp.json()
            )
        };
    }
}