import { resolveComponent } from "vue"
import moment from "moment/moment";

class TableEditorRegistry {

    constructor(name, id) {
        this.name = name,
        this.id = id;
    }

    tableRowRender(h,param) {
        return h('span',null,param.row[param.column.key])
    }

    asString(rowData) {
        return rowData;
    }
}


class TableConnectedSelectRegistry extends TableEditorRegistry {

    constructor(name, id) {
        super(name,id);
    }

    tableRowRender(h,param) {
        const data = param.row[param.column.key];
        if(data && data !== "") {
            return h(resolveComponent("Tag"),{
                color: "green",
                size: "medium"
            }, {
                default() {
                    return data;
                }
            })
        }
        return h('span',null,'');
    }

}

class TableMutipleSelectRegistry extends TableEditorRegistry {

    constructor(name,id) {
        super(name,id);
    }

    tableRowRender(h,param) {
        const data = param.row[param.column.key];
        if(data && data !== "") {
            const items = data.split("/");
            const vds = [];
            for(const item of items) {
                vds.push(h(resolveComponent("Tag"), {
                    color: "volcano",
                    size: "medium"
                }, {
                    default() {
                        return item;
                    }
                }))
            }
            return h("div",vds);
        }
        return h('span',null, "")
    }

}

class TableDateEditorRegistry extends TableEditorRegistry {

    constructor(name,id) {
        super(name,id);
    }

    tableRowRender(h,param) {
        const timestrap = param.row[param.column.key];
        if(timestrap && timestrap !== "") {
            const date = moment(timestrap);
            return h(resolveComponent("Tag"),{
                color: "cyan",
                size: "medium"
            }, {
                default() {
                    return date.format("yyyy / MM / DD");
                }
            })
        }
        return h('span',null, "")
    }

    asString(timestrap) {
        if(timestrap && timestrap !== "") {
            return date.format("yyyy / MM / DD");
        }
        return "";
    }

}

export const Editors = {
    TextInputEditor: new TableEditorRegistry("文本框", "TextInputEditor"),
    CheckBoxesEditor: new TableMutipleSelectRegistry("多选框", "CheckBoxesEditor"),
    RadioBoxesEditor: new TableEditorRegistry("单项选择", "RadioBoxesEditor"),
    DateEditor: new TableDateEditorRegistry("日期选择", "DateEditor"),
    SelectConnectedEditor: new TableConnectedSelectRegistry("数据关联","SelectConnectedEditor")
}