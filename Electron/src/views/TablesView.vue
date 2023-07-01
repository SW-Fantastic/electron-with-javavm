<template>
    <div style="padding-top: 12px;">
        <Tabs :animated="false" v-model="activeTab" type="card" style="height: 100%;" @on-tab-remove="closeTab">
            <div v-for="(elem, idx) in tabOfTypes">
                <TabPane closable :name="'TID' + elem.id" :label="elem.title" >
                    <DescTableView :typeNode="elem"></DescTableView>
                </TabPane>
            </div>
        </Tabs>
    </div>
</template>
<script>

import DescTableView from './DescTableView';

export default {

    name : "TablesView",
    components: {
        DescTableView
    },

    data() {
        return {
            tabOfTypes: [],
            activeTab: ''
        }
    },

    methods : {

        open(typeNode) {
            let pos = this.getTabNodeIndex(typeNode.id);
            if(pos == -1) {
                this.tabOfTypes.push(typeNode);
            }
            this.activeTab = "TID" + typeNode.id;
        },

        getTabNodeIndex(typeId) {
            for(const node of this.tabOfTypes) {
            if(node.id === typeId) {
                let pos = this.tabOfTypes.indexOf(node);
                return pos;
            }
          }  
          return -1;
        },

        closeTab(name) {
            const id = parseInt(name.replace("TID", ""))
            this.close(id);
        },
        close(typeId) {
            let pos = this.getTabNodeIndex(typeId);
            if(pos === -1) {
                return;
            }
            this.tabOfTypes.splice(pos,1);
            if(this.tabOfTypes.length > 0) {
                this.$nextTick(() => {
                    this.activeTab = "TID" + this.tabOfTypes[0].id;
                })
            }
        }

    }

}
</script>