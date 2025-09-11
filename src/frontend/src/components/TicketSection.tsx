import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip } from "recharts";
import { VerificationStatsResponse } from "../../types/dashboard";

export default function TicketSection({ tickets }: { tickets: VerificationStatsResponse }) {
    const hourlyData = Object.entries(tickets.hourlyCounts).map(([hour, count]) => ({
        hour,
        count,
    }));

    return (
        <div>
            <h3>티켓 검증 통계</h3>
            <p>성공률: {(tickets.successRate * 100).toFixed(2)}%</p>
            <LineChart width={600} height={300} data={hourlyData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="hour" />
                <YAxis />
                <Tooltip />
                <Line type="monotone" dataKey="count" stroke="#82ca9d" />
            </LineChart>
        </div>
    );
}
