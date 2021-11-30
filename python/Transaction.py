

class Transaction:

    def __init__(self, date_time, amount):
        self.__dateTime = date_time
        self.__amount = float(amount)

    @property
    def amount(self):
        return self.__amount


