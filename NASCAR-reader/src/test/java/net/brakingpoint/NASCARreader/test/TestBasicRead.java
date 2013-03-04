package net.brakingpoint.NASCARreader.test;

import static org.junit.Assert.*;

import net.brakingpoint.FeedReader;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestBasicRead {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void firsttest() {
//		fail("Not yet implemented");
		FeedReader fr = new FeedReader();
		assert(fr.getServletName().equalsIgnoreCase(""));
	}

}
