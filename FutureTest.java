package bgu.spl.mics;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FutureTest<T> {
	
	private Future<String> futureTest;
	@Before
	public void setUp() throws Exception {
		futureTest=new Future<>();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFuture() {
		assertEquals(false,this.futureTest.isDone());
	}

	@Test
	public void testGet() {
		assertEquals(false,this.futureTest.isDone());
		this.futureTest.resolve("ok!");
		assertEquals(true,this.futureTest.isDone());
		String result=this.futureTest.get();
		assertEquals(result, "ok!");
	}

	@Test
	public void testResolve() {
		assertEquals(false,this.futureTest.isDone());
		this.futureTest.resolve("ok!");
		assertEquals(true,this.futureTest.isDone());
	}

	@Test
	public void testIsDone() {
		assertEquals(false,this.futureTest.isDone());
		this.futureTest.resolve("ok!");
		assertEquals(true,this.futureTest.isDone());
	}

	@Test
	public void testGetLongTimeUnit() {
		assertEquals(false,this.futureTest.isDone());
		this.futureTest.resolve("ok!");
		assertEquals(true,this.futureTest.isDone());
		String result=this.futureTest.get();
		assertEquals(result, "ok!");
	}

}
