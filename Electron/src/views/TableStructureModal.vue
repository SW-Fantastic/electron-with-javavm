<template>
    <Modal :title="title" width="80" v-model="visible" @on-ok="submit">
        <div :style="{height: modalHeight + 'px', 'max-height': 'calc(' + modalHeight + 'px - 10px)'}" style="overflow-y: scroll;overflow-x: hidden;padding: 12px;">
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
import SelectConnectedEditor from './editors/SelectConnectedEditor';
import DateEditor from './editors/DateEditor';

import { TableService } from '@/services/TableService';
import { TableRow } from '@/services/TableRow';

export default {
    name: "TableStructureModal",
    components: {
        TextInputEditor,
        CheckBoxesEditor,
        RadioBoxesEditor,
        DateEditor,
        SelectConnectedEditor
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
            editingId: -1,
            modalHeight: window.innerHeight * 0.6,
            resizeListener: null
        }
    },
    mounted() {
        this.resizeListener = window.addEventListener("resize", () => {
            this.modalHeight = window.innerHeight * 0.6;
        })
    },
    unmounted() {
        window.removeEventListener("resize", this.resizeListener);
    },
    computed: {
        title() {
            return this.editingId === -1 ? "添加..." : "修改...";
        },
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
                    this.$emit("refresh")
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
                    this.$emit("refresh")
                })
            }
        }
    }
}
</script>