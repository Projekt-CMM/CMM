
JAVA_SRCS:=$(wildcard ./src/at/jku/ssw/cmm/*.java) \
	$(wildcard ./src/at/jku/ssw/cmm/compiler/*.java) \
	$(wildcard ./src/at/jku/ssw/cmm/debugger/*.java) \
	$(wildcard ./src/at/jku/ssw/cmm/gui/*.java) \
	$(wildcard ./src/at/jku/ssw/cmm/gui/datastruct/*.java) \
	$(wildcard ./src/at/jku/ssw/cmm/gui/event/*.java) \
	$(wildcard ./src/at/jku/ssw/cmm/gui/event/panel/*.java) \
	$(wildcard ./src/at/jku/ssw/cmm/gui/exception/*.java) \
	$(wildcard ./src/at/jku/ssw/cmm/gui/file/*.java) \
	$(wildcard ./src/at/jku/ssw/cmm/gui/include/*.java) \
	$(wildcard ./src/at/jku/ssw/cmm/gui/init/*.java) \
	$(wildcard ./src/at/jku/ssw/cmm/gui/interpreter/*.java) \
	$(wildcard ./src/at/jku/ssw/cmm/gui/mod/*.java) \
	$(wildcard ./src/at/jku/ssw/cmm/gui/utils/*.java) \
	$(wildcard ./src/at/jku/ssw/cmm/interpreter/*.java) \
	$(wildcard ./src/at/jku/ssw/cmm/interpreter/excpetions/*.java) \
	$(wildcard ./src/at/jku/ssw/cmm/interpreter/memory/*.java)

JAVA_CLASSES=$(subst ./src/,./bin/,$(JAVA_SRCS:.java=.class))

JFLAGS=-cp lib/rsyntaxtextarea.jar 

#https://stackoverflow.com/questions/14780321/java-cannot-find-symbol-when-compiling

JC=javac

.SUFFIXES: .java .class

.java.class: 
	$(JC) $(JFLAGS) $*.java

all: CMM.jar

CMM.jar: build
	jar cf CMM.jar $(JAVA_CLASSES)

coco: src/at/jku/ssw/cmm/compiler/CMM.atg
	cocoj src/at/jku/ssw/cmm/compiler/CMM.atg

build: coco $(JAVA_CLASSES)

clean: 
	$(RM) -rv bin/*
	$(RM) -f CMM.jar
	$(RM) -f src/at/jku/ssw/cmm/compiler/Parser.java
	$(RM) -f src/at/jku/ssw/cmm/compiler/Scanner.java
