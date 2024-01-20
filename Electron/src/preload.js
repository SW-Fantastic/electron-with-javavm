import { SpringServer } from "./backend/SpringServer";

let udp = sessionStorage.getItem("event-port");
const TheServer = new SpringServer(true,udp);
if(!udp) {
    sessionStorage.setItem("event-port",TheServer.udpPort);
}

window.listenBackendEvent = function(typeName, callback) {
    TheServer.serverDispatcher.listen(typeName,callback);
}

window.initializeBackend = function() {
   TheServer.initializeBackendVM();
}

window.destroyBackend = function() {
    TheServer.destroyBackendVM();
}
