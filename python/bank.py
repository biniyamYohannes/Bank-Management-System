# bank.py
"""Implements bank classes Bank, Customer, Account, and Transaction."""

import datetime
import mysql.connector

# Class Transaction
class Transaction:
    """Class that willl represent an account transaction (deposit/withdrawal). """
    def __init__(self, timestamp: datetime.datetime, amount: float):
        """Transaction constructor."""
        self.__timestamp = timestamp
        self.__amount = amount

    @property
    def timestamp(self):
        """Get transaction's timestamp attribute."""
        return self.__timestamp

    @property
    def amount(self):
        """Get transaction's amount attribute."""
        return self.__amount

    def __str__(self):
        """String representation of a Transaction object."""
        return f'Timestamp: {self.timestamp.year}/{self.timestamp.month}/{self.timestamp.day} - \
                            {self.timestamp.hour}: {self.timestamp.minute}:{self.timestamp.second} | \
                            Amount: {self.amount:.2f}'

# ##################################################################################################

# Class Account
class Account:
    """Class that will represent a customer's bank account."""
    def __init__(self, acc_id: str, acc_type: int, balance: float):
        """Account constructor."""
        self.__acc_id = acc_id
        self.__acc_type = acc_type
        self.__balance = balance
        self.__transactions = []

    def add_transaction(self, timestamp: datetime.datetime, amount: float):
        """Add a transaction to the account's list of transactions."""
        self.transactions.append(Transaction(timestamp, amount))

    @property
    def id(self):
        """Get Account id."""
        return self.__acc_id

    @property
    def type(self):
        """Get Account type."""
        return self.__acc_type

    @property
    def balance(self):
        """Get Account balance."""
        return self.__balance

    @property
    def transactions(self):
        return self.__transactions

    def __str__(self):
        """String representation of an Account object."""
        return f'ID: {self.acc_id} | Type: {self.acc_type} | Balance: {self.balance} | \
        Transactions: {len(self.transactions)}'

# ##################################################################################################

# Class Customer
class Customer:
    """Class that will represent a bank's customer."""
    def __new__(cls, email: str, password: str):
        """Create an instance of the customer class if there is a password match."""
        try:
            my_db = mysql.connector.connect(host=Bank.DB['hostname'], port=Bank.DB['port'],
                                            user=Bank.DB['user'], password=Bank.DB['passwd'],
                                            database=Bank.DB['db'])
            cursor = my_db.cursor()
            cursor.execute('SELECT cust_fname, cust_lname, cust_pass '
                           'FROM customer '
                           'WHERE cust_email = %s', (email,))
            for row in cursor.fetchall():
                if row[2] != password:
                    print('Password validation failed.')
                    return None
                else:
                    # create a new instance and set attributes on it
                    instance = super().__new__(cls)  # empty instance
                    instance.__fname = row[0]
                    instance.__lname = row[1]
                    instance.__email = email
                    instance.__accounts = []
                    return instance
        except:
            print('Customer:__new__: Something went wrong trying to reach the DB.')
            return None

    def load_all_accounts(self) -> list:
        """Load all customers from the database."""
        try:
            my_db = mysql.connector.connect(host=Bank.DB['hostname'],port=Bank.DB['port'],
                                            user=Bank.DB['user'],password=Bank.DB['passwd'],
                                            database=Bank.DB['db'])
            cursor = my_db.cursor()
            cursor.execute('SELECT acc_id, acc_type, acc_balance '
                           'FROM account '
                           'WHERE acc_email = %s', (self.email,))

            self.init_accounts()
            for row in cursor.fetchall():
                self.add_account(row[0], row[1], row[2])    # populate the accounts list with Account objects

            cursor.close()
            my_db.close()
            if not self.accounts:
                raise ValueError('Failed to retrieve account data for the requested customer.')
        except:
            print('Something went wrong when retrieving account ids from the database.')

    def get_account_ids(self):
        """Reload all accounts and return their account ids as strings."""
        self.load_all_accounts()
        return [str(account.id) for account in self.accounts]

    def add_account(self, id: str, type: int, balance: float):
        """Add an account to the list of customer's accounts."""
        self.accounts.append(Account(id, type, balance))

    def find_account(self, id: str):
        # Find account in the current account list by id, if not found return None
        for account in self.accounts:
            if account.id == id:
                return account
        return None

    @property
    def fname(self):
        """Get customer's first name."""
        return self.__fname

    @property
    def lname(self):
        """Get customer's last name."""
        return self.__lname

    @property
    def email(self):
        """Get customer's email."""
        return self.__email

    @property
    def accounts(self):
        """Get list of customer's accounts."""
        return self.__accounts

    @accounts.setter
    def accounts(self, account_list):
        self.__accounts = account_list

    def init_accounts(self):
        self.accounts = []

    def __str__(self):
        """String representation of a Customer object."""
        return f'{self.fname}|{self.lname}|{self.email}'

# ##################################################################################################

# Class Bank
class Bank:
    DB = {'hostname': 'localhost', 'port': 3306, 'user': 'root', 'passwd': 'password', 'db': 'test'}

    """Class that will represent a bank."""
    def __init__(self, name: str):
        """Bank constructor."""
        self.__name = name
        self.__customers = []
        self.__current_customer = None

    @property
    def name(self):
        """Get Bank's name attribute."""
        return self.__name

    @property
    def customers(self):
        """Get Bank's list of customers."""
        return self.__customers

    @property
    def current_customer(self):
        """Get the customer currently using the bank."""
        return self.__current_customer

    @current_customer.setter
    def current_customer(self, customer: Customer):
        self.__current_customer = customer

    def create_customer(self, email: str, password:str):
        """Create a new Customer object by retrieving their data from the db."""
        return Customer(email, password)

    def login(self, email: str, password: str):
        """Log in a user based on credentials."""
        if self.current_customer:
            raise ValueError('Another customer is already logged into the session.')
        else:
            self.current_customer = self.create_customer(email, password)
        if not self.current_customer:
            raise ValueError('Login failed. Failed to retrieve customer data for the provided email and password.')
        else:
            return f'success|{str(self.current_customer)}'

    def __str__(self):
        """String representation of a Bank object."""
        return f'Name: {self.name} | Customers: {len(self.customers)}'

# ##################################################################################################

# Test code
if __name__ == "__main__":
    pass

