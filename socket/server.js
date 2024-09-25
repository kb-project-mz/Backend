const express = require("express");
const http = require("http");
const { Server } = require("socket.io");
const cors = require("cors"); // CORS 모듈 추가

// Express 앱 생성
const app = express();

// CORS 설정 추가
app.use(cors());

const server = http.createServer(app);
const io = new Server(server, {
  cors: {
    origin: "http://localhost:5173", // Vue.js 클라이언트의 포트 추가
    methods: ["GET", "POST"],
  },
});

// 클라이언트가 연결되었을 때의 처리
io.on("connection", (socket) => {
  console.log("A user connected");

  socket.on("message", (msg) => {
    console.log("Message received:", msg);
    io.emit("message", msg);
  });

  socket.on("disconnect", () => {
    console.log("A user disconnected");
  });
});

// 서버 포트 설정 및 실행
server.listen(3001, () => {
  console.log("Node.js server listening on http://localhost:3001");
});
