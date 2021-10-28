import socket

HOST = '127.0.0.1'  # The server's hostname or IP address
PORT = 65432        # The port used by the server

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.connect((HOST, PORT))
    while True:
        message = input("Write your message: ").encode('utf-8')
        s.sendall(message)
        data = s.recv(1024)
        received = data.decode('utf-8')
        if received == "exit":
            print("Received exit command. Shutting down.")
            break
        print(received)        

# print('Received', repr(data.decode('utf-8')))