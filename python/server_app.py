from server import Server

if __name__ == "__main__":
    server = Server('localhost', 10000, 10)
    server.run()