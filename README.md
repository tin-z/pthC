# PthC
Framework analysis assembly code by Intel processor trace


what?
=====

PthC is a framework that gives tools to analysis executable file.
It traces cpu instructions by [Intel Processor Trace](https://software.intel.com/en-us/blogs/2013/09/18/processor-tracing), and then rebuild the Control flow Executed (CFE).
After previous process, it gives the code coverage.

The framework can do more than this, in fact it can compare the flow executed from two differ version of the same program, see the [Manuale d'uso pthC](Manuale d'uso pthC).

See the [Manuale d'uso pthC](Manuale d'uso pthC) file for an overview of all tests and links to further information
about the issues.


prereq
=======

	-Cpu Intel >= 5th gen
	-Scala version >= 2.11.0
	-package scala.util.parsing.combinator
	-Linux kernel >= 4.11.x
	-perf record


install
=======

type:

```
./install
```

If you want just the code coverage, type:

```
scala TakeCare < -c "command name"> < -p "command path" >  [ -P "pid to trace" ] [ -o name_file_log ]
```

If you want to compare two diff version ... and so on, type:

```
./script/list_package.sh <command name>
```
next follow on the instruction on the screen


author
======

pthC is developed and maintained by me, if you need more info contact me.
