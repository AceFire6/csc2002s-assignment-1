JFLAGS = -g
JC = javac
FILES_REQ = text_files/transmit.txt text_files/receive.txt
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	SequentialCorrelate.java \
	ParallelCorrelate.java \
	CorrelateArray.java \
	FindMax.java
	
default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
	
run:
	java SequentialCorrelate $(FILES_REQ)

run-parallel:
	java ParallelCorrelate $(FILES_REQ)
