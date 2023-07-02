<template>
    <Drawer :title="table.tableName" v-model="visible" placement="bottom" height="80">
        <div>
            <TableContextMenu ref="ctxMenu" v-model="contextMenuTarget" :tableMenu="tableMenu" />
            <TableStructureModal @refresh="refreshDrawer" :table="table" ref="editModal"/>
            <div style="display: flex;flex-direction: row;justify-content: space-between;align-items: center;padding-bottom: 12px;">
                <div>
                    <Button type="primary" @click="addItem">添加...</Button>
                </div>
                <div>
                    <Page :total="totalElems" :page-size="pageSize" v-model="currentPage" @on-change="pageChanged"></Page>
                </div>
            </div>
            <Table size="small" 
                @on-row-dblclick="rowClicked" 
                :columns="tableColumns" 
                :height="tableHeight" 
                :data="tableRows" 
                @on-contextmenu="showTableContextMenu">
            </Table>
        </div>
    </Drawer>
</template>
<script>

import TableContextMenu from './components/TableContextMenu';
import TableStructureModal from './TableStructureModal';

import { Editors } from "@/views/editors/Editors"

import { TableRow } from "@/services/TableRow";
import { TableService  } from '@/services/TableService';

export default {
    name: "TableDrawer",
    components: {
        TableStructureModal,
        TableContextMenu
    },
    data() {
        return {
            services: {
                tableService: new TableService(),
            },
            contextMenuTarget: null,
            currentPage: 1,
            totalElems: 0,
            pageSize: 20,

            visible: false,
            tableHeight:  window.visualViewport.height - 340,
            resizeListener: null,
            tableColumns: [],
            tableRows: [],
            table: {
                tableName: ""
            }
        }
    },
    computed : {
        tableMenu() {
            return [{
                label: "打开",
                tip: "浏览或修改此记录",
                click: this.onMenuOpen
            }, {
                label: "删除",
                tip: "删除此记录"
            }]
        }
    },
    mounted() {
        let self = this;
        this.resizeListener = window.addEventListener("resize", e => {
            const currHeight = window.visualViewport.height;
            self.tableHeight = currHeight - 340;
        })
    },
    unmounted() {
        window.removeEventListener("resize",this.resizeListener);
    },
    methods: {
        onMenuOpen() {
            if (this.contextMenuTarget === null) {
                return;
            }
            let index = -1;
            for(let idx = 0; idx < this.tableRows.length; idx ++) {
                if(this.tableRows[idx].rowId === this.contextMenuTarget.rowId) {
                    index = idx;
                    break;
                }
            }
            if(index === -1) {
                return;
            }
            this.$refs.editModal.showEdit(this.tableRows[index],this.table.columns);
        },
        addItem() {
            this.$refs.editModal.show(this.table.columns);
        },
        show(table) {
            if(!table || this.visible) {
                return
            }
            const editorTypes = Editors;
            this.table = table;
            this.tableColumns = [];
            for(const col of table.columns) {
                this.tableColumns.push({
                    title: col.name,
                    key: "CID_" + col.id,
                    orderIndex: col.orderIndex,
                    render: editorTypes[col.type.editorId].tableRowRender,
                    ellipsis: true
                });
            }
            this.tableColumns.sort((a,b) => a.orderIndex - b.orderIndex);
            this.loadPage(this.currentPage - 1).then(() => {
                this.visible = true;
            })
        },
        loadPage(pageNo) {
            const tableService = this.services.tableService;
            this.tableRows = [];
            return tableService.loadTableRowsByPage(this.table.id, pageNo).then(resp => {
                if(resp.status === "Success") {
                    resp = resp.data;
                    for(const item of resp.content) {
                        let tableRow = new TableRow(item.id);
                        for(const ent of item.rowEntries) {
                            tableRow.addRowProperty(ent);
                        }
                        this.tableRows.push(tableRow);
                    }
                    this.totalElems = resp.totalElements;
                    this.pageSize = resp.size;
                }
            })
        },
        rowClicked(row, index) {
            this.$refs.editModal.showEdit(this.tableRows[index],this.table.columns);
        },
        showTableContextMenu(row, event, pos) {
          this.$refs.ctxMenu.onCtxMenu(row,event,pos);
        },
        refreshDrawer() {
            this.loadPage(this.currentPage - 1);
        },
        pageChanged(num) {
            this.loadPage(num - 1);
        }
        
    }
}
</script>