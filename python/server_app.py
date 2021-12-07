from server import Server, MultiServer

if __name__ == "__main__":
    server = MultiServer('First Bank', 'localhost', 10000, 5)

    server.run()
