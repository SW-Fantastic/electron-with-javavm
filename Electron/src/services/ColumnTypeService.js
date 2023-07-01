import { NetRequest,Get,Post } from "./HttpAnnotation";

/**
 * 这是一个Ajax的服务类，
 * 它的方法通过装饰器的修饰，被替换为一个一个的对后端的Fetch操作，
 * 这种结构在Java的开发中非常常见。
 * 
 * 详情请首先参阅“HttpAnnotation”的源码。
 * 
 */
export class ColumnTypeService {

    @Get("/columns/all")
    doGetColumnTypes() {
        return new NetRequest(null,null);
    }

    @Get("/columns/load/${id}")
    doGetColumnType(typeId) {
        return new NetRequest({
            id: typeId
        })
    }

    @Post("/columns/update/${id}")
    updateColumnType(id, name, value,editor) {
        return new NetRequest({
            id: id
        }, {
            name: name,
            descriptor: value,
            editorId: editor
        });
    }

    @Post("/columns/trash/${id}")
    deleteColumnType(id) {
        return new NetRequest({
            id: id
        }, null);
    }

    @Post("/columns/add")
    addColumnType(name, value,editorId) {
        return new NetRequest(null, {
            name: name,
            descriptor: value,
            editorId: editorId
        });
    }

    async getFieldType(typeId) {
        const resp = await this.doGetColumnType(typeId);
        if(resp.status !== "Success") {
            return null;
        }
        Object.defineProperty(resp.data, "title", {
            get() {
                return resp.data.folderName;
            },
            set(v) {
                resp.data.folderName = v;
            }
        });
        return resp.data;
    }

    async getColumnTypes() {
        const resp = await this.doGetColumnTypes();
        if(resp.status !== "Success") {
            return [];
        }
        const mapper = (item) => {
            Object.defineProperty(item, "title", {
                get() {
                    return item.name;
                },
                set(v) {
                    item.name = v;
                }
            });
            if(item.children && item.children.length > 0) {
                item.children.forEach(mapper);
            }
        }
        for(let elem of resp.data) {
            mapper(elem);
        }
        return resp.data;
    }

}