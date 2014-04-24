@echo off
rem http://technet.microsoft.com/en-us/library/bb490954.aspx

set SOFTWARE_LIB=C:\Users\shandy3\Software
REM set ANT_BIN=%SOFTWARE_LIB%\eclipse-juno\plugins\org.apache.ant_1.8.3.v20120321-1730\bin
set CURL_BIN=%SOFTWARE_LIB%\curl-7.33.0
REM set CVS_BIN=%SOFTWARE_LIB%\cvs-1.11.22
REM set DIFF_BIN=%SOFTWARE_LIB%\diffutils-2.8.7\bin
set GRAILS_BIN=%SOFTWARE_LIB%\grails-2.3.7\bin
REM set GREP_BIN=%SOFTWARE_LIB%\grep-2.5.4\bin
set PUTTY_BIN=C:\Program Files (x86)\PuTTY
REM set SOAPUI_BIN=%SOFTWARE_LIB%\StandaloneExe\soapui-4.5.2\bin
REM set SVN_BIN=C:\Program Files\TortoiseSVN\bin
set SVN_BIN=C:\Program Files\SlikSvn\bin
REM set UNISON_BIN=%SOFTWARE_LIB%\unison-2.32.52
REM set WGET_BIN=%SOFTWARE_LIB%\wget-1.11.4\bin

set JAVA_HOME=C:\Program Files (x86)\Java\jre7
set JAVA_BIN=%JAVA_HOME%\bin

set JDK_HOME=C:\Program Files (x86)\Java\jdk1.7.0_55
set JDK_BIN=%JDK_HOME%\bin

set JAVA64_HOME=C:\Program Files\Java\jre7
set JAVA64_BIN=%JAVA64_HOME%\bin

set JDK64_HOME=C:\Program Files\Java\jdk1.7.0_25
set JDK64_BIN=%JDK64_HOME%\bin

REM set M2_HOME=%SOFTWARE_LIB%\apache-maven-3.0.4
REM set M2=%M2_HOME%\bin

set PYTHON_HOME=C:\Python27
set PYTHON_SCRIPTS=%PYTHON_HOME%\Scripts

set ORIG_PATH=%PATH%
REM set PATH=%ANT_BIN%;%CVS_BIN%;%DIFF_BIN%;%GREP_BIN%;%PUTTY_BIN%;%SOAPUI_BIN%;%UNISON_BIN%;%WGET_BIN%;%JAVA_BIN%;%M2%;%PYTHON_HOME%;%PYTHON_SCRIPTS%;%PATH%
set PATH=%CURL_BIN%;%GRAILS_BIN%;%PUTTY_BIN%;%SVN_BIN%;%JAVA_BIN%;%PYTHON_HOME%;%PYTHON_SCRIPTS%;%PATH%

set HOME=%HOMEDRIVE%%HOMEPATH%

doskey cdhome=cd %HOME%
doskey cddesk=cd %HOME%\Desktop
doskey cddocs=cd %HOME%\Documents
doskey cdprog32=cd %PROGRAMFILES(X86)%
doskey cdprog64=cd %PROGRAMFILES%
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
doskey touch=echo. $G $*

doskey grails231=%SOFTWARE_LIB%\grails-2.3.1\bin\grails $*
doskey grails234=%SOFTWARE_LIB%\grails-2.3.4\bin\grails $*

REM set CVS_RSH=%PUTTY_BIN%\PLINK.EXE
