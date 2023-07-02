<template>
    <RadioGroup v-model="select" @on-change="changed">
        <div v-for="(item,idx) in items">
            <Radio :label="item" />
        </div>
    </RadioGroup>
</template>
<script>
export default {
    name: "RadioBoxesEditor",
    props: {
        exp: {
            required: true
        },
        modelValue: {
            required: true
        }
    },
    watch: {
        modelValue(val) {
           this.select = val;
        },
        exp(val) {
            this.refreshItems();
        }
    },
    data() {
        return {
            items: [],
            select: null
        }
    },
    created() {
        this.refreshItems();
        this.select = this.modelValue;
    },
    methods: {
        refreshItems() {
            if(this.exp) {
                this.items = this.exp.split("/");
            }
        },
        changed() {
            this.$emit("update:modelValue",this.select)
        }
    }

}
</script>