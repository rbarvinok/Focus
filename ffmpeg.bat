%CD%/ConGoPro/Programs/ffmpeg/bin/ffmpeg.exe -y -i   D:\VideoGoPro/GH010693.MP4 -codec copy -map 0:3 -f rawvideo %CD%/ConGoPro/transit/GH010693.bin 
%CD%/ConGoPro/Programs/gpmd2csv/gpmd2csv.exe  -i  %CD%/ConGoPro/transit/GH010693.bin  -o %CD%/ConGoPro/out/GH010693.csv 
exit