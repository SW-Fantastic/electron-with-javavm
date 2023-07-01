<template>
    <div>
      <!-- hidden components begin 包含一些modal，contex-menu等 -->
      <vue3-menus v-model:open="menuVisible" :event="menuTriggered" :menus="treeMenu" hasIcon />
      <EditTypeModal ref="editModal" 
         @submitted="refreshTree(currentType ? currentType.parent : null)" 
      />
      <!-- hidden components end -->
      <Row span="24" class="span-loading">
        <Spin fix v-if="loading">
          <Icon type="ios-loading" size="38" class="icon-load"></Icon>
          <div style="margin: 16px">系统服务正在启动，请稍候...</div>
        </Spin>
      </Row>
      <Row>
        <Col span="8" style="height: 100vh;padding:12px">
          <Card style="height: 100%;display: flex;flex-direction: column;justify-content: space-between;">
            <div style="display: flex;flex-direction: column;justify-content: space-between;">
              <Button @click="createRootType" type="primary">创建分类</Button>
              <Divider></Divider>
              <Tree :data="typesTree" :render="renderTreeNode" />
            </div>
          </Card>
        </Col>
        <Col span="16">
          <TablesView ref="tabsView" style="height: 100%;"></TablesView>
        </Col>
      </Row>
    </div>
</template>

<script>

import { BackendSetting } from './services/Backend';
import { TypeServices } from "./services/TypeServices";
import { Logger } from "./backend/Logger"

import EditTypeModal from './views/EditTypeModal';
import TablesView from './views/TablesView';

export default {

  components: { EditTypeModal,TablesView },
  
  data() {
    const self = this;
    return {
      logger: null,
      loading: true,
      typesTree: [],
      services: {
        typeServices: new TypeServices()
      },
      treeMenu: [{
          label: "打开分类",
          tip: "左键双击",
          click() {
            if(self.currentType == null) {
              return;
            }
            self.$refs.tabsView.open(self.currentType);
          }
        },{
          label: "修改分类",
          click() {
            if(self.currentType == null) {
              return;
            }
            self.$refs.editModal.show(self.currentType,null);
          }
        },
        {
          label: "添加子分类",
          click() {
            if(self.currentType == null) {
              return;
            }
            self.$refs.editModal.show(null,self.currentType);
          },
          divided: false
        }, {
          label: "删除分类",
          click() {
            if(self.currentType === null) {
              return;
            }
            self.$Modal.confirm({
              title: '删除',
              content: '确实要删除《' + self.currentType.title + "》吗？",
              onOk() {
                const idx = self.$refs.tabsView.getTabNodeIndex(self.currentType.id);
                if(idx >= 0) {
                  self.$refs.tabsView.close(self.currentType.id);
                }
                const typeServices = self.services.typeServices;
                typeServices.trashType(self.currentType.id)
                .then(resp => {
                  if(resp.status === "Success") {
                    self.refreshTree(self.currentType.parent);
                  }
                })
              },
              onCancel() {
              }
           });
          }
        }],
        menuVisible: false,
        menuTriggered: {},
        currentType: null,
      }
  },

  created() {
    this.loading = true;
    listenBackendEvent("log", data => {
        // 监听后端的Log，并且打印
        if(this.logger == null) {
          this.logger = new Logger("Backend");
        }
        let level = data.level.toLowerCase();
        this.logger[level](data.text);
    });

    if(!BackendSetting.getServerPort()) {
      // 没有后端的端口信息，尝试启动Java
      listenBackendEvent("status", data => {
        // 监听status事件，Spring启动完毕会发送此事件
        BackendSetting.setServerPort(data.port);
        this.loading = false;
        this.refreshTree();
      });
      // 启动Java
      initializeBackend();
    } else {
      this.loading = false;
      this.refreshTree();
    }
  },

  methods: {

    refreshTree(parentNode) {
      if(parentNode) {
        parentNode.children = [];
      } else {
        this.typesTree = [];
      }
      this.$nextTick(() => {
        if(parentNode) {
          this.services.typeServices.getChildren(parentNode).then(data => {
            parentNode.children = data;
          })
        } else {
          this.services.typeServices.getRootTypes().then(data => {
            this.typesTree = data;
          })
        }
      })
    },
    createRootType() {
      this.currentType = null;
      this.$refs.editModal.show(null,null);
    },
    renderTreeNode(h,context) {
      const self = this;
      let {root, node, data} = context;
      return h('div',{
        style: {
          "display":"flex",
          "flex-direction": "row",
          "justify-content": "flex-start",
          "align-items": "center"
        },
        onclick(e) {
          self.currentType = data;
        },
        ondblclick(e) {
          self.currentType = data;
          self.$refs.tabsView.open(data);
        },
        oncontextmenu(e) {
          self.menuVisible = false;
          self.$nextTick(() => {
            self.menuTriggered = e;
            self.menuVisible = true;
            self.currentType = data;
          })
          e.preventDefault();
        }
      }, [
        h("span", data.title)
      ])
    }

  }

}
</script>

<style lang="scss">
::-webkit-scrollbar {
    width: 6px
}
::-webkit-scrollbar-track {
    background-color: transparent
}
::-webkit-scrollbar-thumb {
    background: #808695;
    border-radius: 4px
}
body {
  user-select: none;
}
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  background-color: #f8f8f9;
  height: calc(100vh);
}
.span-loading {
  font-size: 16px;
  height: 100vh;
  position: absolute;
  top:0;
  bottom:0;
  left:0;
  right:0;
}
.icon-load{
      animation: ani-loading 1s linear infinite;
}
@keyframes ani-loading {
  from { transform: rotate(0deg);}
  50%  { transform: rotate(180deg);}
  to   { transform: rotate(360deg);}
}
</style>
