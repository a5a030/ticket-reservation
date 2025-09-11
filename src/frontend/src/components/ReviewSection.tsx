import { BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid } from "recharts";
import { ReviewStatsResponse } from "../../types/dashboard";

export default function ReviewSection({ reviews }: { reviews: ReviewStatsResponse }) {
    const keywordData = Object.entries(reviews.topKeywords).map(([k, v]) => ({
        name: k,
        count: v,
    }));

    return (
        <div>
            <h3>리뷰 통계</h3>
            <p>총 리뷰 수: {reviews.totalReviews}</p>
            <p>평균 점수: {reviews.averageScore.toFixed(2)}</p>
            <BarChart width={500} height={300} data={keywordData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis />
                <Tooltip />
                <Bar dataKey="count" fill="#8884d8" />
            </BarChart>
        </div>
    );
}
