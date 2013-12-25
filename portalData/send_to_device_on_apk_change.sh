FILE=../app/build/apk/app-debug-unaligned.apk
echo Watching $FILE
while inotifywait -e close_write $FILE
	do
	echo "-------------------------------"
	ls -lah $FILE
	md5sum $FILE

	adb install -r $FILE && \
	adb shell am start -n com.marcosdiez.ingressportalnavigator/.MainActivity
done

