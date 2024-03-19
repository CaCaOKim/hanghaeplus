package io.hhplus.tdd.point;

import io.hhplus.tdd.TddApplication;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PointControllerTest {

	PointController pointController;

	PointService pointService;

	UserPointTable userPointTable;

	PointHistoryTable pointHistoryTable;

	PointControllerTest() {
		this.userPointTable = new UserPointTable();
		this.pointHistoryTable = new PointHistoryTable();
		this.pointService = new PointService(userPointTable, pointHistoryTable);
		this.pointController = new PointController(this.pointService);
	}

	long id = 1234;

	@Test
	void 성공_Controller() throws InterruptedException {
		// 포인트 충전
		long amount = 300;

		UserPoint userPoint = pointController.charge(id, amount);

		assertEquals(userPoint.id(), id, "포인트 충전");
		assertEquals(userPoint.point(), amount, "포인트 충전");

		// 포인트 조회
		userPoint = pointController.point(id);

		assertEquals(userPoint.point(), amount, "포인트 조회");

		// 포인트 사용
		long useAmount = 200;

		userPoint = pointController.use(id, useAmount);

		assertEquals(userPoint.id(), id, "포인트 사용");
		assertEquals(userPoint.point(), 100, "포인트 사용");

		// 포인트 내역 조회
		List<PointHistory> pointHistory = pointController.history(id);

		assertEquals(pointHistory.size(), 2, "포인트 내역 조회");
		assertEquals(pointHistory.get(0).userId(), id, "포인트 내역 조회");
		assertEquals(pointHistory.get(0).type(), TransactionType.CHARGE, "포인트 내역 조회");
		assertEquals(pointHistory.get(0).amount(), 300, "포인트 내역 조회");
		assertEquals(pointHistory.get(1).userId(), id, "포인트 내역 조회");
		assertEquals(pointHistory.get(1).type(), TransactionType.USE, "포인트 내역 조회");
		assertEquals(pointHistory.get(1).amount(), 200, "포인트 내역 조회");
	}

	@Test
	void 포인트_잔액이_부족하여_포인트_사용_실패_Controller() throws InterruptedException {
		// 포인트 충전
		UserPoint userPoint = pointController.charge(id, 200);

		assertThatThrownBy(() -> {
			// 포인트 사용
			pointController.use(userPoint.id(), 300);
		}).isInstanceOf(RuntimeException.class);
	}

	@Test
	void 포인트_잔액이_부족하여_포인트_사용_실패_Service() throws InterruptedException {
		assertThatThrownBy(() -> {
			long result = pointService.CalculateAmount(200, 300, TransactionType.USE);
		}).isInstanceOf(RuntimeException.class);
	}

}
