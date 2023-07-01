import { NetRequest,Get,Post } from "./HttpAnnotation";
import { TableRequest } from "./TableRequest";

/**
 * 这是一个Ajax的服务类，
 * 它的方法通过装饰器的修饰，被替换为一个一个的对后端的Fetch操作，
 * 这种结构在Java的开发中非常常见。
 * 
 * 详情请首先参阅“HttpAnnotation”的源码。
 * 
 */
export class TableService {

    @Post("/tables/create/${typeId}")
    createTable(typeId, tableRequest) {
        if(!tableRequest instanceof TableRequest) {
            throw Error("please provide a TableRequest object.");
        }
        return new NetRequest({
            typeId: typeId
        }, tableRequest);
    }

    @Post("/tables/update")
    updateTable(tableRequest) {
        if(!tableRequest instanceof TableRequest) {
            throw Error("please provide a TableRequest object.");
        }
        return new NetRequest({}, tableRequest);
    }

    @Get("/tables/${tableId}/load")
    getTable(id) {
        return new NetRequest({
            tableId: id
        })
    }

    @Get("/tables/load/${typeId}/page/${pageNo}")
    loadTablesByPage(typeId, pageNo) {
        return new NetRequest({
            typeId: typeId,
            pageNo: pageNo
        });
    }

}