from collections import Counter
import numpy as np

c = Counter()
data_path = "/home/allen/Code/CalWordSuffix/english-wordlists/TOEFL_delete_CET4+6.txt"
with open(data_path, 'rt') as f:
    for line in f:
        c.update({line.split(' ')[0]})




        


