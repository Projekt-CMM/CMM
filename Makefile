
JAVA_SRCS:=$(wildcard ./src/at/jku/ssw/cmm/compiler/*.java)

JAVA_CLASSES=$(subst ./src/,./bin/,$(JAVA_SRCS:.java=.class))

JFLAGS=-cp lib/rsyntaxtextarea.jar 

#https://stackoverflow.com/questions/14780321/java-cannot-find-symbol-when-compiling

JC=javac

.SUFFIXES: .java .class

.java.class: 
	echo "asdf"
	$(JC) $(JFLAGS) $*.java

all: CMM.jar

CMM.jar: $(JAVA_CLASSES)
	jar cf CMM.jar $(JAVA_CLASSES)

#default: build

#build: 
#	$(JAVA_CLASSES)

clean: 
	$(RM) -rv bin/*
	$(RM) -f CMM.jar
