package tech.romashov.matchers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;

public class AssertWithTimeout {
    private static Duration timeout = Duration.ofSeconds(4);
    private static Duration delay = Duration.ofMillis(300);
    private static Log logger = LogFactory.getLog(AssertWithTimeout.class);

    public static <T> T assertThat(Callable<T> getter, Matcher<? super T> matcher) throws Throwable {
        return assertThat(getter, matcher, timeout, delay);
    }

    public static <T> T assertThat(Callable<T> getter, Matcher<? super T> matcher, Duration timeout, Duration delay) throws Throwable {
        T actual = waitAndGetActualValue(getter, matcher, timeout, delay);
        if (!matcher.matches(actual)) {
            Description description = new StringDescription();
            description.appendText("\nExpected: ")
                    .appendDescriptionOf(matcher)
                    .appendText("\n but: ");
            matcher.describeMismatch(actual, description);
            throw new AssertionError(description.toString());
        }
        return actual;
    }

    private static <T> T waitAndGetActualValue(Callable<T> getter, Matcher<? super T> matcher, Duration timeout, Duration delay) throws Throwable {
        Instant begin = Instant.now();
        T value = getter.call();
        while (Duration.between(begin, Instant.now()).toMillis() < timeout.toMillis() && !matcher.matches(value)) {
            try {
                Thread.sleep(delay.toMillis());
            } catch (InterruptedException e) {
                logger.warn(e);
            }
            value = getter.call();
        }
        return value;
    }
}
