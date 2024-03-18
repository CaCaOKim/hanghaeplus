package io.hhplus.tdd;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TddApplicationTests {

	TddApplication tddApplication;

	@Test
	void contextLoads() {
		int a = 2;
		int b = 2;
		assertEquals(a, b);
	}

	@Test
	void test2() {
		int a = 1;
		int b = 1;
		assertEquals(a, b);
	}

}
