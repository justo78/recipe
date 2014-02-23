package net.clareburt.util;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

/**
 * @author Justin Clareburt
 * @since 24/02/14
 */
public class DateUtilTest {

	@Test
	public void testGetCurrentDate() throws Exception {
		final Date currentDate = DateUtil.getCurrentDate();
		assertNotNull(currentDate);
	}

}
