<template>
    <Drawer :title="table.tableName" v-model="visible" placement="bottom" height="80">
        <div>
            <TableStructureModal ref="editModal"/>
            <div style="display: flex;flex-direction: row;justify-content: space-between;align-items: center;padding-bottom: 12px;">
                <div>
                    <Button type="primary" @click="addItem">添加...</Button>
                </div>
                <div>
                    <Page></Page>
                </div>
            </div>
            <Table :columns="tableColumns" :height="tableHeight"></Table>
        </div>
    </Drawer>
</template>
<script>

import TableStructureModal from './TableStructureModal';

export default {
    name: "TableDrawer",
    components: {
        TableStructureModal,
    },
    data() {
        return {
            visible: false,
            tableHeight:  window.visualViewport.height - 340,
            resizeListener: null,
            tableColumns: [],
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
                    prop: col.id,
                    orderIndex: col.orderIndex
                });
            }
            this.tableColumns.sort((a,b) => a.orderIndex - b.orderIndex);
            this.$nextTick(() => {
                this.visible = true;
            })
        }
    }
}
</script>