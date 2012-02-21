@echo off
rem http://technet.microsoft.com/en-us/library/bb490954.aspx

set SOFTWARE_LIB=C:\Users\handy\Software
set ANT_BIN=%SOFTWARE_LIB%\eclipse-helios\plugins\org.apache.ant_1.7.1.v20100518-1145\bin
set CVS_BIN=%SOFTWARE_LIB%\cvs-1.11.22
set DIFF_BIN=%SOFTWARE_LIB%\diffutils-2.8.7\bin
set GREP_BIN=%SOFTWARE_LIB%\grep-2.5.4\bin
set PUTTY_BIN=%SOFTWARE_LIB%\putty-0.61
set WGET_BIN=%SOFTWARE_LIB%\wget-1.11.4\bin

set JAVA_HOME=C:\Program Files\Java\jdk1.6.0_25
set JAVA_BIN=%JAVA_HOME%\bin
set PYTHON_BIN=C:\Python27

set ORIG_PATH=%PATH%
set PATH=%ANT_BIN%;%CVS_BIN%;%DIFF_BIN%;%GREP_BIN%;%PUTTY_BIN%;%WGET_BIN%;%JAVA_BIN%;%PYTHON_BIN%;%PATH%

set HOME=%HOMEDRIVE%%HOMEPATH%
set PRDS=C:\PRDS

doskey cdhome=cd %HOME%
doskey cddesk=cd %HOME%\Desktop
doskey cddocs=cd %HOME%\Documents
doskey cdprog32=cd %PROGRAMFILES(X86)%
doskey cdprog64=cd %PROGRAMFILES%
doskey cdprds=cd %PRDS%
doskey cdbuild=cd %PRDS%\Build
doskey cdsoft=cd %SOFTWARE_LIB%

doskey cat=type $*
doskey clear=cls
doskey cp=copy $*
doskey wdiff=fc $*
doskey wgrep=findstr $*
doskey ls=dir $*
doskey mv=move $*
doskey pwd=echo %CD%
doskey rm=del $*

set CVS_RSH=%PRDS%\Build\PLINK.EXE

