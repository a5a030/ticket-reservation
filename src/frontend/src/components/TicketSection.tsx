import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    ResponsiveContainer,
} from "recharts";
import { VerificationStatsResponse } from "../../types/dashboard";

export default function TicketSection({ tickets }: { tickets: VerificationStatsResponse }) {
    const hourlyData = Object.entries(tickets.hourlyCounts).map(([hour, count]) => ({
        hour: Number(hour), // 숫자로 변환
        count,
    }));

    return (
        <div className="p-4 bg-white rounded-2xl shadow">
            <h2 className="text-lg font-bold mb-2">🎟️ 티켓 검증 통계</h2>
            <p className="mb-4 text-gray-600">
                성공률: <span className="font-semibold text-green-600">
                    {(tickets.successRate * 100).toFixed(2)}%
                </span>
            </p>

            <ResponsiveContainer width="100%" height={300}>
                <LineChart data={hourlyData}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="hour" tickFormatter={(h) => `${h}시`} />
                    <YAxis />
                    <Tooltip formatter={(value: number) => `${value.toLocaleString()} 건`} />
                    <Line
                        type="monotone"
                        dataKey="count"
                        stroke="#4F46E5"
                        strokeWidth={2}
                        dot={{ r: 3 }}
                    />
                </LineChart>
            </ResponsiveContainer>
        </div>
    );
}
