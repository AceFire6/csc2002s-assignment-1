JFLAGS = -g
JC = javac
FILES_REQ = text_files/small/transmit.txt text_files/small/receive.txt
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
	
run-seq:
	java SequentialCorrelate $(FILES_REQ)

run-parallel:
	java ParallelCorrelate $(FILES_REQ)

run-full-test-suite:
	java SequentialCorrelate text_files/small/transmit.txt text_files/small/receive.txt
	java ParallelCorrelate text_files/small/transmit.txt text_files/small/receive.txt
	if [ ! -d small-run ]; then\
		mkdir small-run;\
	fi
	mv *.out small-run/;
	java SequentialCorrelate text_files/medium/transmit.txt text_files/medium/receive.txt
	java ParallelCorrelate text_files/medium/transmit.txt text_files/medium/receive.txt
	if [ ! -d medium-run ]; then\
		mkdir medium-run;\
	fi
	mv *.out medium-run/;
	java SequentialCorrelate text_files/large/transmit.txt text_files/large/receive.txt
	java ParallelCorrelate text_files/large/transmit.txt text_files/large/receive.txt
	if [ ! -d large-run ]; then\
		mkdir large-run;\
	fi
	mv *.out large-run/;
