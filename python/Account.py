import Transaction
import time


class Account:

    def __init__(self, account_id, account_type, balance):
        self.__account_id = account_id
        self.__account_type = account_type
        self.__balance = balance
        self.__transactions = []

    @property
    def account_id(self):
        return self.__account_id

    @property
    def account_type(self):
        return self.__account_type

    @property
    def balance(self):
        return self.__balance

    def deposit(self, amount):
        self.__balance += amount
        self.__transactions.append(Transaction(time.ctime(), amount))

    def withdrawl(self, amount):
        self.__balance -= amount
        self.__transactions.append(Transaction(time.ctime(), amount))


class Checking(Account):
    def __init__(self, account_id, account_type, balance):
        super(Account, self).__init__(account_id, account_type, balance)


class Saving(Account):
    def __init__(self, account_id, account_type, balance, interest_rate):
        super(Account, self).__init__(account_id, account_type, balance)
        self.__interest_rate = interest_rate


class creditCard(Account):
    def __init__(self, account_id, account_type, balance, credit_limit):
        super(Account, self).__init__(account_id, account_type, balance)
        self.__credit_limit = credit_limit

    def make_payment(self, amount):
        self.deposit(amount)
