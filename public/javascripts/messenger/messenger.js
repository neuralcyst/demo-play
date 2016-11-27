define(['jquery'], function(jquery) {
    return {

        socket: null,

        init: function() {
            this.initWebSocket();
            this.initButtons();
        },

        send: function() {
            var outgoingUser = $("#usr1").val();
            var outgoingMessage = $("#msg1").val();
            var messageObject = {
                event: "add",
                user: outgoingUser,
                message: outgoingMessage
            }
            this.socket.send(JSON.stringify(messageObject));
        },

        showMessage: function(messages) {
            console.log(messages);

            $("#messages-counter").text(messages.length);

            var messagesRoot = $('#messages-root');
            messagesRoot.children().remove();

            messages.forEach(function(entry) {
                var messageElem = document.createElement('a');
                messageElem.setAttribute('href', '#');
                messageElem.setAttribute('class', 'list-group-item');

                var messageElemHeader = document.createElement('h5');
                messageElemHeader.setAttribute('class', 'list-group-item-heading');

                var messageElemHeaderSpan = document.createElement('span');
                messageElemHeaderSpan.setAttribute('class', 'glyphicon glyphicon-user');
                messageElemHeaderSpan.appendChild(document.createTextNode(''));
                messageElemHeader.appendChild(messageElemHeaderSpan);
                messageElemHeader.appendChild(document.createTextNode(entry.user));

                var messageElemBody = document.createElement('p');
                messageElemBody.setAttribute('class', 'list-group-item-text');
                messageElemBody.appendChild(document.createTextNode(entry.message));

                messageElem.appendChild(messageElemHeader);
                messageElem.appendChild(messageElemBody);
                messagesRoot[0].appendChild(messageElem);
            });
        },

        initWebSocket: function () {
            var it = this;
            this.socket = new WebSocket($("meta[name='websocketurl']").one().attr("content"));

            this.socket.onopen = function(event) {
                var askEvent = {
                    event: "getAll"
                }
                this.send(JSON.stringify(askEvent));
            };

            this.socket.onmessage = function(event) {
                var incomingMessage = JSON.parse(event.data);
                it.showMessage(incomingMessage.messages);
            };
        },

        initButtons: function () {
            var it = this;
            $('#btn1').click(function() {
                it.send();
            });
        }
    }
});
