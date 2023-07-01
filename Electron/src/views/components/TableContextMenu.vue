<template>
    <vue3-menus v-model:open="menuVisible" :event="menuTriggered" :menus="tableMenu" hasIcon :zIndex="2000" />
</template>
<script>
/**
 * Table的右键菜单，将Vue3-menus用于
 * IView的Table组件。
 */
export default {

    name: "TableContextMenu",
    emits: ["update:modelValue"],
    props: {
        tableMenu: {
            require: true
        }
    },
    data() {
        return {
            menuTriggered: {},
            menuVisible: false,
        }
    },

    methods: {
        onCtxMenu(row,event,pos) {
            this.menuVisible = false;
            this.menuTriggered = event;
            this.$nextTick(() => {
                this.$emit("update:modelValue", row);
                this.menuVisible = true;
            });
            event.preventDefault();
        }
    }

}
</script>