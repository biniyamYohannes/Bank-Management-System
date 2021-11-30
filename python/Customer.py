class Customer:

    def __init__(self, fname, lname, email, ssn, dob):
        self.__fname = fname
        self.__lname = lname
        self.__email = email
        self.__ssn = ssn
        self.__dob = dob
        self.__accounts = []

    @property
    def accounts(self):
        return self.__accounts

    def create_account(self, account):
        self.__accounts.append(account)

    def delete_account(self, account):
        self.__accounts.remove(account)
