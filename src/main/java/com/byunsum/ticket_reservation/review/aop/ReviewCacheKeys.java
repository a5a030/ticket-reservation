package com.byunsum.ticket_reservation.review.aop;

public final class ReviewCacheKeys {
    private ReviewCacheKeys() {}

    //ReviewAdminService에서 쓰는 키와 동일
    public static String dashboardKey(Long performanceId) {
        return "dashboard::" + performanceId;
    }

    //KeywordService에서 쓰는 키와 동일
    public static String keywordsKey(Long performanceId) {
        return "review:keywords:" + performanceId;
    }
}
