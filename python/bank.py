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
        return f'Timestamp: {self.timestamp.year}/{self.timestamp.month}/{self.timestamp.day} - {self.timestamp.hour}:{self.timestamp.minute}:{self.timestamp.second} |' \
               f' Amount: {self.amount:.2f}'

    # def load(self):
    #     """Load a transaction from the database."""
    #     my_db = mysql.connector.connect(host=Bank.DB['hostname'], port=Bank.DB['port'], user=Bank.DB['user'], password=Bank.DB['passwd'], database=Bank.DB['db'])
    #     cursor = my_db.cursor()
    #     cursor.execute('SELECT trans_id, trans_account_id, trans_amount, trans_created WHERE trans_id = %s', ())
    #
    # @staticmethod
    # def store():
    #     my_db = mysql.connector.connect(host=Bank.DB['hostname'], port=Bank.DB['port'], user=Bank.DB['user'], password=Bank.DB['passwd'], database=Bank.DB['db'])
    #     cursor = my_db.cursor()
    #     cursor.execute('SELECT trans_id, trans_account_id, trans_amount, trans_created WHERE trans_id = %s', ())


# ####################################################################################

# Class Account
class Account:
    """Class that will represent a customer's bank account."""
    def __init__(self, id: str, type: int, balance: float):
        """Account constructor."""
        self.__id = id
        self.__type = type
        self.__balance = balance
        self.__transactions = []

    def add_transaction(self, timestamp: datetime.datetime, amount: float):
        """Add a transaction to the account's list of transactions."""
        self.transactions.append(Transaction(timestamp, amount))

    @property
    def id(self):
        """Get Account id."""
        return self.__id

    @property
    def type(self):
        """Get Account type."""
        return self.__type

    @property
    def balance(self):
        """Get Account balance."""
        return self.__balance

    @property
    def transactions(self):
        return self.__transactions

    def __str__(self):
        """String representation of an Account object."""
        return f'ID: {self.id} | Type: {self.type} | Balance: {self.balance} | Transactions: {len(self.transactions)}'

# ####################################################################################

# Class Customer
class Customer:
    """Class that will represent a bank's customer."""
    def __new__(cls, email: str, password: str):
        """Create an instance of the customer class if there is a password match."""
        try:
            my_db = mysql.connector.connect(host=Bank.DB['hostname'], port=Bank.DB['port'], user=Bank.DB['user'], password=Bank.DB['passwd'], database=Bank.DB['db'])
            cursor = my_db.cursor()
            cursor.execute('SELECT cust_fname, cust_lname, cust_pass FROM customer WHERE cust_email = %s', (email,))
            for row in cursor.fetchall():
                if row[2] != password:
                    print('Password validation failed.')
                    return ''
                else:
                    # create a new instance and set attributes on it
                    instance = super().__new__(cls)  # empty instance
                    instance.__fname = row[0]
                    instance.__lname = row[1]
                    instance.__email = email
                    instance.__accounts = []
                    return instance
        except:
            print('Something went wrong trying to reach the DB.')
            return ''

    # def __init__(self, email: str, password: str):
    #     """Customer constructor."""
    #     self.__email = email
    #     self.__fname = None
    #     self.__lname = None
    #     self.__accounts = []
    #     self.load(password)

    def add_account(self, id: str, type: int, balance: float):
        """Add an account to the list of customer's accounts."""
        self.accounts.append(Account(id, type, balance))

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

    def __str__(self):
        """String representation of a Customer object."""
        return f'Name: {self.fname} {self.lname} | Email {self.email} | Accounts: {len(self.accounts)}'

    def load(self, password: str):
        """Load a transaction from the database."""
        try:
            my_db = mysql.connector.connect(host=Bank.DB['hostname'], port=Bank.DB['port'], user=Bank.DB['user'], password=Bank.DB['passwd'], database=Bank.DB['db'])
            cursor = my_db.cursor()
            cursor.execute('SELECT cust_fname, cust_lname, cust_pass FROM customer WHERE cust_email = %s', (self.email,))
            for row in cursor.fetchall():
                if row[2] == password:
                    self.__fname = row[0]
                    self.__lname = row[1]
            cursor.close()
            my_db.close()
        except:
            print('Password validation failed.')

    # @staticmethod
    # def store():
    #     my_db = mysql.connector.connect(host=Bank.DB['hostname'], port=Bank.DB['port'], user=Bank.DB['user'], password=Bank.DB['passwd'], database=Bank.DB['db'])
    #     cursor = my_db.cursor()
    #     cursor.execute('SELECT trans_id, trans_account_id, trans_amount, trans_created WHERE trans_id = %s', (self.email))


# ####################################################################################

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

    def addCustomer(self, fname: str, lname: str, email: str):
        """Add a customer to the bank's database."""
        self.customers.append(Customer(fname, lname, email))

    def __str__(self):
        """String representation of a Bank object."""
        return f'Name: {self.name} | Customers: {len(self.customers)}'

# ####################################################################################

# Test code
if __name__ == "__main__":
    customer1 = Customer('biniyam.yohannes@ucdenver.edu', 'password')
    print(customer1) # Name: Biniyam Yohannes | Email biniyam.yohannes@ucdenver.edu | Accounts: 0
    customer2 = Customer('biniyam.yohannes@ucdenver.edu', 'invalid_password')
    print(customer2) # Password validation failed.
    if not customer2:
        print('Customer 2 returned an empty string')