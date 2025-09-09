import { useEffect, useState } from "react";
import {
    PieChart,
    Pie,
    Tooltip,
    ResponsiveContainer,
    Cell,
    Legend,
} from "recharts";

type SalesStats = {
    label: string;        // 장르명
    totalAmount: number;  // 총 매출액
    count: number;        // 결제 건수
};

const COLORS = ["#4F46E5", "#22C55E", "#F59E0B", "#EF4444", "#14B8A6", "#8B5CF6"];

export default function GenreSalesChart() {
    const [data, setData] = useState<SalesStats[]>([]);

    useEffect(() => {
        fetch("/admin/payments/revenue-by-genre")
            .then((res) => res.json())
            .then((json) => setData(json));
    }, []);

    return (
        <div className="p-4 bg-white rounded-2xl shadow">
            <h2 className="text-lg font-bold mb-4">🎼 장르별 매출 집계</h2>
            <ResponsiveContainer width="100%" height={400}>
                <PieChart>
                    <Pie
                        data={data}
                        dataKey="totalAmount"
                        nameKey="label"
                        cx="50%"
                        cy="50%"
                        outerRadius={120}
                        label={(entry) => `${entry.label} (${(entry.totalAmount / data.reduce((a, b) => a + b.totalAmount, 0) * 100).toFixed(1)}%)`}
                    >
                        {data.map((_, index) => (
                            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                        ))}
                    </Pie>
                    <Tooltip formatter={(value: number) => `${value.toLocaleString()} 원`} />
                    <Legend />
                </PieChart>
            </ResponsiveContainer>
        </div>
    );
}
