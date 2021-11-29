# bank.py
"""Implements bank classes Bank, Customer, Account, and Transaction."""

import datetime

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
    def __init__(self, fname: str, lname: str, email: str):
        """Customer constructor."""
        self.__fname = fname
        self.__lname = lname
        self.__email = email
        self.__accounts = []

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


# ####################################################################################

# Class Bank
class Bank:
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




if __name__ == "__main__":
    transaction = Transaction(datetime.datetime.now(), 1000.)
    print(transaction)
    account = Account('000000', 1, 1000)
    print(account)
    account.add_transaction(datetime.datetime.now(), 1000.)
    print(account)
    customer = Customer('Biniyam', 'Yohannes', 'biniyam.yohannes@ucdenver.edu')
    print(customer)
    customer.add_account('000000', 1, 1000)
    print(customer)
    bank = Bank("Test Bank")
    print(bank)
    bank.addCustomer('Biniyam', 'Yohannes', 'biniyam.yohannes@ucdenver.edu')
    print(bank)
