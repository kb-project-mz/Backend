const express = require("express");
const http = require("http");
const { Server } = require("socket.io");
const cors = require("cors");

const app = express();

// cors 설정 추가
app.use(cors());

const server = http.createServer(app);
const io = new Server(server, {
  cors: {
    // vue 클라이언트 포트
    origin: "http://localhost:5173",
    methods: ["GET", "POST"],
  },
});

// 클라이언트가 연결되었을 때의 처리
io.on("connection", (socket) => {
  console.log("A user connected");

  // balance DB가 업데이트 되었을 때 실행
  socket.on("balanceUpdate", (memberId, balanceList) => {
    console.log(`Balance updated for member: ${memberId}`);
    io.emit("balanceUpdate", { memberId, balanceList });
  });

  socket.on("disconnect", () => {
    console.log("A user disconnected");
  });
});

// 서버 포트 설정 및 실행
server.listen(3001, () => {
  console.log("Node.js server listening on http://localhost:3001");
});
