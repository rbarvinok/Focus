D:/Focus/ConGoPro/Programs/ffmpeg/bin/ffmpeg.exe -y -i   D:\VideoGoPro/GH030436.MP4 -codec copy -map 0:3 -f rawvideo D:/Focus/ConGoPro/transit/GH030436.bin 
D:/Focus/ConGoPro/Programs/gpmd2csv/gpmd2csv.exe  -i  D:/Focus/ConGoPro/transit/GH030436.bin  -o D:/Focus/ConGoPro/out/GH030436.csv 
exit