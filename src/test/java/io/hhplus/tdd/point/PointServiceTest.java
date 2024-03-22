package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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
	void 성공() throws InterruptedException {
		// 포인트 충전
		long amount = 300;

		UserPoint userPoint = pointService.insertOrUpdate(id, amount, TransactionType.CHARGE);

		assertEquals(userPoint.id(), id, "포인트 충전");
		assertEquals(userPoint.point(), amount, "포인트 충전");

		// 포인트 조회
		userPoint = pointService.getPoint(id);

		assertNotNull(userPoint, "포인트 조회");
		assertEquals(userPoint.point(), amount, "포인트 조회");

		// 포인트 사용
		long useAmount = 200;

		userPoint = pointService.insertOrUpdate(id, useAmount, TransactionType.USE);

		assertEquals(userPoint.id(), id, "포인트 사용");
		assertEquals(userPoint.point(), 100, "포인트 사용");

		// 포인트 내역 조회
		List<PointHistory> pointHistory = pointService.getPointHistory(id);

		assertEquals(pointHistory.size(), 2, "포인트 내역 조회");
		assertEquals(pointHistory.get(0).userId(), id, "포인트 내역 조회");
		assertEquals(pointHistory.get(0).type(), TransactionType.CHARGE, "포인트 내역 조회");
		assertEquals(pointHistory.get(0).amount(), 300, "포인트 내역 조회");
		assertEquals(pointHistory.get(1).userId(), id, "포인트 내역 조회");
		assertEquals(pointHistory.get(1).type(), TransactionType.USE, "포인트 내역 조회");
		assertEquals(pointHistory.get(1).amount(), 200, "포인트 내역 조회");
	}

	@Test
	void 등록되지_않은_아이디로_조회_시_빈_유저정보_생성_성공() throws InterruptedException {
		long ghostId = 1111;

		UserPoint userPoint = pointService.getPoint(ghostId);

		assertNotNull(userPoint);
		assertEquals(userPoint.id(), ghostId);
		assertEquals(userPoint.point(), 0);
	}

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
