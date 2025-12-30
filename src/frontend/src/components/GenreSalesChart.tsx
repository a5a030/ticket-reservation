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
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        fetch("/admin/payments/statistics/genre")
            .then((res) => {
                if (!res.ok) throw new Error("장르별 매출 데이터를 불러올 수 없습니다.");
                return res.json();
            })
            .then((json) => setData(json))
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false));
    }, []);

    if (loading) return <div>Loading...</div>;
    if (error) return <div className="text-red-500">{error}</div>;
    if (data.length === 0) return <div className="text-gray-500">데이터가 없습니다.</div>;

    const total = data.reduce((sum, item) => sum + item.totalAmount, 0);

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
                        label={(entry) =>
                            total > 0
                                ? `${entry.label} (${((entry.totalAmount / total) * 100).toFixed(1)}%)`
                                : `${entry.label} (0%)`
                        }
                    >
                        {data.map((_, index) => (
                            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                        ))}
                    </Pie>
                    <Tooltip
                        formatter={(value: number, _, entry: any) => {
                            const percent = total > 0 ? ((entry.payload.totalAmount / total) * 100).toFixed(1) : "0";
                            return [`${value.toLocaleString()} 원 (${percent}%)`, entry.payload.label];
                        }}
                    />
                    <Legend />
                </PieChart>
            </ResponsiveContainer>
        </div>
    );
}
