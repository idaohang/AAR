@echo off
setlocal enabledelayedexpansion
rem **************************************************************************
rem * Article Recommender System Benchmarking Script                         *
rem * by James Pyke 05090946                                                 *
rem * semester 2, 2013                                                       *
rem **************************************************************************

rem **************************************************************************
rem * IMPORTANT!                                                             *
rem * Adjust the variables below as required when installing on a new system *
rem **************************************************************************
set ARSdbHost=WIN7_X86_ARS
set ARSdbName=capstone
set ARSdbUser=qut
set ARSdbPass=vres2012


title 1/2: Testing AVERAGE
echo ==============================================================================
echo !time! 1/2: Testing AVERAGE
echo ------------------------------------------------------------------------------
call java -jar "bench\dist\bench.jar" "%ARSdbHost%" "%ARSdbName%" "%ARSdbUser%" "%ARSdbPass%" "%~dp0benchresults avg.csv" avg
echo.

title 2/2: Testing SUM
echo ==============================================================================
echo !time! 2/2: Testing SUM
echo ------------------------------------------------------------------------------
java -jar "bench\dist\bench.jar" "%ARSdbHost%" "%ARSdbName%" "%ARSdbUser%" "%ARSdbPass%" "%~dp0benchresults sum.csv" sum
echo.

title FINISHED
echo ==============================================================================
echo !time! FINISHED!
echo ------------------------------------------------------------------------------
echo.
pause