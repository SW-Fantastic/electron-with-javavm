/**
 * 对于格式复杂的数据，
 * 
 * 建议使用自定义的Class来保持前端和后端数据结构的一致，
 * 这里的TableRequest就是一个例子，本类维护了一个和后端DTO
 * 一致的数据结构，他能方便的把前端零散的数据收集并且为请求
 * 做好准备。
 */
export class TableRequest {

    constructor() {
        this.tableName = null;
        this.columns= [];
    }

    name(name) {
        this.tableName = name;
        return this;
    }

    updateTable(tableId) {
        this.tableId = tableId;
        return this;
    }

    updateColumn(colId, name, typeId,order) {
        if(!colId) {
            colId = -1;
        }
        let isUpdated = false;
        for(const col of this.columns) {
            if(col.id === colId || col.name === name) {
                col.columnId = colId;
                col.columnName = name;
                col.columnTypeId = typeId;
                col.orderIndex = order;
                isUpdated = true;
                break;
            }
        }
        if(!isUpdated) {
            this.columns.push({
                columnId: colId ? colId : -1,
                columnName: name,
                columnTypeId: typeId,
                orderIndex: order
            })
        }
        return this;
    }

    addColumn(name, typeId,order) {
        let isUpdated = false;
        for(const col of this.columns) {
            if(col.name === name) {
                col.columnTypeId = typeId;
                col.orderIndex = order;
                isUpdated = true;
            }
        }
        if(!isUpdated) {
            this.columns.push({
                columnName: name,
                columnTypeId: typeId,
                orderIndex: order
            })
        }
        return this;
    }

}