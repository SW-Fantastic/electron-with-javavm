<template>
    <Modal title="添加.." v-model="visible" @on-ok="submit">
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

export default {
    name: "TableStructureModal",
    components: {
        TextInputEditor,
        CheckBoxesEditor,
        RadioBoxesEditor
    },
    data() {
        return {
            visible: false,
            columns: [], 
            model : {},
            modelRules: {}
        }
    },
    methods: {
        show(columns) {
            if(this.visible) {
                return
            }
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
            console.log(this.model);
        }
    }
}
</script>