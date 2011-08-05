import sys, os, random

f = open(sys.argv[1])
jiang = []
jiangDict = {}

for line in f:
	path = line.split("->")[0]
	jiang.append(path)
	jiangDict[path] = line
	
f.close()
f = open(sys.argv[2])
novel = []
novelDict = {}

for line in f:
	path = line.split("->")[0]
	novel.append(path)
	novelDict[path] = line

f.close()	
f = open(sys.argv[3])
hybrid = []
hybridDict = {}

for line in f:
	path = line.split("->")[0]
	hybrid.append(path)
	hybridDict[path] = line
	
intersect = list(set.intersection(set(jiang), set(novel), set(hybrid)))

if len(intersect)<=300:
	print "You Suck Juas!"
	sys.exit(-1)
	
sample = []
for i in range(0,300):
	x = random.choice(intersect)
	sample.append(x)
	intersect.remove(x)
	
sample.sort()
	
j = open("jaingSample.txt", 'w')
n = open("novelSample.txt", 'w')
h = open("hybridSample.txt", 'w')

for i in range(0, 300):
	s = sample[i]
	j.write("%s" % str(jiangDict[s]))
	n.write("%s" % str(novelDict[s]))
	h.write("%s" % str(hybridDict[s]))
	
j.close()
n.close()
h.close()

print "All Done"
	