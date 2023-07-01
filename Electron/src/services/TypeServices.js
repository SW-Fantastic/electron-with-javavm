import { NetRequest,Get,Post } from "./HttpAnnotation";

/**
 * 这是一个Ajax的服务类，
 * 它的方法通过装饰器的修饰，被替换为一个一个的对后端的Fetch操作，
 * 这种结构在Java的开发中非常常见。
 * 
 * 详情请首先参阅“HttpAnnotation”的源码。
 * 
 */
export class TypeServices {

    @Post("/folders/createRoot")
    createRootType(typeName) {
        return new NetRequest(null, {
            folderName: typeName
        })
    }

    @Post("/folders/updateFolder/${folderId}")
    updateType(folderId, newName) {
        return new NetRequest({
            folderId: folderId
        }, {
            folderName: newName
        });
    }

    @Post("/folders/create/${folderId}")
    createSubType(parentId, typeName) {
        return new NetRequest({
            folderId: parentId
        }, {
            folderName : typeName
        });
    }

    @Post("/folders/deleteFolder/${folderId}")
    trashType(typeId) {
        return new NetRequest({
            folderId : typeId
        }, null);
    }

    @Get("/folders/roots")
    doGetRoots() {
        return new NetRequest(null,null);
    }

    @Get("/folders/roots/${folderId}")
    doGetChildren(parentId) {
        return new NetRequest({
            folderId: parentId
        },null);
    }

    async getChildren(parentNode) {
        const resp = await this.doGetChildren(parentNode.id);
        if(resp.status !== "Success") {
            return [];
        }
        const mapper = (parent, item) => {
            Object.defineProperty(item, "title", {
                get() {
                    return item.folderName;
                },
                set(v) {
                    item.folderName = v;
                }
            });
            item.parent = parent;
            if(item.children && item.children.length > 0) {
                item.children.forEach(i => mapper(i,item));
            }
        }
        for(let elem of resp.data) {
            mapper(parentNode, elem);
        }
        return resp.data;
    }

    async getRootTypes() {
        const resp = await this.doGetRoots();
        if(resp.status !== "Success") {
            return [];
        }
        const mapper = (parent, item) => {

            Object.defineProperty(item, "title", {
                get() {
                    return item.folderName;
                },
                set(v) {
                    item.folderName = v;
                }
            });

            item.parent = parent;
            if(item.children && item.children.length > 0) {
                item.children.forEach(i => mapper(item,i));
            }
        }
        for(let elem of resp.data) {
            mapper(null,elem);
        }
        return resp.data;
    }
}