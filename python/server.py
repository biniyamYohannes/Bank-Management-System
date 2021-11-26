# import socket
#
# HOST = '127.0.0.1'  # Standard loopback interface address (localhost)
# PORT = 65432        # Port to listen on (non-privileged ports are > 1023)
#
# with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
#     s.bind((HOST, PORT))
#     s.listen()
#     conn, addr = s.accept()
#     with conn:
#         print('Connected by', addr)
#         while True:
#             data = conn.recv(1024)
#             # if not data:
#             #     break
#             conn.sendall(data)

from multiprocessing import Process, cpu_count
from threading import Thread
import time
import os

def get_number_cores():
    return cpu_count()


class ProcessUseCPU(Process):
    def run(self):
        start = time.time()
        for i in range(200_000_000):
            pass
        end = time.time()
        print(f"""ProcessId:[{os.getpid():5}]...Done in {end-start:10.5f} sec.""")
        return 0


class ThreadUseCPU(Thread):
    def run(self):
        start = time.time()
        for i in range(200_000_000):
            pass
        end = time.time()
        print(f"""Thread Run...Done in {end-start:10.5f} sec.""")

number_cores = get_number_cores()
print(f"""\nNumber of Cores in the computer is {number_cores}\n""")

for cores in [1, number_cores, 10]:

    print(f"""{"-"*50}\n{f"{cores} Threads":^50}\n{"-"*50}""")
    start = time.time()
    threads = [ThreadUseCPU() for f in range(cores)]
    for t in threads:        t.start()
    for t in threads:        t.join()
    end = time.time()
    print(f"""{cores} Threads of work took {end-start:10.5f} sec.\n""")

    print(f"""{"-"*50}\n{f"{cores} Processes":^50}\n{"-"*50}""")
    start = time.time()
    processes = [ProcessUseCPU() for f in range(cores)]
    for p in processes:        p.start()
    for p in processes:        p.join()
    end = time.time()
    print(f"""{cores} Processes of work took {end-start:10.5f} sec.\n""")