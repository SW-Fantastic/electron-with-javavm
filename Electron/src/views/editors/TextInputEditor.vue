<template>
    <Input v-model="content" ></Input>
</template>
<script>
export default {
    name: "TextInputEditor",
    props: {
        exp: {
            required: true
        },
        modelValue: {
            required: true
        }
    },
    data() {
        return {
            content: ""
        }
    },
    watch: {
        content(newVal) {
            this.$emit("update:modelValue",newVal);
        },
        modelValue(val) {
            this.content = val;
        }
    },
    methods : {
        
    }
}
</script>