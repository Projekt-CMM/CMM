CMM
===

java based compiler for a minimal c-dialect

[![Build Status](https://travis-ci.org/Projekt-CMM/CMM.svg?branch=master)](https://travis-ci.org/Projekt-CMM/CMM)

### Build project

##### Install required packages on Debian/Ubuntu

```
sudo apt-get install ant coco-java
```

##### using ant to build project

```
ant build
```

### Overview

This project is an IDE for a minimal version of C.
Currently, the user interface shows the program steps, operations and varibables like a debugger. However, we are planing to
design a graphical user interface which is able to explain simple program flows and processes to programming beginners in a
demonstrative and understandable way.

The program is based on three components:

1) A compiler which generates an abstract syntax tree  
2) An interpreter which builds up a call stack and runs the AST step by step  
3) The GUI which enables editing the source code and running it  
