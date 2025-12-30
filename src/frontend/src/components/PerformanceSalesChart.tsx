import { useEffect, useState } from "react";
import {
    BarChart,
    Bar,
    XAxis,
    YAxis,
    Tooltip,
    CartesianGrid,
    ResponsiveContainer,
    Legend,
} from "recharts";

type SalesStats = {
    label: string;        // 공연명
    totalAmount: number;  // 총 매출액
    count: number;        // 결제 건수
};

export default function PerformanceSalesChart() {
    const [data, setData] = useState<SalesStats[]>([]);

    useEffect(() => {
        fetch("/admin/payments/statistics/performance") // ✅ 엔드포인트 수정
            .then((res) => res.json())
            .then((json) => setData(json))
            .catch((err) => console.error("공연별 매출 불러오기 실패:", err));
    }, []);

    return (
        <div className="p-4 bg-white rounded-2xl shadow">
            <h2 className="text-lg font-bold mb-4">🎭 공연별 매출 집계</h2>
            <ResponsiveContainer width="100%" height={400}>
                <BarChart
                    data={data}
                    margin={{ top: 20, right: 30, left: 20, bottom: 80 }}
                >
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="label" angle={-45} textAnchor="end" interval={0} />
                    <YAxis />
                    <Tooltip
                        formatter={(value: number, name: string) =>
                            name === "총 매출액"
                                ? `${value.toLocaleString()} 원`
                                : `${value.toLocaleString()} 건`
                        }
                    />
                    <Legend />
                    <Bar
                        dataKey="totalAmount"
                        name="총 매출액"
                        fill="#4F46E5"
                        radius={[6, 6, 0, 0]}
                    />
                    <Bar
                        dataKey="count"
                        name="결제 건수"
                        fill="#22C55E"
                        radius={[6, 6, 0, 0]}
                    />
                </BarChart>
            </ResponsiveContainer>
        </div>
    );
}
