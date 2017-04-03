
set storepath=wzc25151.jks
set storepass=123499
set keypass=123499
set alias=wzc25151
set sevenzip=7zip_win64.exe
set zipalign=E:\software\sdk\build-tools\25.0.2\zipalign.exe

java -jar AndResGuard-cli-1.1.16.jar input.apk -config config.xml -out output.apk -signature "%storepath%" "%storepass%" "%keypass%" "%alias%" -7zip "sevenzip" -zipalign "%zipalign%"
pause
