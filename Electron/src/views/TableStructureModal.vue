<template>
    <Modal :title="title" v-model="visible" @on-ok="submit">
        <div style="height: 480px;max-height: 460px;overflow-y: scroll;overflow-x: hidden;padding: 12px;">
            <Form label-position="top">
                <FormItem v-for="(col, index) in columns" :label="col.name">
                     <component :is="col.type.editorId" v-model="model[col.id]"  :exp="col.type.descriptor" />
                </FormItem>
            </Form>
        </div>
    </Modal>
</template>
<script>

import TextInputEditor from './editors/TextInputEditor';
import CheckBoxesEditor from './editors/CheckBoxesEditor';
import RadioBoxesEditor from './editors/RadioBoxesEditor';

import { TableService } from '@/services/TableService';
import { TableRow } from '@/services/TableRow';

export default {
    name: "TableStructureModal",
    components: {
        TextInputEditor,
        CheckBoxesEditor,
        RadioBoxesEditor
    },
    props: {
        table: {
            required: true
        }
    },
    data() {
        return {
            services: {
                tableService: new TableService(),
            },
            visible: false,
            columns: [], 
            model : {},
            modelRules: {},
            editingId: -1
        }
    },
    computed: {
        title() {
            return this.editingId === -1 ? "添加..." : "修改...";
        }
    },
    methods: {
        showEdit(row,columns) {
            if(this.visible || (!row instanceof TableRow)) {
                return
            }
            this.columns = [];
            this.$nextTick(() => {
                this.columns = columns;
                this.columns.sort((a,b) => a.orderIndex - b.orderIndex);
                this.model = row.getDataModel();
                this.editingId = row.rowId;
                this.visible = true;
            })
        },
        show(columns) {
            if(this.visible) {
                return
            }
            this.editingId = -1;
            this.model = {};
            this.columns = [];
            this.$nextTick(() => {
                this.columns = columns;
                this.columns.sort((a,b) => a.orderIndex - b.orderIndex);
                for(const col of columns) {
                    this.model[col.id] = null;
                }
                this.visible = true;
            })
        },
        submit() {
            const service = this.services.tableService;
            if(this.editingId === -1) {
                service.addTableRow(this.table.id, this.model).then(resp => {
                    this.visible = false;
                    this.columns = [];
                    this.model = {};
                })
            } else {
                const names = Object.getOwnPropertyNames(this.model);
                const data = {};
                for(const colId of names) {
                    if(colId.startsWith("CID_")) {
                        continue;
                    }
                    data[colId] = this.model[colId];
                }
                service.updateTableRow(this.editingId,data).then(resp => {
                    this.visible = false;
                    this.columns = [];
                    this.model = {};
                })
            }
        }
    }
}
</script>