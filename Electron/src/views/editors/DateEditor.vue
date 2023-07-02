<template>
    <DatePicker v-model="content" type="date" placeholder="Select date" style="width: 100%" />
</template>
<script>
export default {

    name: "DateEditor",

    props: {
        exp: {
            required: true
        },
        modelValue: {
            required: true
        }
    },

    watch: {
        content(val) {
            this.$emit("update:modelValue", val);
        }
    },

    data() {
        return {
            content: ""
        }
    },

    created() {
        this.content = this.modelValue === "" ? moment(this.exp).utc() : this.modelValue;
    }

}
</script>