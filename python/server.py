import socket
import bank as b

class Server:
    """Server class that handles client requests."""
    def __init__(self, bank: str, ip: str, port: int, backlog: int):
        """Server constructor."""
        self.__bank = b.Bank(bank)
        self.__ip = ip
        self.__port = port
        self.__backlog = backlog
        self.__client_socket = None
        self.__keep_running = True
        self.__keep_running_client = False

    @property
    def bank(self):
        return self.__bank

    def display_message(self, message: str):
        print(f'[SRV] >> {message}')

    def send_message(self, msg: str):
        """Send message to a client."""
        self.display_message(f"""SEND>> {msg}""")
        self.__client_socket.send(msg.encode('UTF-8'))

    def receive_message(self, max_length: int=1024):
        """Receive message from a client."""
        msg = self.__client_socket.recv(max_length).decode('UTF-8')
        self.display_message(f"""RCV>> {msg}""")
        return msg

    def run(self):
        """Run the server."""
        server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server_socket.bind((self.__ip, self.__port))
        server_socket.listen(self.__backlog)

        # Main event loop (accept connection, accept and process requests)
        while self.__keep_running:
            print(f"""[SRV] Waiting for a Client""")
            self.__client_socket, client_address = server_socket.accept()
            print(f"""Received a connection from {client_address}""")

            self.send_message("Connected to Python Bank Server\n")

            self.__keep_running_client = True
            while self.__keep_running_client:
                self.process_client_request()

            self.__client_socket.close()

        server_socket.close()

    def process_client_request(self):
        """Process message received from the client."""

        def check_arguments(arguments: list, expect_arguments: int, must_match: int, *args) -> bool:
            """Check whether the number of arguments and argument format matches given argument list."""
            for i in range(must_match):
                if arguments[i] != args[i]:
                    return False
            if len(arguments) != expect_arguments:
                raise ValueError("Number of arguments does not match the requested function.")
            for i, arg in enumerate(args):
                if arguments[i] != arg:
                    raise ValueError(f'The argument at position {i+1} does not match the request format.')
            return True

        client_message = self.receive_message()
        # arguments = client_message.split('|')      # used for local python client testing
        arguments = client_message[:-2].split('|')      # java sends commands with a \n character at the end

        try:
            # Login
            if check_arguments(arguments, 3, 1, 'login'):
                print('RECEIVED A LOGIN REQUEST AT THE SERVER')
                response = self.bank.login(arguments[1], arguments[2])

            # Get all accounts for currently logged in customer
            elif check_arguments(arguments, 3, 3,'customer', 'get', 'all'):
                if self.bank.current_customer == None:
                    raise ValueError('No customer is currently logged in.')
                print('RECEIVED A GET REQUEST FROM CLIENT TO RETRIEVE ALL CURRENT_CUSTOMER ACCOUNT IDs.')
                account_ids = self.bank.current_customer.get_account_ids()
                response = f'success|{"|".join(account_ids)}'

            # Get a specific account
            elif check_arguments(arguments, 3, 2, 'account', 'get'):
                if self.bank.current_customer == None:
                    raise ValueError('No customer is currently logged in.')
                print('RECEIVED A GET REQUEST FROM CLIENT TO RETRIEVE A SPECIFIC ACCOUNT.')
                account = self.bank.current_customer.get_account(arguments[2])
                if account:
                   response = f'success|{str(account)}'
                else:
                    raise ValueError(f'Account with id {arguments[2]} not found.')

            # Terminate client's connection
            elif check_arguments(arguments, 1, 1, 'terminate'):
                response = 'TERMINATING CONNECTION...'
                self.__keep_running_client = False
                self.bank.current_customer = None

            # Invalid requests
            else:
                raise ValueError('I could not understand that message.')

        except (ValueError, IndexError) as ve:
            response = 'fail|' + str(ve)
            print('Something went wrong when processing the request.')

        self.send_message(response + '\n')