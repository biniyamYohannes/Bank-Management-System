import socket
from mysql.connector.errors import IntegrityError, DataError
from bank import Bank
from threading import Thread

class MultiServer:
    """MultiServer class that handles multiple client connections."""

    def __init__(self, bank: str, ip: str, port: int, backlog: int):
        """Server constructor."""
        self.__bank = bank
        self.__ip = ip
        self.__port = port
        self.__backlog = backlog
        self.__server_socket = None
        self.__keep_running = True
        self.__connection_count = 0
        self.__list_cw = []

    def terminate_server(self):
        self.__keep_running = False
        self.__server_socket.close()

    @property
    def bank(self):
        return self.__bank

    def run(self):
        """Run the server."""
        self.__server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.__server_socket.bind((self.__ip, self.__port))
        self.__server_socket.listen(self.__backlog)

        # Main event loop (accept connection, accept and process requests)
        while self.__keep_running:
            print(f"""[SRV] Waiting for a Client""")
            try:
                client_socket, client_address = self.__server_socket.accept()

                print(f"""Received a connection from {client_address}""")
                self.__connection_count += 1
                cw = ClientWorker(self.__connection_count, client_socket, self.__bank, self)
                self.__list_cw.append(cw)
                cw.start()
            except Exception as e:
                print(e)

        cw: ClientWorker
        for cw in self.__list_cw:
            cw.terminate_connection()
            cw.join()


class ClientWorker(Thread):
    """ClientWorker class objects serve a single client's requests"""
    def __init__(self, client_id: int, client_socket: socket, bank: str, server: MultiServer):
        super().__init__()
        self.__client_socket = client_socket
        self.__keep_running_client = True
        self.__bank = Bank(bank)
        self.__id = client_id
        self.__server = server

    @property
    def bank(self):
        return self.__bank

    def run(self):
        self.send_message("Connected to Python Bank Server\n")

        while self.__keep_running_client:
            self.process_client_request()

        self.__client_socket.close()

    def terminate_connection(self):
        self.__keep_running_client = False
        self.__client_socket.close()

    def display_message(self, message: str):
        print(f'[SRV] >> {message}')

    def send_message(self, msg: str):
        """Send message to a client."""
        self.display_message(f"""SEND>> {msg}""")
        self.__client_socket.send(msg.encode('UTF-8'))

    def receive_message(self, max_length: int = 1024):
        """Receive message from a client."""
        msg = self.__client_socket.recv(max_length).decode('UTF-8')
        self.display_message(f"""RCV>> {msg}""")
        return msg

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
                    raise ValueError(f'The argument at position {i + 1} does not match the request format.')
            return True

        def is_logged_in():
            """Raise a ValueError if no customer is currently logged in."""
            if self.bank.current_customer == None:
                raise ValueError('No customer is currently logged in.')

        def no_duplicate_login(email: str):
            """Raise a ValueError if the customer is already logged in in another session."""
            if email in Bank.logged_in:
                raise ValueError('Another customer with these credentials is already logged in. '
                                 'Close your other session first.')
            else:
                return True

        client_message = self.receive_message()
        arguments = client_message.split('|')      # used for local python client testing
        # arguments = client_message[:-2].split('|')  # java sends commands with a \n character at the end

        try:
            # Login
            if check_arguments(arguments, 3, 1, 'login'):
                print('RECEIVED A LOGIN REQUEST AT THE SERVER')
                if no_duplicate_login(arguments[1]):
                    response = f'success|{str(self.bank.login(arguments[1], arguments[2]))}'

            # Get all accounts for currently logged in customer
            elif check_arguments(arguments, 3, 3,'customer', 'get', 'all'):
                is_logged_in()
                print('RECEIVED A GET REQUEST FROM CLIENT TO RETRIEVE ALL CURRENT_CUSTOMER ACCOUNT IDs.')
                account_ids = self.bank.current_customer.get_account_ids()
                response = f'success|{"|".join(account_ids)}'

            # Get a specific account
            elif check_arguments(arguments, 3, 2, 'account', 'get'):
                is_logged_in()
                print('RECEIVED A GET REQUEST FROM CLIENT TO RETRIEVE A SPECIFIC ACCOUNT.')
                account = self.bank.current_customer.get_account(arguments[2])
                if account:
                   response = f'success|{str(account)}'
                else:
                    raise ValueError(f'Account with id {arguments[2]} not found.')

            # Get all transactions for the requested account
            elif check_arguments(arguments, 3, 2,'transaction', 'get'):
                is_logged_in()
                print(f'RECEIVED A GET REQUEST FROM CLIENT TO RETRIEVE TRANSACTIONS FOR ACCOUNT WITH ID = {arguments[2]}.')
                transactions = self.bank.current_customer.get_transactions(arguments[2])
                if transactions:
                    response = f'success|{"|".join(transactions)}'
                elif not transactions:
                    raise ValueError(f'No transactions for account with id {arguments[2]} were found.')

            # Modify account and insert transaction
            elif check_arguments(arguments, 4, 2, 'transaction', 'put'):
                print(f'RECEIVED A REQUEST FROM CLIENT TO PERFORM A TRANSACTION ON ACCOUNT WITH ID = {arguments[2]}.')
                is_logged_in()
                response = f'success|{self.bank.current_customer.perform_transaction(arguments[2], arguments[3])}'

            elif check_arguments(arguments, 6, 2, 'customer', 'post'):
                print(f'RECEIVED A REQUEST FROM CLIENT TO CREATE A NEW CUSTOMER WITH FIRST NAME {arguments[2]},'
                      f' LAST NAME {arguments[3]}, EMAIL {arguments[4]}, AND PASSWORD {"*" * len(arguments[5])}.')
                self.bank.add_customer(arguments[2], arguments[3], arguments[4], arguments[5])
                response = 'success|'

            elif check_arguments(arguments, 5, 2, 'account', 'post'):
                print(f'RECEIVED A REQUEST FROM CLIENT TO CREATE A NEW ACCOUNT WITH TYPE {arguments[2]}, '
                      f'RATE {arguments[3]}, LIMIT {arguments[4]}.')
                is_logged_in()
                self.bank.current_customer.add_account(arguments[2], arguments[3], arguments[4])
                response = 'success|'

            # Logout
            elif check_arguments(arguments, 1, 1, 'logout'):
                print(f'RECEIVED A LOGOUt REQUEST FROM THE CLIENT.')
                is_logged_in()
                response = f'{self.bank.logout()}'

            # Terminate client's connection
            elif check_arguments(arguments, 1, 1, 'terminate'):
                response = 'TERMINATING CONNECTION...'
                self.__keep_running_client = False
                if self.bank.current_customer:
                    Bank.logged_in.remove(self.bank.current_customer.email)
                self.bank.current_customer = None

            # Invalid requests
            else:
                raise ValueError('I could not understand that message.')

        except (ValueError, IndexError, IntegrityError, DataError) as ve:
            response = 'fail|' + str(ve)
            print('Something went wrong when processing the request.')

        self.send_message(response + '\n')
