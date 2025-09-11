import { PieChart, Pie, Cell, Tooltip, Legend } from "recharts";
import { SalesStatsResponse } from "../../types/dashboard";

export default function SalesSection({ sales }: { sales: SalesStatsResponse }) {
    const salesData = [
        { name: "총 매출액", value: sales.totalSales },
        { name: "평균 결제 금액", value: sales.averagePayment },
    ];

    return (
        <div>
            <h3>매출 통계</h3>
            <p>총 매출액: {sales.totalSales.toLocaleString()}원</p>
            <p>총 결제 건수: {sales.totalPayments}건</p>
            <PieChart width={400} height={300}>
                <Pie dataKey="value" data={salesData} cx="50%" cy="50%" outerRadius={100}>
                    {salesData.map((_, index) => (
                        <Cell key={index} fill={index === 0 ? "#82ca9d" : "#8884d8"} />
                    ))}
                </Pie>
                <Tooltip />
                <Legend />
            </PieChart>
        </div>
    );
}
