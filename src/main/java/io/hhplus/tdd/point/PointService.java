package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointService {

    private UserPointTable userPointTable;
    private PointHistoryTable pointHistoryTable;

    @Autowired
    PointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    // 잔액 조회
    public UserPoint getPoint(long id) throws InterruptedException {
        UserPoint userPoint = userPointTable.selectById(id);
        return userPoint;
    }

    // 사용내역 조회
    public List<PointHistory> getPointHistory(long id) throws InterruptedException {
        List<PointHistory> userPointHistory = pointHistoryTable.selectAllByUserId(id);
        return userPointHistory;
    }

    // 잔액 계산 -> Test가능하도록 따로 메쏘드 분리
    public long CalculateAmount(long point, long amount, TransactionType transactionType) {
        long result = -1;
        if (transactionType == TransactionType.USE) {
            result = point - amount;
        } else if (transactionType == TransactionType.CHARGE) {
            result = point + amount;
        }
        if (result < 0) throw new RuntimeException();
        return result;
    }

    // 잔액 업데이트
    public UserPoint insertOrUpdate(long id, long amount, TransactionType transactionType) throws InterruptedException {
        UserPoint userPoint = userPointTable.selectById(id);
        long point = CalculateAmount(userPoint.point(), amount, transactionType);
        if (point >= 0) {
            userPoint = userPointTable.insertOrUpdate(id, point);
            pointHistoryTable.insert(id, amount, transactionType, System.currentTimeMillis());
        }
        return userPoint;
    }

}
