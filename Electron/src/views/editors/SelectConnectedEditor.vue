<template>
    <Select v-model="content"
            filterable 
            @on-change="valueChanged"
            :remote-method="loadOptions" 
            :loading="loading">
            <Option v-for="(opt, idx) in options" :value="getString(opt)">{{ getString(opt) }}</Option>
    </Select>
</template>
<script>
import { TableService  } from '@/services/TableService';
import { Editors } from './Editors';
export default {

    name: "SelectConnectedEditor",

    props:{

        exp: {
            required: true
        },

        modelValue: {
            required: true
        }

    },

    data() {
        return {
            services: {
                tableService: new TableService()
            },
            content: "",
            loading: false,
            options: []
        }
    },

    created() {
        this.content = this.modelValue;
    },

    methods: {
        getString(item) {
            const column = item.column;
            const editorId = column.type.editorId;
            return Editors[editorId].asString(item.data);
        },
        loadOptions(query) {
            const service = this.services.tableService;
            const kv = this.exp.split("/");
            if(kv.length != 2) {
                return
            }
            this.loading = true;
            this.options = [];
            service.searchForData(kv[0],kv[1],query).then(resp => {
                if(resp.status === "Success") {
                    this.options = resp.data;
                    this.loading = false;
                }
            })
        },
        valueChanged(value) {
            if (! value || value === "") {
                return
            }
            this.$emit("update:modelValue",value);
        }
    }

}
</script>