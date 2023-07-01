<template>
    <div>
        <div v-for="(item,idx) in items">
            <Checkbox @on-change="changed" v-model="values[item]">{{ item }}</CheckBox>
        </div>
    </div>
</template>
<script>
export default {
    name: "CheckBoxesEditor",
    props: {
        exp: {
            required: true
        },
        modelValue: {
            required: true
        }
    },
    watch: {
        exp(val) {
            this.refreshItems();
        },
        modelValue(val) {
            if(!val) {
                this.values = {};
                return;
            } 
            const selItems = val.split("/");
            const keys = Object.getOwnPropertyNames(this.values);
            for(const key of keys) {
                if(selItems.indexOf(key) === -1) {
                    this.values[key] = false;
                } else {
                    this.values[key] = true;
                }
            }
        }
    },
    data() {
        return {
            items: [],
            values: {}
        }
    },
    created() {
        this.refreshItems()
    },
    methods: {
        refreshItems() {
            if(this.exp) {
                this.items = this.exp.split("/");
                for(let item of this.items) {
                    this.values[item] = false;
                }
            }
        },
        changed() {
            const keys = Object.getOwnPropertyNames(this.values);
            let selected = "";
            for(const key of keys) {
                if(this.values[key] === true) {
                    selected = (selected === "") ? key : selected + "/" + key;
                }
            }
            this.$emit("update:modelValue",selected);
        }
    }

}
</script>