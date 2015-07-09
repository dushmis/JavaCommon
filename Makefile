compile:
	find com -type f -name "*.java" -exec javac -g -cp .:* {} \;
