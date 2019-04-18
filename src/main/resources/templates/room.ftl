<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<link rel="stylesheet" href="//cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css">
<script src="//cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<style>
    body{
        margin-top:5px;
    }
</style>
<body>
<div class="container">
    <div class="row">
        <div class="col-md-3">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <h3 class="panel-title">当前登录用户</h3>
                </div>
                <div class="panel-body">
                    <div class="list-group">
                        <a href="#" class="list-group-item">你好，${name}</a>
                        <a href="logout" class="list-group-item">退出</a>
                    </div>
                </div>
            </div>
            <div class="panel panel-primary" id="online">
                <div class="panel-heading">
                    <h3 class="panel-title">当前在线的其他用户</h3>
                </div>
                <div class="panel-body">
                    <div class="list-group" id="users">
                    </div>
                </div>
            </div>
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <h3 class="panel-title">群发系统广播</h3>
                </div>
                <div class="panel-body">
                    <input type="text" class="form-control"  id="msg" /><br>
                    <button id="broadcast" type="button" class="btn btn-primary">发送</button>
                </div>
            </div>
        </div>
        <div class="col-md-9">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <h3 class="panel-title" id="talktitle"></h3>
                </div>
                <div class="panel-body">
                    <div class="well" id="log-container" style="height:400px;overflow-y:scroll">

                    </div>
                    <input type="text" id="myinfo" class="form-control col-md-12" /> <br>
                    <button id="send" type="button" class="btn btn-primary">发送</button>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="/static/js/jquery-3.3.1.min.js"></script>
<script>
    $(function() {
        // 指定websocket路径
        var websocket;
        if ('WebSocket' in window) {
            var url = "ws://localhost:8005/ws/im?uid="+${uid};
            websocket = new WebSocket(url+"&cuid=2");
        } else if ('MozWebSocket' in window) {
            websocket = new MozWebSocket("ws://localhost:8005/ws/im?uid="+${uid});
        } else {
            websocket = new SockJS("https://localhost:8005/ws/im?uid="+${uid});
        }
        //var websocket = new WebSocket('ws://localhost:8080/Spring-websocket/ws');
        websocket.onmessage = function(event) {
            var data=JSON.parse(event.data);
            if(data.statusId>0||data.statusId==-1){//用户或者群消息
                // 接收服务端的实时消息并添加到HTML页面中
                $("#log-container").append("<div class='bg-info'><label class='text-danger'>"+data.fromName+"&nbsp;"+data.date+"</label><div class='text-success'>"+data.text+"</div></div><br>");
                // 滚动条滚动到最低部
                scrollToBottom();
            }else if(data.statusId==0){//上线消息
                if(data.text!="${name}")
                {
                    $("#users").append('<a href="#" onclick="talk(this)" class="list-group-item">'+data.text+'</a>');
                    alert(data.text+"上线了");
                }
            }else if(data.statusId==-2){//下线消息
                if(data.text!="${name}")
                {
                    $("#users > a").remove(":contains('"+data.text+"')");
                    alert(data.text+"下线了");
                }
            }
        };
        $.get("/websocket/onLineUsers",function(data){
            for(var i=0;i<data.length;i++)
                $("#users").append('<a href="#" onclick="talk(this)" class="list-group-item">'+data[i]+'</a>');
        });

        $("#broadcast").click(function(){
            $.post("broadcast",{"text":$("#msg").val()});
        });

        $("#send").click(function(){
            $.get("/websocket/getUid",{"name":$("body").data("toId")},function(d){
                var v=$("#myinfo").val();

                if(v==""){
                    return;
                }else{
                    var data={};
                    data["fromId"]="${uid}";
                    data["fromName"]="${name}";
                    data["toId"]=d.id;
                    data["text"]=v;
                    websocket.send(JSON.stringify(data));
                    $("#log-container").append("<div class='bg-success'><label class='text-info'>我&nbsp;"+new Date()+"</label><div class='text-info'>"+v+"</div></div><br>");
                    scrollToBottom();
                    $("#myinfo").val("");
                }
            });

        });

    });

    function talk(a){
        $("#talktitle").text("与"+a.innerHTML+"的聊天");
        $("body").data("toId",a.innerHTML);
    }
    function scrollToBottom(){
        var div = document.getElementById('log-container');
        div.scrollTop = div.scrollHeight;
    }
</script>

</body>
</html>