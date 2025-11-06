package com.example.demo.services;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.models.WasteDetail;
import com.example.demo.models.WasteRateResult;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;

@Service
public class FoodService {

    private final EntityManager entityManager;

    public FoodService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    
    /**
     * BigDecimalをdoubleに安全に変換するヘルパーメソッド
     */
    private double safeToDouble(Object value) {
        if (value instanceof java.math.BigDecimal) {
            return ((java.math.BigDecimal) value).doubleValue();
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }
    
    /**
     * MySQLの構文に合わせて期間絞り込み用のWHERE句を生成します。
     * F.waste_date を基準とします。
     * @param period "week", "month", "year" または "prev_week", "prev_month", "prev_year"
     */
    private String getDateFilter(String period) {
        if (period == null || "all".equals(period)) {
            return "";
        }
        
        String filter = "";
        String referenceDate = "CURDATE()";
        String interval = "";
        
        if (period.startsWith("prev_")) {
            String basePeriod = period.substring(5);
            
            if ("week".equals(basePeriod)) {
                referenceDate = "DATE_SUB(CURDATE(), INTERVAL DAYOFWEEK(CURDATE())-1 DAY)";
                interval = "7 DAY";
            } else if ("month".equals(basePeriod)) {
                referenceDate = "DATE_SUB(CURDATE(), INTERVAL DAYOFMONTH(CURDATE())-1 DAY)";
                interval = "1 MONTH";
            } else if ("year".equals(basePeriod)) {
                referenceDate = "DATE_SUB(CURDATE(), INTERVAL DAYOFYEAR(CURDATE())-1 DAY)";
                interval = "1 YEAR";
            }
            
            // 期間の始まり: DATE_SUB(開始日, 1期間) -> 例: 前月の1日
            String startDateSql = String.format("DATE_SUB(%s, INTERVAL %s)", referenceDate, interval);
            
            // 期間の終わり: 基準日の前日 -> 例: 今月の1日の前日(前月の末日)
            String endDateSql = String.format("DATE_SUB(%s, INTERVAL 1 DAY)", referenceDate);

            // 最終フィルター: 前期間の開始日から前期間の終了日まで
            filter = String.format("AND F.waste_date >= %s AND F.waste_date <= %s ", startDateSql, endDateSql);
            
        } else {
            // 現在の期間 (week, month, year)
            switch (period) {
                case "week":
                    filter = "AND F.waste_date >= DATE_SUB(CURDATE(), INTERVAL DAYOFWEEK(CURDATE())-1 DAY) ";
                    break;
                case "month":
                    filter = "AND F.waste_date >= DATE_SUB(CURDATE(), INTERVAL DAYOFMONTH(CURDATE())-1 DAY) ";
                    break;
                case "year":
                    filter = "AND F.waste_date >= DATE_SUB(CURDATE(), INTERVAL DAYOFYEAR(CURDATE())-1 DAY) ";
                    break;
                default:
                    filter = "";
            }
        }
        
        return filter;
    }

    /**
     * ジャンルごとの廃棄率を集計して取得します。
     */
    @SuppressWarnings("unchecked")
    public List<WasteRateResult> getWasteRates(String period, Integer userId) {
        String wasteFilter = getDateFilter(period);

        String sql = String.format("""
            SELECT 
                FG.food_genre_name,
                (SUM(CASE 
                    WHEN F.flag = 3 %s 
                    THEN F.food_amount * COALESCE(F.bunshi, 1) / COALESCE(F.bunbo, 1) 
                    ELSE 0 
                END) * 100.0 
                
                / 
                
                SUM(F.food_amount * COALESCE(F.bunshi, 1) / COALESCE(F.bunbo, 1))) AS waste_rate
            FROM
                food_genre FG
            JOIN
                food F ON FG.food_genre_id = F.food_genre_id
            WHERE F.user_id = :userId
            GROUP BY
                FG.food_genre_name
            HAVING
                SUM(F.food_amount * COALESCE(F.bunshi, 1) / COALESCE(F.bunbo, 1)) > 0
            ORDER BY
                waste_rate DESC
        """, wasteFilter); 

        try {
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter("userId", userId);
            List<Object[]> results = query.getResultList();
            
            return results.stream()
                .map(row -> new WasteRateResult((String) row[0], safeToDouble(row[1])))
                .toList();
        } catch (PersistenceException e) {
            System.err.println("Database Error during getWasteRates: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * 廃棄された個々の食品の詳細を取得します。（廃棄トレンド表示に使用）
     */
    @SuppressWarnings("unchecked")
    public List<WasteDetail> getWasteDetails(String period, Integer userId) {
        String dateFilter = getDateFilter(period);

        String sql = String.format("""
            SELECT 
                F.food_name, 
                (F.food_amount * COALESCE(F.bunshi, 1) / COALESCE(F.bunbo, 1)),
                FG.food_genre_name
            FROM
                food F
            JOIN
                food_genre FG ON F.food_genre_id = FG.food_genre_id
            WHERE
                F.flag = 3 AND F.user_id = :userId %s 
            ORDER BY
                FG.food_genre_name, 2 DESC
        """, dateFilter);
        
        try {
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter("userId", userId);
            List<Object[]> results = query.getResultList();
            
            return results.stream()
                .map(row -> {
                    String foodName = (String) row[0];
                    double wasteAmount = safeToDouble(row[1]);
                    String genreName = (String) row[2];
                    
                    return new WasteDetail(foodName, wasteAmount, genreName, 0.0);
                })
                .toList();
        } catch (PersistenceException e) {
            System.err.println("Database Error during getWasteDetails: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 食材ごとの廃棄率ランキングを取得します。（全期間の廃棄トレンド表示に使用）
     */
    @SuppressWarnings("unchecked")
    public List<WasteDetail> getWasteRankings(Integer userId) {
        String sql = """
            SELECT 
                F.food_name,
                FG.food_genre_name,
                SUM(CASE WHEN F.flag = 3 THEN F.food_amount * COALESCE(F.bunshi, 1) / COALESCE(F.bunbo, 1) ELSE 0 END) AS total_waste,
                SUM(F.food_amount * COALESCE(F.bunshi, 1) / COALESCE(F.bunbo, 1)) AS total_purchase,
                (SUM(CASE WHEN F.flag = 3 THEN F.food_amount * COALESCE(F.bunshi, 1) / COALESCE(F.bunbo, 1) ELSE 0 END) * 100.0 
                 / SUM(F.food_amount * COALESCE(F.bunshi, 1) / COALESCE(F.bunbo, 1))) AS waste_rate
            FROM
                food F
            JOIN
                food_genre FG ON F.food_genre_id = FG.food_genre_id
            WHERE F.user_id = :userId
            GROUP BY
                F.food_name, FG.food_genre_name
            HAVING
                total_waste > 0
            ORDER BY
                waste_rate DESC, total_waste DESC
        """;

        try {
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter("userId", userId);
            List<Object[]> results = query.getResultList();
            
            return results.stream()
                .map(row -> {
                    String foodName = (String) row[0];
                    String genreName = (String) row[1];
                    double wasteAmount = safeToDouble(row[2]); 
                    double wasteRate = safeToDouble(row[4]); 
                    
                    return new WasteDetail(foodName, wasteAmount, genreName, wasteRate); 
                })
                .toList();
        } catch (PersistenceException e) {
            System.err.println("Database Error during getWasteRankings: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    
    @SuppressWarnings("unchecked")
    public double getTotalWasteAmount(String period, Integer userId) {
        String dateFilter = getDateFilter(period);

        // flag=3 (廃棄) のもののみを合計
        String sql = String.format("""
            SELECT 
                SUM(CASE 
                    WHEN F.flag = 3 
                    THEN F.food_amount * COALESCE(F.bunshi, 1) / COALESCE(F.bunbo, 1) 
                    ELSE 0 
                END)
            FROM
                food F
            WHERE F.user_id = :userId %s
        """, dateFilter);
        
        try {
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter("userId", userId);
            
            Object result = query.getSingleResult();
            
            if (result == null) {
                return 0.0;
            }
            return safeToDouble(result);
        } catch (PersistenceException e) {
            System.err.println("Database Error during getTotalWasteAmount: " + e.getMessage());
            e.printStackTrace();
            return 0.0;
        }
    }
}