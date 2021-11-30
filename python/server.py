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

        def check_arguments(arguments: list, num_arguments, *args):
            """Check whether the number of arguments and argument format matches given argument list."""
            for i, arg in enumerate(args):
                if arguments[i] != arg:
                    return False
            if len(arguments) != num_arguments:
                raise ValueError("Number of arguments does not match the requested service.")
            return True

        client_message = self.receive_message()
        # self.display_message(f'CLIENT SAID>>>{client_message}')
        arguments = client_message.split('|')
        response = ''

        try:
            if check_arguments(arguments, 3, 'login'):
                print('RECEIVED A LOGIN REQUEST AT THE SERVER')
                if self.bank.current_customer == None:
                    customer = b.Customer(arguments[1], arguments[2])
                    if customer == None:
                        raise ValueError('fail|Failed to retrieve customer data for the provided email and password.')
                    else:
                        self.bank.current_customer = customer
                        response = f'success|{str(customer)}'# + f' (The current_customer is set to ' \
                                                             #   f'{self.bank.current_customer.fname} ' \
                                                             #   f'{self.bank.current_customer.lname})'
                else:
                    response = 'fail|A customer is already logged into the current session.' \
                               ' Please log out first.'

            elif check_arguments(arguments, 3, 'customer', 'get', 'all'):
                response = 'The client has requested all accounts for the current customer.'


            elif arguments[0] == 'terminate':
                response = 'TERMINATING CONNECTION...'
                self.__keep_running_client = False
            else:
                response = "fail|I couldn't understand that message."

        except ValueError or IndexError as ve:
            response = 'fail|' + str(ve)
            print('Something went wrong when processing the request.')

        self.send_message(response + '\n')