#pt checker moodle 

# MAIN=Tema1
# SOURCES=$(wildcard *.java)
# CLASSPATH=$(CURDIR):$(CURDIR)/lib/*

# build:
# 	javac -cp $(CLASSPATH) $(SOURCES)

# run:
# 	java -cp $(CLASSPATH) $(MAIN) $(ARGS)
# clean:
# 	rm -f *.class

# lcoal pt mine

MAIN=Tema1
SOURCES=$(wildcard *.java)
CLASSPATH=.:../lib/*

build:
	javac -cp $(CLASSPATH) $(SOURCES)

run:
	java -cp $(CLASSPATH) $(MAIN) $(ARGS) 2>&1 | tee /tmp/debug_output.txt

clean:
	rm -f *.class
