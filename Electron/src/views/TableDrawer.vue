<template>
    <Drawer :title="table.tableName" v-model="visible" placement="bottom" height="80">
        <div>
            <TableStructureModal :table="table" ref="editModal"/>
            <div style="display: flex;flex-direction: row;justify-content: space-between;align-items: center;padding-bottom: 12px;">
                <div>
                    <Button type="primary" @click="addItem">添加...</Button>
                </div>
                <div>
                    <Page :total="totalElems" v-model="currentPage"></Page>
                </div>
            </div>
            <Table @on-row-dblclick="rowClicked" :columns="tableColumns" :height="tableHeight" :data="tableRows">
            </Table>
        </div>
    </Drawer>
</template>
<script>

import TableStructureModal from './TableStructureModal';
import { TableRow } from "@/services/TableRow";
import { TableService  } from '@/services/TableService';

export default {
    name: "TableDrawer",
    components: {
        TableStructureModal,
    },
    data() {
        return {
            services: {
                tableService: new TableService(),
            },
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
        addItem() {
            this.$refs.editModal.show(this.table.columns);
        },
        show(table) {
            if(!table || this.visible) {
                return
            }
            this.table = table;
            this.tableColumns = [];
            for(const col of table.columns) {
                this.tableColumns.push({
                    title: col.name,
                    key: "CID_" + col.id,
                    orderIndex: col.orderIndex
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
                    this.totalElems = resp.totalPages;
                    this.pageSize = resp.size;
                }
            })
        },
        rowClicked(row, index) {
            this.$refs.editModal.showEdit(this.tableRows[index],this.table.columns);
        }
        
    }
}
</script>