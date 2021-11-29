from client import Client

if __name__ == "__main__":
    client = Client('localhost', 10000)

    client.connect()

    server_message = client.receive_message()
    print(f"""[CLI] SRV -> {server_message}""")

    client_runs = True
    while client_runs:
        msg = input("[CLI] Message to send:")
        client.send_message(msg)
        server_message = client.receive_message()
        print(f"""[CLI] SRV -> {server_message}""")

        if msg == 'terminate':
            client_runs = False