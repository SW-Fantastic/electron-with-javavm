<template>
      <Modal title="数据类型"
            width="65"
            v-model="editDataVisible"
            footer-hide
            @click="resetEditing"
            class-name="vertical-center-modal">
            <div style="height: 480px;">
                <TableContextMenu ref="ctxMenu" v-model="contextMenuTarget" :tableMenu="tableMenu"/>
                <div style="height: 44px;">
                    <Form ref="fromAddType" :model="formAdd" :rules="ruleAddType" inline>
                        <div style="display: flex;flex-direction: row;justify-content: space-between;align-items:center;">
                            <div>
                                <FormItem prop="name">
                                    <Input type="text" v-model="formAdd.name" placeholder="类型名称"></Input>
                                </FormItem>
                                <FormItem prop="descriptor">
                                    <Input type="text" v-model="formAdd.descriptor" placeholder="表达式">
                                    </Input>
                                </FormItem>
                                <FormItem>
                                    <Select style="width: 120px;" v-model="formAdd.editorType">
                                        <Option v-for="(item, idx) in Object.getOwnPropertyNames(editorTypes)" :value="editorTypes[item].id">{{ editorTypes[item].name }}</Option>
                                    </Select>
                                </FormItem>
                            </div>
                            <FormItem>
                                <Button type="primary" @click="addColumnType">添加</Button>
                            </FormItem>
                        </div>
                    </Form>
                </div>
                <Divider></Divider>
                <Table :height="380" :columns="columns" :data="items" size="small"  @on-contextmenu="showTableContextMenu">
                    <template #name="{row,index}">
                        <Input @on-enter="submitEditing" @click.stop v-if="itemEditingIndex === index" v-model="itemEditingName" />
                        <span v-else>{{ row.name }}</span>
                    </template>
                    <template #descriptor="{row, index}">
                        <Input @on-enter="submitEditing" @click.stop v-if="itemEditingIndex === index" v-model="itemEditingValue" />
                        <span v-else >{{ row.descriptor }}</span>
                    </template>
                    <template #editorType="{row,index}">
                        <Select @click.stop  v-if="itemEditingIndex === index" style="width: 120px;" v-model="itemEditingEditor" @on-select="editorChanged">
                            <Option v-for="(item, idx) in Object.getOwnPropertyNames(editorTypes)" :value="editorTypes[item].id">{{ editorTypes[item].name }}</Option>
                        </Select>
                        <span v-else>{{ editorTypes[row.editorId].name }}</span>
                    </template>
                </Table>
            </div>
        </Modal>
</template>
<script>

import TableContextMenu from "./components/TableContextMenu";
import { ColumnTypeService } from "../services/ColumnTypeService";

import { Editors } from "./editors/Editors";

export default {
    name: "EditDatatypeModal",
    components: { TableContextMenu },
    data() {
        return {
            editDataVisible: false,
            contextMenuTarget: null,
            formAdd: {
                name: "",
                descriptor: "",
                editorType: null
            },
            ruleAddType: {
                name: [
                    { required: true, message: '类型名称不能为空', trigger: 'blur' }
                ],
                descriptor: [
                    { required: true, message: '请输入类型的类型规则不能为空', trigger: 'blur' },
                ]
            },
            services: {
                columnTypeService: new ColumnTypeService()
            },
            items: [],
            itemEditingIndex: null,
            itemEditingEditor: null,
            itemEditingName: "",
            itemEditingValue: "",
            columns: [{
                title: "名称",
                slot: "name"
            }, {
                title: "表达式",
                slot: "descriptor",
                ellipsis: true,
            }, {
                title: "编辑方式",
                slot: "editorType"
            }],
            editorTypes: {},
        };
    },
    mounted() {
        this.editorTypes = Editors;
    },
    computed: {
        tableMenu() {
            return [{
                label: "修改",
                tip: "按Enter确认",
                click: this.menuEdit
            }, {
                label: "删除",
                tip: "无法撤销",
                click: this.menuDelete
            }]
        },
    },
    methods: {
        
        menuDelete() {
            if(this.contextMenuTarget === null) {
                return
            }
            let targetIdx = -1;
            for(let idx = 0; idx < this.items.length; idx ++) {
                if(this.items[idx].id == this.contextMenuTarget.id) {
                    targetIdx = idx;
                }
            }
            const item = this.items[targetIdx];
            const service = this.services.columnTypeService;
            service.deleteColumnType(item.id).then(resp => {
                if(resp.status !== "Success") {
                    this.$Message.error('类型可能已经被使用，所以无法删除。');
                    return;
                }
                this.$nextTick(() => {
                    this.items.splice(targetIdx,1);
                })
            }).catch(e => {
                this.$Message.error('类型可能已经被使用，所以无法删除。');
            });
        },

        menuEdit() {
            if(this.contextMenuTarget === null) {
                return
            }
            for(let idx = 0; idx < this.items.length; idx ++) {
               if(this.items[idx].id == this.contextMenuTarget.id) {
                    this.itemEditingIndex = idx;
                    this.itemEditingName = this.items[idx].name;
                    this.itemEditingValue = this.items[idx].descriptor;
                    this.itemEditingEditor = this.items[idx].editorId;
                    break;
                }
            }
            this.$Message.info('按下Enter将会保存修改后的数据，点击空白处将会取消修改。');
        },
        async refreshTypes() {
            const columnTypeService = this.services.columnTypeService;
            this.items = await columnTypeService.getColumnTypes();
        },
        show() {
            this.refreshTypes().then(() => {
                if (!this.editDataVisible) {
                    this.editDataVisible = true;
                }
            });
        },
        showTableContextMenu(row,event,pos) {
            this.$refs.ctxMenu.onCtxMenu(row,event,pos);
        },
        addColumnType() {
            this.$refs.fromAddType.validate((valid) => {
                if (valid) {
                    const columnTypeService = this.services.columnTypeService;
                    columnTypeService.addColumnType(this.formAdd.name, this.formAdd.descriptor,this.formAdd.editorType)
                        .then(resp => {
                            this.refreshTypes();
                            this.formAdd.name = '';
                            this.formAdd.descriptor = '';
                            this.formAdd.editorType = null;
                        })
                } else {
                    this.$Message.error('请正确的输入需要填写的内容!');
                }
            })
        },
        submitEditing() {
            if(this.itemEditingIndex === -1 || this.itemEditingIndex === null) {
                return;
            }
            const columnTypeService = this.services.columnTypeService;
            const item = this.items[this.itemEditingIndex];
            this.$nextTick(() => {
                columnTypeService.updateColumnType(
                    item.id,
                    this.itemEditingName, 
                    this.itemEditingValue,
                    this.itemEditingEditor
                ).then(resp => {
                    if(resp.state !== "Success") {
                        this.resetEditing();
                    } else {
                        this.items.splice(this.itemEditingIndex,1,resp.data);
                        this.itemEditingIndex = -1;
                    }
                })
            });
        },
        editorChanged(item) {
            this.itemEditingEditor = item.value;
            this.submitEditing();
        },
        resetEditing() {
            if(this.itemEditingIndex === -1 || this.itemEditingIndex === null) {
                return;
            } 
            const columnTypeService = this.services.columnTypeService;
            const item = this.items[this.itemEditingIndex];
            this.$nextTick(() => {
                columnTypeService.getFieldType(item.id).then(data => {
                    if(data !== null) {
                        this.items.splice(this.itemEditingIndex,1,data);
                    }
                    this.itemEditingIndex = -1;
                });
            })
            
        }
    }
}
</script>