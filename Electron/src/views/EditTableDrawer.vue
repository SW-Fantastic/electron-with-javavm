<template>
  <Drawer v-model="visible" width="60" :title="title" :mask-closable="false"  @on-close="closed">
    <div @click="resetEditing">
      <TableContextMenu ref="ctxMenu" v-model="contextMenuTarget" :tableMenu="tableMenu" />
      <Form :model="tableData" label-position="right" :label-width="80">
        <FormItem label="名称：" prop="name">
          <Input v-model="tableData.name" />
        </FormItem>
      </Form>
      <Divider></Divider>
      <div style="padding-bottom: 12px;display: flex;flex-direction: row;justify-content: center;align-items: flex-start;">
        <Input v-model="editingColName">
          <template #append>
            <Select style="width: 120px;" v-model="editingColType">
              <Option v-for="(elem, key) in columnTypes" :value="elem.id" >{{ elem.title }}</Option>
            </Select>
          </template>
        </Input>
        <Button type="primary" style="margin-left: 8px;" @click="addColumn">添加列</Button>
      </div>
      <Table @on-drag-drop="onSortDragDrop" draggable :height="tableHeight" :columns="tableColumns" :data="tableData.columns" size="small" @on-contextmenu="showTableContextMenu">
          <template #name="{ row,index }">
            <Input @click.stop v-model="itemEditingName" v-if="itemEditingIndex === index" />
            <span v-else>{{ row.name }}</span>
          </template>
          <template #type="{ row,index }">
            <Select @click.stop v-model="itemEditingType" v-if="itemEditingIndex === index">
              <Option v-for="(elem, key) in columnTypes" :value="elem.id" >{{ elem.title }}</Option>
            </Select>
            <span v-else>{{ getTypeWithId(row.type.id).name }}</span>
          </template>
      </Table>
      <div style="padding: 24px;">
        <Button type="primary" long @click="submitTable">完成</Button>
      </div>
    </div>
  </Drawer>
</template>
<script>

import { TableRequest } from "@/services/TableRequest";
import { TableService } from "@/services/TableService";
import { ColumnTypeService } from '@/services/ColumnTypeService';

import TableContextMenu from "./components/TableContextMenu";

export default {
    name: "EditTableDrawer",
    props: {
        type: {
            retquired: true
        }
    },
    components: {
      TableContextMenu
    },
    emits: ["closed"],
    data() {
        const self = this;
        return {
            title: "",
            contextMenuTarget: null,

            // UI - 表格编辑 - 正在编辑中的列数据
            itemEditingName: "",
            itemEditingType: null,
            itemEditingIndex: -1,
            // Network - 网络服务
            services: {
                columnTypeService: new ColumnTypeService(),
                tableService: new TableService(),
            },
            // UI - 本Drawer的显示状态
            visible: false,
            // UI - 动态计算Table高度
            resizeListener: null,
            tableHeight: window.visualViewport.height - 320,
            // UI - 表格编辑 - 列编辑
            tableColumns: [{
                    title: "名称",
                    slot: "name"
                }, {
                    title: "类型",
                    slot: "type"
                }],
            // 表格 - 列类型
            columnTypes: [],
            // 表格 - 数据模型
            tableData: {
                name: "",
                columns: []
            },
            // 表格 - 列编辑功能
            editingColName: "",
            editingColType: null,
            // 更新表 = tableId, 创建表 = -1 功能未完成。
            modifyTableId: -1
        };
    },
    computed: {
        tableMenu() {
            return [{
              label: "修改",
              tip: "修改完毕请点击完成",
              click: this.menuColumnEdit
            }, {
              label: "删除",
              tip: "无法撤销",
              click: this.menuDelete
            }];
        }
    },
    mounted() {
        const self = this;
        this.resizeListener = window.addEventListener("resize", e => {
            const currHeight = window.visualViewport.height;
            self.tableHeight = currHeight - 320;
        });
    },
    unmounted() {
        window.removeEventListener("resize", this.resizeListener);
    },
    methods: {
        menuDelete() {
          if (this.contextMenuTarget === null) {
            return;
          }
          let target = -1;
          for(let idx = 0; idx < this.tableData.columns.length; idx ++) {
            if(this.contextMenuTarget.id === this.tableData.columns[idx].id) {
                target = idx;
                break;
            }
          }
          this.tableData.columns.splice(target,1);
        },
        menuColumnEdit() {
            if (this.contextMenuTarget === null) {
                return;
            }
            const columns = this.tableData.columns;
            for (let idx = 0; idx < columns.length; idx++) {
                if (columns[idx].id === this.contextMenuTarget.id) {
                    this.itemEditingName = columns[idx].name;
                    this.itemEditingType = columns[idx].type.id;
                    this.itemEditingIndex = idx;
                    break;
                }
            }
        },
        addColumn() {
            if (this.editingColName === "") {
                this.$Message.error("请输入列的名称。");
                return;
            }
            if (this.editingColType === null) {
                this.$Message.error("请为列选择一个类型。");
                return;
            }
            let type = this.getTypeWithId(this.editingColType);
            let order = this.tableData.columns.length;
            this.tableData.columns.push({
                name: this.editingColName,
                type: {
                    name: type.name,
                    id: type.id,
                },
                orderIndex: order
            });
            this.editingColName = "";
            this.editingColType = null;
        },
        showTableContextMenu(row, event, pos) {
          this.$refs.ctxMenu.onCtxMenu(row,event,pos);
        },
        getTypeWithId(typeId) {
            for (const typeItem of this.columnTypes) {
                if (typeItem.id == typeId) {
                    return typeItem;
                }
            }
            return null;
        },
        resetEditing() {
            if (this.itemEditingIndex !== -1) {
                let type = this.getTypeWithId(this.itemEditingType);
                const item = this.tableData.columns[this.itemEditingIndex];
                item.name = this.itemEditingName;
                item.type = {
                    name: type.name,
                    id: type.id
                };
            }
            this.itemEditingIndex = -1;
            this.itemEditingType = null;
            this.itemEditingName = "";
        },
        show(table) {
            if (this.visible === true) {
                return;
            }
            let colTypeService = this.services.columnTypeService;
            colTypeService.getColumnTypes().then(data => {
                this.columnTypes = data;
                if(table) {
                    this.title = "修改表格"
                    this.modifyTableId = table.id;
                    this.tableData.name = table.tableName;
                    this.tableData.columns = [];
                    for(const item of table.columns) {
                        this.tableData.columns.push({
                            name: item.name,
                            id: item.id,
                            type: {
                                id: item.type.id,
                                name: item.type.name
                            },
                            orderIndex: item.orderIndex
                        });
                    }
                    this.tableData.columns.sort((a,b) => a.orderIndex - b.orderIndex);
                } else {
                    this.tableData.columns = [];
                    this.tableData.name = "";
                    this.modifyTableId = -1;
                    this.title = "创建表格"
                }
                this.visible = false;
                this.$nextTick(() => {
                    this.visible = true;
                });
            });
        },
        submitTable() {
            if (this.tableData.name === "") {
                this.$Message.error("表格名称不能为空。");
                return;
            }
            if (this.tableData.columns.length === 0) {
                this.$Message.error("表中至少包含一列。");
                return;
            }
            const request = new TableRequest();
            const cols = this.tableData.columns;
            for (const col of cols) {
                if(this.modifyTableId < 0) {
                    request.addColumn(
                        col.name, 
                        col.type.id,
                        this.tableData.columns.indexOf(col)
                    );
                } else {
                    request.updateColumn(
                        col.id,
                        col.name,
                        col.type.id,
                        this.tableData.columns.indexOf(col)
                    );
                }
            }
            request.name(this.tableData.name);
            const service = this.services.tableService;
            if(this.modifyTableId < 0) {
                service.createTable(this.type.id, request).then(resp => {
                    if (resp.status === "Success") {
                        this.visible = false;
                        this.tableData = {
                            name: "",
                            columns: []
                        };
                        this.$emit("closed");
                    }
                    else {
                        this.$Message.error("创建失败！");
                    }
                });
            } else {
                request.updateTable(this.modifyTableId);
                service.updateTable(request).then(resp => {
                    if (resp.status === "Success") {
                        this.visible = false;
                        this.tableData = {
                            name: "",
                            columns: []
                        };
                        this.modifyTableId = -1;
                        this.$emit("closed");
                    }
                    else {
                        this.$Message.error("修改失败！");
                    }
                })
            }
        },
        onSortDragDrop(indexA, indexB) {
            const itemA = this.tableData.columns[indexA];
            const itemB = this.tableData.columns[indexB];
            this.tableData.columns.splice(indexA,1,itemB);
            this.tableData.columns.splice(indexB,1,itemA);
        },
        closed() {
            this.$emit("closed");
        }
    }
    
}
</script>