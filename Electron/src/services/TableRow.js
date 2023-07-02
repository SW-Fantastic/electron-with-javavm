export class TableRow {

    constructor(rowId) {
        this.rowId = rowId;
        this.rowProps = [];
    }

    addRowProperty(entry) {
        const columnId = entry.column.id;
        if(this[columnId]) {
            this[columnId] = entry.data;
            return;
        }
        this["CID_" + columnId] = entry.data;
        Object.defineProperty(this,"CID_" + columnId,{
            get() {
                return entry.data;
            },
            set(val) {
                entry.data = val;
                this["CID_" + columnId] = val;
            }
        });
        this.rowProps.push(entry);
    }

    getDataModel() {
        let model = {};
        for(const col of this.rowProps) {
            Object.defineProperty(model,col.column.id, {
                get() {
                    return col.data;
                },
                set(val) {
                    col.data = val;
                    this["CID_" + col.column.id] = val;
                }
            })
        }
        return model;
    }

    getColumns() {
        let cols = [];
        for(const col of this.rowProps) {
            cols.push(col.column);
        }
        return cols;
    }

}