import itertools
import sys

words = sys.argv[1:]
permutations = list(itertools.permutations(words))

print(permutations)
