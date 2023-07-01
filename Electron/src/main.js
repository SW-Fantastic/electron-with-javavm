import { createApp } from 'vue'
import App from './App.vue'
import store from './store'
import ViewUIPlus from 'view-ui-plus'
import Menus from 'vue3-menus';

import 'view-ui-plus/dist/styles/viewuiplus.css'

createApp(App)
    .use(store)
    .use(ViewUIPlus)
    .use(Menus)
    .mount('#app')
