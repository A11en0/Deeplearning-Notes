from collections import Counter
from pprint import pprint

# import numpy as np              
# Counter(['a', 'b', 'c', 'a', 'b', 'b'])  

# import csv                      
# with open(data_path, encoding = 'utf-8') as f:
#     data = np.loadtxt(f, delimiter = " ")
#     print(data[:5])

suffix = ["auto",  "bene", "bi", "ing", "ed", "be"]

#c = Counter()
c = {}
for s in suffix:
    c[s] = [0, []]

# data_path = "/home/allen/Code/english-wordlists/TOEFL_delete_CET4+6.txt"
data_path = "/usr/share/dict/words"
with open(data_path, 'rt') as f:
    for line in f:
        line = line.rstrip('\n')
        for s in suffix:
            if line.rstrip().lower().endswith(s):
                c[s][0] += 1
                c[s][1].append(line)

                
# print("Most Common: ")
def mostCommon(limit=5):
    cnt = 1
    for i in sorted(c.items(), key=lambda c: c[1], reverse=True):
        if cnt > limit:
            break
        yield i[0], i[1][0], i[1][1]
        cnt += 1
        
# for i in mostCommon(5):
#    print("Suffix {} has {} items: \n".format(i[0], i[1]))

def getSuffix():
    suffixs = []
    with open("suffix.txt", "r+") as f:
        for line in f:
            line = line.split('.')[0].split("-")[0]
            suffixs.append(line)
    with open("prefix.txt", "r+") as f:
        for line in f:
            word = ""
            line  = line.split(' ')[0].split('-')[1]
            suffixs.append(word)
    return suffixs
    
print(getSuffix())

