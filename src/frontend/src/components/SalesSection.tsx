import {
    PieChart,
    Pie,
    Cell,
    Tooltip,
    Legend,
    ResponsiveContainer,
} from "recharts";
import { SalesStatsResponse } from "../../types/dashboard";

export default function SalesSection({ sales }: { sales: SalesStatsResponse }) {
    const salesData = [
        { name: "총 매출액", value: sales.totalSales },
        { name: "평균 결제 금액", value: sales.averagePayment },
    ];

    return (
        <div className="p-4 bg-white rounded-2xl shadow">
            <h2 className="text-lg font-bold mb-2">📊 매출 통계</h2>
            <p className="text-gray-700">
                총 매출액:{" "}
                <span className="font-semibold text-indigo-600">
                    {sales.totalSales.toLocaleString()} 원
                </span>
            </p>
            <p className="text-gray-700 mb-4">
                총 결제 건수:{" "}
                <span className="font-semibold text-green-600">
                    {sales.totalPayments.toLocaleString()} 건
                </span>
            </p>

            <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                    <Pie
                        data={salesData}
                        dataKey="value"
                        nameKey="name"
                        cx="50%"
                        cy="50%"
                        outerRadius={100}
                        label
                    >
                        {salesData.map((_, index) => (
                            <Cell
                                key={index}
                                fill={index === 0 ? "#4F46E5" : "#22C55E"}
                            />
                        ))}
                    </Pie>
                    <Tooltip formatter={(value: number) => `${value.toLocaleString()} 원`} />
                    <Legend />
                </PieChart>
            </ResponsiveContainer>
        </div>
    );
}
