const express = require("express");
const http = require("http");
const { Server } = require("socket.io");
const app = express();
const server = http.createServer(app);
const io = new Server(server, {
  cors: {
    origin: "http://localhost:5173", // Vue 서버의 주소
    methods: ["GET", "POST"],
  },
});

// 클라이언트가 연결되면 메시지 출력
io.on("connection", (socket) => {
  console.log("A client connected: " + socket.id);

  // Spring 서버로부터 balance 업데이트 시 emit
  socket.on("disconnect", () => {
    console.log("Client disconnected");
  });
});

// Spring 서버에서 데이터를 수신하고, 이를 Vue로 전송
app.use(express.json());
app.post("/update", (req, res) => {
  const updatedData = req.body;
  console.log("Received updated data from Spring:", updatedData); // Spring에서 받은 데이터 확인
  io.emit("balanceUpdate", updatedData); // 모든 클라이언트에게 balance 업데이트 전달
  console.log("Sent data to Vue clients:", updatedData);
  res.sendStatus(200);
});

server.listen(3000, () => {
  console.log("Socket.IO server running on port 3000");
});
