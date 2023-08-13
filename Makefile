JAVAC=/usr/bin/javac
JAVA=/usr/bin/java
.SUFFIXES: .java .class


SRCDIR=src/MonteCarloMini
BINDIR=bin
.java.class:
	$(JAVAC) $< 

$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) -sourcepath $(SRCDIR) $<


CLASSES=CommonThreads.class Direction.class TerrainArea.class SearchParallel.class MonteCarloMinimization.class MonteCarloMinimizationParallel.class
MAIN1 = MonteCarloMinimization.class
MAIN2 = MonteCarloMinimizationParallel.class

CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)

default: $(CLASS_FILES)
clean:
	rm $(BINDIR)/MonteCarloMini/*.class

runSerial: default
	java -cp $(BINDIR) MonteCarloMini.MonteCarloMinimization $(ARGS)

runParallel: default
	java -cp $(BINDIR) MonteCarloMini.MonteCarloMinimizationParallel $(ARGS)