MAIN=Tema1
SOURCES=$(wildcard *.java)
CLASSPATH=.:../lib/*

build:
	javac -cp $(CLASSPATH) $(SOURCES)

run:
	java -cp $(CLASSPATH) $(MAIN) $(ARGS) 2>&1 | tee /tmp/debug_output.txt
# 	java -cp $(CLASSPATH) $(MAIN) $(ARGS)

clean:
	rm -f *.class
