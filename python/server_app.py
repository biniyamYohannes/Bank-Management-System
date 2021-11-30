from server import Server

if __name__ == "__main__":
    server = Server('First Bank', 'localhost', 10000, 5)

    server.run()