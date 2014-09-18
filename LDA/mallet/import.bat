SETLOCAL
set MALLET_HOME = %PATH%;%CD%
bin\mallet import-dir --input ..\..\DataGeneration\mergedTagCatProfile --output data.mallet --keep-sequence --remove-stopwords
ENDLOCAL