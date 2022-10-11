// import protobuf, {util} from 'protobufjs';
import { Message, User } from './chat';
import {types} from "protobufjs";
import defaults = types.defaults;
import $ from 'jquery'
// import Sockjs from 'sockjs-client';


/*
const client = new WebSocket('ws://localhost:8080/chatproto')

client.onmessage = async (ev) => {
    receiveMsg(Message.decode(new Uint8Array(await ev.data.arrayBuffer())));
}

client.onclose = () => {
    alert("Server Disconnect You")
}

client.onopen = () => {
    let name = "";
    while (name == "") { // @ts-ignore
        name = prompt("Enter your name");
    }
    sendMessage("join", name);
}

$("#send").click(function (){
    sendMessage("say", (<HTMLInputElement>document.getElementById("msg")).value);
})

$("#msg").keypress(function(e) {
    if(e.which == 13) sendMessage("say", e.target.value);
});

function sendMessage(type: string, date: string) {
    if(date != "") {
        client.send(Message.encode({ msgType: type, data: date, user: undefined }).finish());
        (<HTMLInputElement>document.getElementById("msg")).value = "";
        document.getElementById("msg")?.focus();
    }
}

function receiveMsg(msg: Message) {
    if (msg.msgType == "say") {
        $("#chatbox").append("<p>" + msg.data + "</p>");
    }
    else if (msg.msgType == "join") {
        addUser(msg.user);
    }
    else if (msg.msgType == "left") {
        $("#user"+msg.user?.id).remove();
    }
}

function addUser(user: User | undefined) {
    $("#userlist").append("<li id='user"+user?.id+"'>"+user?.name+"</li>");
}*/

let ws = new WebSocket('ws://0.0.0.0:8080/uw')

ws.onopen = () => {
    let name: string = "";
    let gameCode: string = "";
    while (name == "") { // @ts-ignore
        name = prompt("Enter your name");
    }
    while (gameCode == "") { // @ts-ignore
        gameCode = prompt("Enter your session code");
    }
    sendMessage("join", JSON.stringify({playerName: name, gameCode: gameCode}))
    //ws.send(JSON.stringify({type:"join", playerName: name, gameCode: gameCode}));
}

ws.onmessage = async (ev) => {
    let msg = JSON.parse(ev.data)

    if(msg.type == "join") {
        console.log(msg.data)
        addPlayer(msg.data)
    }
    else if(msg.type == "players") {
        msg.data.prototype.forEach(function(el) { addPlayer(el); });
    }
    else if(msg.type == "left") {
        console.log(msg.data.id)
        $("#player-"+msg.data.id).remove();
        $("#activation-"+msg.data.id).remove();
    }
    else if(msg.type == "end_activation_step") {
        console.log(msg.data)
    }
    else if(msg.type == "end_phase") {
        endPhase()
    }
}

function sendMessage(type: String, data: String) {
    ws.send(JSON.stringify({type:type, data: data}));
}

function addPlayer(player: any) {
    $("#player-list").append("" +
        "<li id='player-"+player.id+"'>"+player.playerName+"</li>" +
        "<p id='activation-" + player.id + "'>" + player.activation + 1 + "</p>");
}

function endActivationStep() {
    sendMessage("end_activation_step", "")
}

function endPhase() {

}

function gloryPointGet(input: String) {
    sendMessage("get_glory_point", input)
}

function gloryPointUse(input: String) {
    sendMessage("use_glory_point", input)
}

$("#activation-end").click(function () {
    endActivationStep()
});