package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointService {

    private UserPointTable userPointTable;
    private PointHistoryTable pointHistoryTable;

    public UserPoint getPoint(Long id) throws InterruptedException {
        UserPoint userPoint = userPointTable.selectById(id);
        return userPoint;
    }

    public List<PointHistory> getPointHistory(Long id) throws InterruptedException {
        List<PointHistory> userPointHistory = pointHistoryTable.selectAllByUserId(id);
        return userPointHistory;
    }

    public UserPoint chargePoint(Long id, Long point, Long amount) throws InterruptedException {
        UserPoint userPoint = userPointTable.selectById(id);
        return new UserPoint(userPoint.id(), userPoint.point() + amount, System.currentTimeMillis());
    }

    public UserPoint usePoint(Long id, Long point, Long amount) throws InterruptedException {
        UserPoint newUserPoint = null;
        if (point >= amount) {
            newUserPoint = new UserPoint(id, point - amount, System.currentTimeMillis());
        }
        return newUserPoint;
    }

    public PointHistory insertHistory(Long id, Long amount, TransactionType transactionType) throws InterruptedException {
        PointHistory pointHistory = pointHistoryTable.insert(id, amount, transactionType, System.currentTimeMillis());
        return pointHistory;
    }

    public UserPoint insertOrUpdate(Long id, Long amount, TransactionType transactionType) throws InterruptedException {
        UserPoint userPoint = userPointTable.selectById(id);
        UserPoint result = null;
        if (transactionType == TransactionType.USE) {
            this.chargePoint(id, userPoint.point(), amount);
            this.insertHistory(id, amount, transactionType);
        } else if (transactionType == TransactionType.CHARGE) {
            this.chargePoint(id, userPoint.point(), amount);
            this.insertHistory(id, amount, transactionType);
        }
        return result;
    }

}
