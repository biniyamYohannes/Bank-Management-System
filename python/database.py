import mysql.connector

DB = {'hostname': 'localhost', 'port': 3306, 'user': 'root', 'passwd': 'password', 'db': 'test'}
my_db = mysql.connector.connect(host=DB['hostname'], port=DB['port'], user=DB['user'], password=DB['passwd'], database=DB['db'])
cursor = my_db.cursor()

def insert_customer(customer: tuple) -> None:
    if len(customer) != 4:
        raise ValueError("Number of arguments doesn't match number of table columns (fname, lname, email, password).")
    else:
        values = ", ".join(f"'{v}'" for v in customer)
        print("INSERT INTO customer VALUES (%s);" %values)
        cursor.execute("INSERT INTO customer VALUES (%s);" %values)
        my_db.commit()

customer = ('Biniyam', 'Yohannes', 'biniyam.yohannes@ucdenver.edu', 'password')
# print("INSERT INTO customer VALUES %s;" % (customer,))
insert_customer(customer)

