package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PointServiceTest {

	PointService pointService;

	UserPointTable userPointTable;

	PointHistoryTable pointHistoryTable;

	PointServiceTest() {
		this.userPointTable = new UserPointTable();
		this.pointHistoryTable = new PointHistoryTable();
		this.pointService = new PointService(userPointTable, pointHistoryTable);
	}

	long id = 1234;

	@Test
	void 최대_보유할_수_있는_포인트를_초과하여_충전_실패() throws InterruptedException {
		long point = 600;
		long amount = 500;
		assertThatThrownBy(() -> {
			long result = pointService.CalculateAmount(point, amount, TransactionType.CHARGE);
		}).isInstanceOf(RuntimeException.class);
	}

	@Test
	void 최소사용포인트_미만이므로_사용_실패() throws InterruptedException {
		long point = 200;
		long amount = 20;
		assertThatThrownBy(() -> {
			long result = pointService.CalculateAmount(point, amount, TransactionType.USE);
		}).isInstanceOf(RuntimeException.class);
	}

	@Test
	void 포인트_잔액이_부족하여_포인트_사용_실패() throws InterruptedException {
		long point = 200;
		long amount = 300;
		assertThatThrownBy(() -> {
			long result = pointService.CalculateAmount(point, amount, TransactionType.USE);
		}).isInstanceOf(RuntimeException.class);
	}
}
