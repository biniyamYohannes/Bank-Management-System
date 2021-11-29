import socket

class Client:
    """Client class that will send messages to a server."""
    def __init__(self, ip: str, port: int):
        """Client constructor."""
        self.__ip = ip
        self.__port = port
        self.__is_connected = False
        self.__client_socket = None

    def connect(self):
        """Connect to a server."""
        self.__client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.__client_socket.connect((self.__ip, self.__port))
        self.__is_connected = True

    def  send_message(self, msg: str):
        """Send a message to the server."""
        self.__client_socket.send(msg.encode('UTF-16'))

    def receive_message(self):
        """Receive a message from the server."""
        return self.__client_socket.recv(1024).decode('UTF-16')

    def disconnect(self):
        self.__client_socket.close()
        self.__is_connected = False