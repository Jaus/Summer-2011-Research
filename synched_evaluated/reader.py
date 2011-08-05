import sys

if __name__ == '__main__':
    inFile = open(sys.argv[1], 'r')
    outFile = open(sys.argv[1][:-4] + '.out.txt', 'w')

    for line in inFile:
        print line,
        userInput = raw_input()
        print ''
        outFile.write(userInput + ' ' + line)

    inFile.close()
    outFile.close()
