package fingertips.backend.home.service;

import fingertips.backend.home.dto.BalanceDTO;
import org.springframework.stereotype.Service;

import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;
import java.util.List;

@Service
public class SocketIOService {

    private Socket socket;

    public SocketIOService() throws URISyntaxException {
        socket = IO.socket("http://localhost:3001");
        socket.connect();
    }

    public void sendBalanceUpdate(String memberId, List<BalanceDTO> balanceList) {
        socket.emit("balanceUpdate", memberId, balanceList);
    }
}
