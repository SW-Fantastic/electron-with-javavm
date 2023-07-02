<template>
    <div style="padding-right: 12px;width: 100%;">
        <EditDataTypeModal ref="dtEditModal"/>
        <EditTableDrawer :type="typeNode" ref="tableEditor" @closed="onDrawerHide" />
        <TableDrawer ref="tableDrawer" />
        <TableContextMenu ref="ctxMenu" v-model="contextMenuTarget" :tableMenu="tableMenu" ></TableContextMenu>
        <Card :padding="8">
            <div style="display: flex;flex-direction: row;justify-content: space-between;align-items: center;">
                <div>
                    <Button type="primary" @click="onShowTableDrawer" style="margin-right: 8px;">创建表格</Button>
                    <Button type="primary" @click="onEditDataType">数据类型...</Button>
                </div>
                <div>
                    <Input placeholder="输入标题以查找内容" v-model="searchText" />
                </div>
            </div>
        </Card>
        <Table :columns="columns" 
            :border="true" 
            :height="tableHeight" 
            :data="tableItems" 
            size="small" 
            @on-row-dblclick="showTable"
            @on-contextmenu="showTableContextMenu">
        </Table>
        <div style="display: flex;flex-direction: row;justify-content: flex-end;align-items: center;padding: 12px;">
            <Page :page-size="pageSize" :total="totalElems" v-model="currentPage" @on-change="pageChanged"></Page>
        </div>
    </div>
</template>
<script>

import EditTableDrawer from './EditTableDrawer';
import TableDrawer from './TableDrawer';
import EditDataTypeModal from './EditDataTypeModal';
import TableContextMenu from './components/TableContextMenu';

import { TableService  } from '@/services/TableService';

export default {
    name : "DescTableView",
    props: {
        typeNode: {
            required: true,
        },
    },
    components: {
        EditDataTypeModal,
        EditTableDrawer,
        TableContextMenu,
        TableDrawer
    },
    data() {
        return {
            contextMenuTarget: null,
            tableHeight:  window.visualViewport.height - 186,
            resizeListener: null,
            searchText: "",
            services: {
                tableService: new TableService()
            },
            columns: [{
                title: "ID",
                key: "id"
            }, {
                title: "名称",
                key: "tableName",
            }, {
                title: "描述",
                key: "desc"
            }],
            tableItems: [],
            totalElems: 0,
            pageSize: 20,
            currentPage: 1,
        }
    },
    mounted() {
        const self = this;
        this.resizeListener = window.addEventListener("resize", e => {
            const currHeight = window.visualViewport.height;
            self.tableHeight = currHeight - 186;
        })
        this.loadPage(this.currentPage - 1);
    },
    unmounted() {
        window.removeEventListener("resize",this.resizeListener);
    },
    computed: {
        tableMenu() {
            return [{
                label: "打开...",
                click: this.onMenuOpen
            },{
                label: "修改",
                tip: "修改表结构",
                click: this.onMenuEdit
            }, {
                label: "删除",
                tip: "不可撤销",
                click: this.onMenuDelete
            }]
        }
    },
    methods : {
        async refreshItem(selected) {
            const tableService = this.services.tableService;
            const resp = await tableService.getTable(selected.id); 
            const idx = this.tableItems.indexOf(selected);
            if(!resp.status === "Success") {
                this.tableItems.splice(idx,1);
            } else {
                this.tableItems.splice(idx,1,resp.data);                    
            }
            return resp.data;
        },
        onMenuOpen() {
            if(this.contextMenuTarget === null) {
                return;
            }
            this.refreshItem(this.contextMenuTarget).then(data => {
                this.contextMenuTarget = data;
                this.$refs.tableDrawer.show(this.contextMenuTarget);
            })
        },
        onMenuEdit() {
            if(this.contextMenuTarget === null) {
                return;
            }
            this.refreshItem(this.contextMenuTarget).then(data => {
                this.contextMenuTarget = data;
                this.$refs.tableEditor.show(this.contextMenuTarget);
            })
        },
        onMenuDelete() {

        },
        loadPage(no) {
            const tableService = this.services.tableService;
            return tableService.loadTablesByPage(this.typeNode.id, no).then(resp => {
                if(resp.status === "Success") {
                    const data = resp.data;
                    
                    this.totalElems = data.totalElements;
                    this.pageSize = data.size;

                    this.tableItems = data.content;
                }
            });
        },
        onEditDataType() {
            this.$refs.dtEditModal.show();
        },
        onShowTableDrawer() {
            this.$refs.tableEditor.show();
        },
        showTableContextMenu(row, event, pos) {
          this.$refs.ctxMenu.onCtxMenu(row,event,pos);
        },
        onDrawerHide() {
            this.loadPage(this.currentPage - 1);
        },
        showTable(row) {
            this.refreshItem(row).then(data => {
                this.$refs.tableDrawer.show(data);
            })
        },
        pageChanged(num) {
            this.loadPage(num - 1);
        }
    }
}
</script>
<style scoped>
</style>