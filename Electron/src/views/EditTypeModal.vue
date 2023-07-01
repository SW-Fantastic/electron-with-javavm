<template>
    <Modal :title="headerText"
            v-model="addTypeModalVisible"
            @on-ok="onSubmit"
            class-name="vertical-center-modal">
            <Input v-model="title" placeholder="请输入分类名称"></Input>
    </Modal>
</template>
<script>

import { TypeServices } from '@/services/TypeServices';

export default {

    name: "EditTypeModal",

    data() {
        return {
            services: {
                typeServices: new TypeServices()
            },
            headerText: "",
            addTypeModalVisible: false,
            title: "",
            parent: null,
            editing: null
        }
    },

    methods: {

        show(editing, parent) {
            
            this.editing = editing;
            this.parent = parent;

            if(this.editing !== null) {
                this.headerText = this.editing.title + " - 修改分类"
            } else if(this.parent !== null) {
                this.headerText = this.parent.title + " - 创建子分类"
            } else {
                this.headerText = "创建分类"
            }

            if(!this.addTypeModalVisible) {
                this.addTypeModalVisible = true;
            }
        },

        onSubmit() {
            const typeServices = this.services.typeServices;
            if(this.title === '') {
                this.$Message.error('操作失败！名称不能为空。');
                return;
            }
            if(this.editing == null) {
                if(this.parent != null) {
                    // 创建到Parent中
                    typeServices.createSubType(this.parent.id,this.title).then(resp => {
                        if(resp.status === "Success") {
                            this.$emit("submitted")
                            this.title = ""
                        }
                    });
                } else {
                    // 创建Root分类
                    typeServices.createRootType(this.title).then(resp => {
                        if(resp.status === "Success") {
                            this.$emit("submitted")
                            this.title = ""
                        }
                    });
                }
                
            } else {
                typeServices.updateType(this.editing.id, this.title).then(resp => {
                    if(resp.status === "Success") {
                        this.$emit("submitted")
                        this.title = ""
                    }
                })
            }
            
        }

    }

}
</script>