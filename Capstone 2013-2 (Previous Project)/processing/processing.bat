@echo off
setlocal enabledelayedexpansion
rem **************************************************************************
rem * Article Recommender System Pre-Processing Script                       *
rem * by James Pyke 05090946                                                 *
rem * semester 2, 2013                                                       *
rem **************************************************************************

rem **************************************************************************
rem * IMPORTANT!                                                             *
rem * Adjust the variables below as required when installing on a new system *
rem **************************************************************************
set ARSdbHost=SEF-EEC-066935
set ARSdbName=capstone
set ARSdbUser=qut
set ARSdbPass=vres2012

set ARSdataPath=%~dp0data
set ARSprogPath=%~dp0programs

rem **************************************************************************
rem * 1/8: DB -> MALLET conversion                                           *
rem **************************************************************************
mkdir "%ARSdataPath%"
mkdir "%ARSdataPath%\01txt"
title 1/8: DUMP DATABASE
echo ==============================================================================
echo !time! 1/8: DUMP DATABASE
echo ------------------------------------------------------------------------------
java -jar "%ARSprogPath%\DB2Mallet\dist\DB2Mallet.jar" "%ARSdbHost%" "%ARSdbName%" "%ARSdbUser%" "%ARSdbPass%" "%ARSdataPath%\01txt"
echo.

rem **************************************************************************
rem * 2/8: MALLET part 1                                                     *
rem **************************************************************************
mkdir "%ARSdataPath%\02mallet"
set MALLET_HOME=%ARSprogPath%\mallet
for /d %%a in ("%ARSdataPath%\01txt\*") do (
	title 2/8: %%~na
	echo ==============================================================================
	echo !time! 2/8: %%~na
	echo ------------------------------------------------------------------------------
	call "%ARSprogPath%\mallet\bin\mallet" import-dir --input "%%a" --output "%ARSdataPath%\02mallet\%%~na.mallet" --keep-sequence --remove-stopwords
	echo.
)

rem **************************************************************************
rem * 3/8: MALLET part 2                                                     *
rem **************************************************************************
mkdir "%ARSdataPath%\03stategz"
mkdir "%ARSdataPath%\03doctopics"
for %%a in ("%ARSdataPath%\02mallet\*.mallet") do (
	title 3/8: %%~na
	echo ==============================================================================
	echo !time! 3/8: %%~na
	echo ------------------------------------------------------------------------------
	call "%ARSprogPath%\mallet\bin\mallet" train-topics --input "%%a" --num-topics 20 --output-state "%ARSdataPath%\03stategz\%%~na.txt.gz" --output-doc-topics "data\03doctopics\%%~na.txt"
	echo.
)

rem **************************************************************************
rem * 4/8: unzip MALLET output                                               *
rem **************************************************************************
mkdir "%ARSdataPath%\04state"
title 4/8: unzipping MALLET output
echo ==============================================================================
echo !time! 4/8: unzipping MALLET output
echo ------------------------------------------------------------------------------
for %%a in ("%ARSdataPath%\03stategz\*.gz") do (
	call "%ARSprogPath%\7zip\7za.exe" e "%%a" -o"%ARSdataPath%\04state"
)

rem **************************************************************************
rem * 5/8: MALLET -> ARM conversion                                          *
rem **************************************************************************
mkdir "%ARSdataPath%\05pdre"
for /d %%a in ("%ARSdataPath%\01txt\*") do (
	title 5/8: %%~na
	echo ==============================================================================
	echo !time! 5/8: %%~na
	echo ------------------------------------------------------------------------------
	mkdir "%ARSdataPath%\05pdre\%%~na"
	call java -jar "%ARSprogPath%\PatternDocRepre\dist\PatternDocRepre.jar" "%%a" "%ARSdataPath%\04state\%%~na.txt" "%ARSdataPath%\05pdre\%%~na"
	echo.
)

rem **************************************************************************
rem * 6/8: ARM                                                               *
rem **************************************************************************
mkdir "%ARSdataPath%\06arm"
cd "%ARSprogPath%\ARM\Association rule mining\PS-System\Build"
for /d %%a in ("%ARSdataPath%\05pdre\*") do (
	mkdir "%ARSdataPath%\06arm\%%~na"
	for %%b in ("%%a\*.data") do (
		title 6/8: %%~na %%~nb
		echo ==============================================================================
		echo !time! 6/8: %%~na %%~nb
		echo ------------------------------------------------------------------------------
		mkdir "%ARSdataPath%\06arm\%%~na\%%~nb"
		call java -Xms64M -Xmx1024M ARM "%%b" 0.1 0.5 T F F F 1 2 2 "%ARSdataPath%\06arm\%%~na\%%~nb"
		echo.
	)
)
cd %~dp0

rem **************************************************************************
rem * 7/8: find matches, calculate values, insert to DB                      *
rem **************************************************************************
for /d %%a in ("%ARSdataPath%\06arm\*") do (
	for /d %%b in ("%%a\*") do (
		title %%~na %%~nb
		title 7/8: %%~na %%~nb
		echo ==============================================================================
		echo !time! 7/8: %%~na %%~nb
		echo ------------------------------------------------------------------------------
		java -jar "%ARSprogPath%\PatternCounter\dist\PatternCounter.jar" "%ARSdbHost%" "%ARSdbName%" "%ARSdbUser%" "%ARSdbPass%" "%%b" "%ARSdataPath%\01txt\%%~na" "%ARSdataPath%\03doctopics\%%~na.txt" "%%~na" "%%~nb"
		echo.
	)
)

rem **************************************************************************
rem * Final cleanup & exit                                                   *
rem **************************************************************************
title 8/8: final cleanup
echo ==============================================================================
echo !time! 8/8: final cleanup
echo ------------------------------------------------------------------------------
rmdir /s/q "%ARSdataPath%"
set ARSdbHost=
set ARSdbName=
set ARSdbUser=
set ARSdbPass=
set ARSdataPath=
set ARSprogPath=
echo.

title FINISHED
echo ==============================================================================
echo !time! FINISHED!
echo ------------------------------------------------------------------------------
echo.
pause