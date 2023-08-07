SRCDIR = src
BINDIR = bin
DOCDIR = doc

JC = javac
JFLAGS = -g -d $(BINDIR) -cp $(BINDIR) -cp $(SRCDIR)

vpath %.java $(SRCDIR)
vpath %.class $(BINDIR)

.SUFFIXES: .java .class

.java.class:	
	$(JC) $(JFLAGS) $<
	
CLASSES = TerrainArea.class \
	Search.class \
	MonteCarloMinimization.class \
	
classes: $(CLASSES:.java=.class)

doc: $(BINDIR)
	javadoc -d $(DOCDIR) $(SRCDIR)/*.java
	
clean:
	$(RM) $(BINDIR)/*.class
	$(RM) $(SRC)/*.java~
	$(RM) $(DOCDIR)/*
	
run: $(SRCDIR)
	java -cp $(BINDIR) MonteCarloMinimization $(rows) $(columns) $(xmin) $(xmax) $(ymin) $(ymax) $(searchDensity)