GRADLE := exec ./gradlew

default:: clean

clean::
	$(GRADLE) clean

test::
	$(GRADLE) cleanCheck check

matrix-test::
	exec ./bin/matrix-test
